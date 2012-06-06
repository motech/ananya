package org.motechproject.ananya.seed;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
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

    private static Logger log = LoggerFactory.getLogger(CertificateCourseDataCorrectionSeed.class);

    @Autowired
    private CourseItemMeasureSeedService courseItemMeasureSeedService;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private SendSMSService sendSMSService;

    private DateTime startDate;

    private DateTime endDate;

    private Map<String, List<Integer>> flwChaptersMap = new HashMap<String, List<Integer>>();

    private Map<String, List<ChapterScore>> flwScoresMap = new HashMap<String, List<ChapterScore>>();

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-tool.xml");
        CertificateCourseDataCorrectionSeed certificateCourseDataCorrectionSeed =
                (CertificateCourseDataCorrectionSeed) context.getBean("certificateCourseDataCorrectionSeed");
        certificateCourseDataCorrectionSeed.correctScoresData();
    }

    @Seed(priority = 0, version = "1.0", comment = "Correct scores")
    public void correctScoresData() {

        startDate = DateUtil.newDate(2012, 5, 23).toDateTimeAtStartOfDay();

        List<CourseItemMeasure> courseItemMeasureList =
                courseItemMeasureSeedService.fetchQuizEndMeasuresBetweenDates(startDate, DateTime.now());

        List<ChapterScore> flwScores;
        String msisdn;
        String currentChapter;
        for(CourseItemMeasure courseItemMeasure : courseItemMeasureList) {
            msisdn = courseItemMeasure.getFrontLineWorkerDimension().getMsisdn().toString();
            currentChapter = getChapterIndexFromCourseItemDimension(courseItemMeasure.getCourseItemDimension()).toString();

            if (flwScoresMap.containsKey(msisdn))
                flwScores = flwScoresMap.get(msisdn);
            else
                flwScores = new ArrayList<ChapterScore>();

            int index;
            ChapterScore chapterScore = new ChapterScore(currentChapter, courseItemMeasure.getScore());
            if ((index = flwScores.indexOf(currentChapter)) != -1)
                flwScores.set(index, chapterScore);
            else
                flwScores.add(chapterScore);
        }

        FrontLineWorker frontLineWorker;
        List<ChapterScore> chapterScoresMap;
        Map<String, Integer> currentScoresInCouch;
        int incorrectNumbersCount = 0;
        for (String checkMsisdn : flwScoresMap.keySet()) {
            frontLineWorker = allFrontLineWorkers.findByMsisdn(checkMsisdn);
            currentScoresInCouch = frontLineWorker.reportCard().scoresByChapterIndex();

            chapterScoresMap = flwScoresMap.get(checkMsisdn);
            for (ChapterScore chapterScore : chapterScoresMap) {
                if (chapterScore.getScore() != currentScoresInCouch.get(chapterScore.getChapterIndex())) {
                    System.out.println("Score in postgres : " + chapterScore.getScore() +
                            " Score in couch : " + currentScoresInCouch.get(chapterScore.getChapterIndex()) +
                            " FLW with msisdn " + checkMsisdn);
                    incorrectNumbersCount++;
                }
            }
        }

        System.out.println("Incorrect FLWs in the database are : " + incorrectNumbersCount);
    }

    @Seed(priority = 0, version = "1.0", comment = "Correct certificate course scores for FLWs")
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

            currentChapter = getChapterIndexFromCourseItemDimension(courseItemMeasure.getCourseItemDimension());
            if (!chaptersForFLW.contains(currentChapter)) chaptersForFLW.add(currentChapter);

            log.info("Generated incorrect chapters for msisdn : " + msisdn + " chapters - " + chaptersForFLW);
        }

        // remove freshly called chaptersForFLW from the hash
        for (CourseItemMeasure courseItemMeasure : correctCallsAfterBug) {
            msisdn = courseItemMeasure.getFrontLineWorkerDimension().getMsisdn().toString();
            currentChapter = getChapterIndexFromCourseItemDimension(courseItemMeasure.getCourseItemDimension());

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

    private Integer getChapterIndexFromCourseItemDimension(CourseItemDimension courseItemDimension) {
        System.out.println("Getting chapter index for course dimension : " + courseItemDimension.getName());
        return Integer.parseInt(courseItemDimension.getName().substring(8, 1));
    }


}
