package org.motechproject.ananya.seed;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.seed.service.CourseItemMeasureSeedService;
import org.motechproject.ananya.service.SendSMSService;
import org.motechproject.deliverytools.seed.Seed;
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

    }
}
