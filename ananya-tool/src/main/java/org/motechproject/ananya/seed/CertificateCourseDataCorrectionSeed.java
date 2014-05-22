package org.motechproject.ananya.seed;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.motechproject.ananya.seed.service.CourseItemMeasureSeedService;
import org.motechproject.ananya.support.synchroniser.service.SMSService;
import org.motechproject.deliverytools.seed.Seed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CertificateCourseDataCorrectionSeed {

    private static Logger log = LoggerFactory.getLogger(CertificateCourseDataCorrectionSeed.class);

    private DateTime endDate = new DateTime(2012, 06, 2, 0, 0);
    private DateTime startDate = new DateTime(2012, 05, 23, 0, 0);
    private Map<String, List<QuizInformation>> flwQuizHistoryMap = new HashMap<String, List<QuizInformation>>();

    @Autowired
    private CourseItemMeasureSeedService courseItemMeasureSeedService;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private SMSService smsService;
    @Autowired
    private AllSMSReferences allSMSReferences;

    @Seed(priority = 0, version = "1.3", comment = "Correct incorrect scores data for certificate course.")
    public void correctFlwScoresData() {
        log.info("Correction FLW Scores data:START");

        List flwCourseHistory = courseItemMeasureSeedService.fetchFlwCourseHistory();
        log.info("Fetched course item measure history.");

        Iterator iterator = flwCourseHistory.iterator();
        while (iterator.hasNext()) {
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
        log.info("Created Flw quiz history map");

        for (String msisdn : flwQuizHistoryMap.keySet()) {
            log.info("Correcting the score for FLW with msisdn : " + msisdn);
            FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);

            if (allSMSReferences.findByMsisdn(msisdn) != null) {
                log.info("SMS already sent for " + msisdn);
                continue;
            }

            List<QuizInformation> quizInformationList = flwQuizHistoryMap.get(msisdn);
            ReportCard reportCard = new ReportCard();
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
                log.info("Updating FLW : " + msisdn + " oldScore - " + oldScore + " newScore - " + newScore);
                allFrontLineWorkers.update(frontLineWorker);
            }

            if (frontLineWorker.currentCourseAttempts() > 0) {
                log.info("Checking FLW " + msisdn + " who has completed the course.");
                int previousChapterIndex = -1;
                int counter = 0;
                for (QuizInformation quizInformation : quizInformationList) {
                    counter++;
                    if (quizInformation.type.equalsIgnoreCase("END")) {
                        reportCard.clearScoresForChapterIndex(quizInformation.chapterIndex);
                        for (int i = 0; i < quizInformation.score; i++)
                            reportCard.addScore(new Score(quizInformation.chapterIndex, "" + i, true, ""));

                        for (int i = quizInformation.score; i < 4; i++)
                            reportCard.addScore(new Score(quizInformation.chapterIndex, "" + i, false, ""));

                    }
                    if (Integer.parseInt(quizInformation.chapterIndex) < previousChapterIndex
                            || (quizInformation.chapterIndex.equalsIgnoreCase("8")
                            && quizInformation.type.equalsIgnoreCase("END")
                            && counter == quizInformationList.size())) {

                        log.info("Switching attempts at " + quizInformation.chapterIndex + " " + quizInformation.type +
                                " previousChapterIndex is " + previousChapterIndex +
                                " FLW msisdn is : " + msisdn);

                        if (reportCard.totalScore() > FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE) {
                            log.info("FLW " + msisdn + "had passed course. Sending FLW an SMS");
                            smsService.buildAndSendSMS(
                                    frontLineWorker.getMsisdn(),
                                    frontLineWorker.getLanguage(),
                                    frontLineWorker.getLocationId(),
                                    frontLineWorker.currentCourseAttempts());
                        }
                        reportCard = new ReportCard();
                    }
                    previousChapterIndex = Integer.parseInt(quizInformation.chapterIndex);
                }
            }
        }
        log.info("Correction FLW Scores data:END");
    }

    private Integer getChapterIndexFromCourseItemDimension(String str) {
        return Integer.parseInt(str.substring(8, 9)) - 1;
    }

    class QuizInformation {
        private String chapterIndex;
        private String type;
        private int score;

        public QuizInformation(Object[] row) {
            chapterIndex = getChapterIndexFromCourseItemDimension((String) row[1]).toString();
            type = (String) row[3];
            if (row[4] == null) score = -1;
            else score = (Integer) row[4];
        }
    }

}
