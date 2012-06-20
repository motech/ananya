package org.motechproject.ananya.seed;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.seed.service.CourseItemMeasureSeedService;
import org.motechproject.ananya.service.SendSMSService;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CertificateCourseDataCorrectionSeed {

    private DateTime endDate = new DateTime(2012, 06, 2, 0, 0);
    private DateTime startDate = new DateTime(2012, 05, 23, 0, 0);
    private Map<String, List<QuizInformation>> flwQuizHistoryMap = new HashMap<String, List<QuizInformation>>();

    @Autowired
    private CourseItemMeasureSeedService courseItemMeasureSeedService;
    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;
    @Autowired
    private SendSMSService sendSMSService;

    @Seed(priority = 0, version = "1.3", comment = "Correct incorrect scores data for certificate course.")
    public void correctFlwScoresData() {
        print("Correction FLW Scores data:START");

        List flwCourseHistory = courseItemMeasureSeedService.fetchFlwCourseHistory();
        print("Fetched course item measure history.");

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
        print("Created Flw quiz history map");

        for (String msisdn : flwQuizHistoryMap.keySet()) {
            print("Correcting the score for FLW with msisdn : " + msisdn);
            FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
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
                print("Updating FLW : " + msisdn + " oldScore - " + oldScore + " newScore - " + newScore);
                allFrontLineWorkers.update(frontLineWorker);
            }

            if (frontLineWorker.currentCourseAttempt() > 0) {
                print("Checking FLW " + msisdn + " who has completed the course.");
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
                    if (Integer.parseInt(quizInformation.chapterIndex) < previousChapterIndex ||
                            (quizInformation.chapterIndex.equalsIgnoreCase("8") &&
                                    quizInformation.type.equalsIgnoreCase("END") &&
                                    counter == quizInformationList.size())) {
                        print("Switching attempts at " + quizInformation.chapterIndex + " " + quizInformation.type +
                                " previousChapterIndex is " + previousChapterIndex +
                                " FLW msisdn is : " + msisdn);
                        if (reportCard.totalScore() > FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE) {
                            print("FLW " + msisdn + "had passed course. Sending FLW an SMS");
                            sendSMSService.buildAndSendSMS(
                                    frontLineWorker.getMsisdn(),
                                    frontLineWorker.getLocationId(),
                                    frontLineWorker.currentCourseAttempt());
                        }
                        reportCard = new ReportCard();
                    }
                    previousChapterIndex = Integer.parseInt(quizInformation.chapterIndex);
                }
            }
        }
        print("Correction FLW Scores data:END");
    }

    private void print(String message) {
        System.out.println(message);
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
