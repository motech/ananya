package org.motechproject.ananya.seed;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.domain.measure.CourseItemMeasure;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.seed.service.CourseItemMeasureSeedService;
import org.motechproject.ananya.service.SendSMSService;
import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CertificateCourseDataCorrectionSeed {

    class ChapterScore {

        private String chapterIndex;
        private int score;

        ChapterScore(String chapterIndex, int score) {
            this.chapterIndex = chapterIndex;
            this.score = score;
        }

        public String getChapterIndex() {
            return chapterIndex;
        }

        public void setChapterIndex(String chapterIndex) {
            this.chapterIndex = chapterIndex;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ChapterScore that = (ChapterScore) o;

            if (chapterIndex != null ? !chapterIndex.equals(that.chapterIndex) : that.chapterIndex != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return chapterIndex != null ? chapterIndex.hashCode() : 0;
        }
    }

    class QuizInformation
    {
        private String chapterIndex;

        private String type;

        private int score;

        private DateTime timestamp;

        public QuizInformation(Object[] row) {
            chapterIndex = getChapterIndexFromCourseItemDimension((String)row[1]).toString();
            type = (String)row[3];
            if (row[4] == null) score = -1; else score = (Integer)row[4];
//            if (row[5] != null) timestamp = DateTime.parse(row[5].toString());
        }
    }

    private static Logger log = LoggerFactory.getLogger(CertificateCourseDataCorrectionSeed.class);

    @Autowired
    private CourseItemMeasureSeedService courseItemMeasureSeedService;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private SendSMSService sendSMSService;

    private DateTime startDate = new DateTime(2012, 05, 23, 0, 0);
    private DateTime endDate = new DateTime(2012, 06, 2, 0, 0);
    private int startTimeId = 144;
    private int endTimeId = 153;

    private Map<String, List<Integer>> flwChaptersMap = new HashMap<String, List<Integer>>();

    private Map<String, List<ChapterScore>> flwScoresMap = new HashMap<String, List<ChapterScore>>();

    private Map<String, List<QuizInformation>> flwQuizHistoryMap = new HashMap<String, List<QuizInformation>>();

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-tool.xml");
        CertificateCourseDataCorrectionSeed certificateCourseDataCorrectionSeed =
                (CertificateCourseDataCorrectionSeed) context.getBean("certificateCourseDataCorrectionSeed");
        certificateCourseDataCorrectionSeed.correctFlwScoresData();
    }

    private Integer getChapterIndexFromCourseItemDimension(String str) {
        return Integer.parseInt(str.substring(8, 9)) - 1;
    }

    @Seed(priority = 0, version = "1.4", comment = "Correct incorrect scores data for certificate course.")
    public void correctFlwScoresData() {
        System.out.println("Correction FLW Scores data.");

        System.out.println("Fetching course ite measure history.");
        List flwCourseHistory = courseItemMeasureSeedService.fetchFlwCourseHistory();

        System.out.println("Creating FLW QUIZ history map");
        Iterator iterator = flwCourseHistory.iterator();
        while(iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            String msisdn = row[0].toString();
            QuizInformation quizInformation = new QuizInformation(row);

            if (flwQuizHistoryMap.containsKey(msisdn)) {
                flwQuizHistoryMap.get(msisdn).add(quizInformation);
            } else {
                List<QuizInformation> quizInformationList = new ArrayList<QuizInformation>();
                quizInformationList.add(quizInformation);
                flwQuizHistoryMap.put(msisdn, quizInformationList);
            }
        }

        for (String msisdn : flwQuizHistoryMap.keySet()) {
            System.out.println("Correcting the score for FLW with msisdn : " + msisdn);

            FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
            List<QuizInformation> quizInformationList = flwQuizHistoryMap.get(msisdn);

            ReportCard reportCard = new ReportCard();
            ReportCard oldReportCard = null;

            boolean modified = false;
            int oldScore = frontLineWorker.reportCard().totalScore();
            List<Score> scores = frontLineWorker.reportCard().scores();
            for (Score score : scores) {
                String callId = score.getCallId();
                String[] parts = callId.split("-");
                String callIdTime = parts[1];

                DateTime timeOfCall = new DateTime(Long.parseLong(callIdTime));
                if (timeOfCall.getMillis() >= startDate.getMillis() &&
                    timeOfCall.getMillis() < endDate.getMillis()) {
                    score.setResult(!score.result());
                    modified = true;
                }
            }
            int newScore = frontLineWorker.reportCard().totalScore();

            if (modified) {
                System.out.println("Updating FLW : " + msisdn + " oldScore - " + oldScore + " newScore - " + newScore);
                allFrontLineWorkers.update(frontLineWorker);
            }

            if (frontLineWorker.currentCourseAttempt() > 0) {
                System.out.println("Checking FLW " + msisdn + " who has completed the course.");
                int previousChapterIndex = -1;
                int counter = 0;
                for (QuizInformation quizInformation : quizInformationList) {
                    counter++;
                    if (quizInformation.type.equalsIgnoreCase("END")) {
                        reportCard.clearScoresForChapterIndex(quizInformation.chapterIndex);
                        for (int i = 0; i < quizInformation.score; i++) {
                            reportCard.addScore(new Score(quizInformation.chapterIndex, "" + i, true, ""));
                        }
                        for (int i = quizInformation.score; i < 4; i++) {
                            reportCard.addScore(new Score(quizInformation.chapterIndex, "" + i, false, ""));
                        }
                    }
                    if (Integer.parseInt(quizInformation.chapterIndex) < previousChapterIndex ||
                            (quizInformation.chapterIndex.equalsIgnoreCase("8") &&
                             quizInformation.type.equalsIgnoreCase("END") &&
                             counter == quizInformationList.size())) {
                        System.out.println("Switching attempts at " + quizInformation.chapterIndex + " " +
                                quizInformation.type + " previousChapterIndex is " + previousChapterIndex +
                                " FLW msisdn is : " + msisdn);
                        if (reportCard.totalScore() > FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE) {
                            System.out.println("FLW " + msisdn + "had passed course. Sending FLW an SMS");
                            sendSMSService.buildAndSendSMS(
                                    frontLineWorker.getMsisdn(),
                                    frontLineWorker.getLocationId(),
                                    frontLineWorker.currentCourseAttempt());
                        }

                        oldReportCard = reportCard;
                        reportCard = new ReportCard();
                    }
                    previousChapterIndex = Integer.parseInt(quizInformation.chapterIndex);
                }
            }
        }

//            int previousChapterIndex = -1;
//            for (QuizInformation quizInformation : quizInformationList) {
//                if (quizInformation.type.equalsIgnoreCase("END")) {
//                    reportCard.clearScoresForChapterIndex(quizInformation.chapterIndex);
//                    for (int i = 0; i < quizInformation.score; i++) {
//                        reportCard.addScore(new Score(quizInformation.chapterIndex, "" + i, true, ""));
//                    }
//                    for (int i = quizInformation.score; i < 4; i++) {
//                        reportCard.addScore(new Score(quizInformation.chapterIndex, "" + i, false, ""));
//                    }
//                }
//                if (Integer.parseInt(quizInformation.chapterIndex) < previousChapterIndex) {
//                    System.out.println("Switching attempts at " + quizInformation.chapterIndex + " " +
//                            quizInformation.type + " previousChapterIndex is " + previousChapterIndex +
//                            " FLW msisdn is : " + msisdn);
//                    oldReportCard = reportCard;
//                    reportCard = new ReportCard();
//                }
//                previousChapterIndex = Integer.parseInt(quizInformation.chapterIndex);
//            }
//
//            if (oldReportCard != null) {
//                if (frontLineWorker.currentCourseAttempt() <= 0) {
//                    System.out.println("oldReportCard is not null but attempts shown as 0. BAD! FLW msisdn is : " + msisdn);
//                } else {
//                    System.out.println("FLW " + msisdn + " has a full certificate course attempt.");
//                }
//
//                if (oldReportCard.totalScore() > FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE) {
//                    System.out.println("Front Line Worker with msisdn " + msisdn + " has passed the course. Send SMS!");
//                }
//            }
//
//            Map<String, Integer> currentScoresInCouch = frontLineWorker.reportCard().scoresByChapterIndex();
//            Map<String, Integer> scoresInPostgres = reportCard.scoresByChapterIndex();
//
//            for(int currentChapterIndex = 0; currentChapterIndex < 9; currentChapterIndex++) {
//                Integer couchScore = currentScoresInCouch.get("" + currentChapterIndex);
//                Integer postgresScore = scoresInPostgres.get("" + currentChapterIndex);
//
//                if (couchScore != null && postgresScore == null) {
//                    Collection<Score> scores = frontLineWorker.reportCard().getScoresForChapter("" + currentChapterIndex);
//                    for (Score score : scores) {
//                        CallDurationMeasure callDurationMeasure =
//                                courseItemMeasureSeedService.fetchCallDurationMeasureForCallId(score.getCallId());
//                        if (callDurationMeasure.getTimeDimension().getId() >= startTimeId &&
//                            callDurationMeasure.getTimeDimension().getId() <= endTimeId) {
//                            System.out.println("Call not recorded in postgres Cours");
//                        }
//                    }
//                }
//
//                if (couchScore != postgresScore) {
//                    System.out.println("Score for FLW with msisdn " + msisdn + " for chapter " + currentChapterIndex +
//                            " in couch is " + couchScore + " in postgres is " + postgresScore);
//                }
//            }
    }

    public void correctScoresData() {

        startDate = DateUtil.newDate(2012, 5, 23).toDateTimeAtStartOfDay();

        List<CourseItemMeasure> courseItemMeasureList =
                courseItemMeasureSeedService.fetchQuizEndMeasuresBetweenDates(startDate, DateTime.now());

        List<ChapterScore> flwScores;
        String msisdn;
        String currentChapter;
        for(CourseItemMeasure courseItemMeasure : courseItemMeasureList) {
            msisdn = courseItemMeasure.getFrontLineWorkerDimension().getMsisdn().toString();
            currentChapter = getChapterIndexFromCourseItemDimension(courseItemMeasure.getCourseItemDimension().getName()).toString();

            if (flwScoresMap.containsKey(msisdn))
                flwScores = flwScoresMap.get(msisdn);
            else {
                flwScores = new ArrayList<ChapterScore>();
                flwScoresMap.put(msisdn, flwScores);
            }

            int index;
            ChapterScore chapterScore = new ChapterScore(currentChapter, courseItemMeasure.getScore());
            if ((index = flwScores.indexOf(new ChapterScore(currentChapter, -1))) != -1)
                flwScores.set(index, chapterScore);
            else
                flwScores.add(chapterScore);
        }

        FrontLineWorker frontLineWorker;
        List<ChapterScore> chapterScoresMap;
        Map<String, Integer> currentScoresInCouch;
        Integer scoreInCouch;
        int incorrectNumbersCount = 0;
        for (String checkMsisdn : flwScoresMap.keySet()) {
            frontLineWorker = allFrontLineWorkers.findByMsisdn(checkMsisdn);
            currentScoresInCouch = frontLineWorker.reportCard().scoresByChapterIndex();

            chapterScoresMap = flwScoresMap.get(checkMsisdn);
            for (ChapterScore chapterScore : chapterScoresMap) {
                scoreInCouch = currentScoresInCouch.get(chapterScore.getChapterIndex());
                if (scoreInCouch == null || chapterScore.getScore() != scoreInCouch) {
                    System.out.println("Score in postgres : " + chapterScore.getScore() +
                            " Score in couch : " + scoreInCouch +
                            " FLW with msisdn " + checkMsisdn +
                            " Chapter is : " + chapterScore.getChapterIndex() +
                            " couch id : " + frontLineWorker.getId());
                    incorrectNumbersCount++;
                }
            }
        }

        System.out.println("Incorrect FLWs in the database are : " + incorrectNumbersCount);
    }

    public void correctData() {
        log.info("Correcting error FLW data : ");

        startDate = DateUtil.newDate(2012, 5, 23).toDateTimeAtStartOfDay();
        endDate = DateUtil.newDate(2012, 6, 1).toDateTimeAtStartOfDay();

        log.info("Correction start date (inclusive) is : " + startDate);
        log.info("Correction end date (inclusive) is : " + endDate);

        List<CourseItemMeasure> incorrectCalls =
                courseItemMeasureSeedService.fetchQuizStartMeasuresBetweenDates(startDate, endDate);
        List<CourseItemMeasure> correctCallsAfterBug =
                courseItemMeasureSeedService.fetchQuizStartMeasuresBetweenDates(endDate.plusDays(1), DateTime.now());

        log.info("Fetched incorrect Measures (count) : " + incorrectCalls.size());
        log.info("Fetched correct calls after bug (count) : " + correctCallsAfterBug.size());

        // add incorrect chaptersForFLW to the hash
        List<Integer> chaptersForFLW;
        String msisdn;
        Integer currentChapter;
        for (CourseItemMeasure courseItemMeasure : incorrectCalls) {
            msisdn = courseItemMeasure.getFrontLineWorkerDimension().getMsisdn().toString();

            if (!flwChaptersMap.containsKey(msisdn)) {
                chaptersForFLW = new ArrayList<Integer>();
                flwChaptersMap.put(msisdn, chaptersForFLW);
            } else {
                chaptersForFLW = flwChaptersMap.get(msisdn);
            }

            currentChapter = getChapterIndexFromCourseItemDimension(courseItemMeasure.getCourseItemDimension().getName());
            if (!chaptersForFLW.contains(currentChapter)) chaptersForFLW.add(currentChapter);

            log.info("Generated incorrect chapters for msisdn : " + msisdn + " chapters - " + chaptersForFLW);
        }

        // remove freshly called chaptersForFLW from the hash
        for (CourseItemMeasure courseItemMeasure : correctCallsAfterBug) {
            msisdn = courseItemMeasure.getFrontLineWorkerDimension().getMsisdn().toString();
            currentChapter = getChapterIndexFromCourseItemDimension(courseItemMeasure.getCourseItemDimension().getName());

            if (flwChaptersMap.containsKey(msisdn)) {
                chaptersForFLW = flwChaptersMap.get(msisdn);
                if (chaptersForFLW.contains(currentChapter)) {
                    chaptersForFLW.remove(currentChapter);
                    log.info("Removing chapter " + currentChapter + " from msisdn " + msisdn + " 's incorrect chapter list");
                }
            }
        }

        FrontLineWorker dirtyFrontLineWorker;
        Collection<Score> dirtyScoreCollection;
        for (String dirtyMsisdn : flwChaptersMap.keySet()) {
            dirtyFrontLineWorker = allFrontLineWorkers.findByMsisdn(dirtyMsisdn);
            chaptersForFLW = flwChaptersMap.get(dirtyMsisdn);

            Integer oldScore = dirtyFrontLineWorker.reportCard().totalScore();
            log.info("msisdn " + dirtyMsisdn + " : incorrect score is - " + oldScore);

            for (Integer dirtyChapter : chaptersForFLW) {
                dirtyScoreCollection = dirtyFrontLineWorker.reportCard().getScoresForChapter(dirtyChapter.toString());
                for (Score score : dirtyScoreCollection) {
                    score.setResult(!score.result());
                }
            }

            Integer newScore = dirtyFrontLineWorker.reportCard().totalScore();
            log.info("msisdn " + dirtyMsisdn + " : corrected score is - " + newScore);

            if (oldScore < FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE &&
                newScore >= FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE) {
                log.info("Sending SMS to msisdn : " + dirtyMsisdn);
                sendSMSService.buildAndSendSMS(
                        dirtyFrontLineWorker.getMsisdn(),
                        dirtyFrontLineWorker.getLocationId(),
                        dirtyFrontLineWorker.currentCourseAttempt());
            }

            allFrontLineWorkers.update(dirtyFrontLineWorker);
            log.info("Update FLW with msisdn : " + dirtyMsisdn + "  " + dirtyFrontLineWorker);
        }

    }

}
