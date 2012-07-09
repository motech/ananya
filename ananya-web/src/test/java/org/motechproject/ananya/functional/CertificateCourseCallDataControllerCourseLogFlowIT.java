package org.motechproject.ananya.functional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.TestUtils;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.framework.MyWebClient;
import org.motechproject.ananya.repository.AllCertificateCourseLogs;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.ananya.framework.MyWebClient.PostParam.param;

public class CertificateCourseCallDataControllerCourseLogFlowIT extends SpringIntegrationTest {

    private MyWebClient myWebClient;

    @Autowired
    private AllCertificateCourseLogs allCertificateCourseLogs;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Before
    public void setUp() throws Exception {
        myWebClient = new MyWebClient();
    }

    @Test
    public void shouldLogCertificationCourseAndSaveBookmarkAndScores() throws IOException {

        FrontLineWorker frontLineWorker = TestUtils.getSampleFLW();
        allFrontLineWorkers.add(frontLineWorker);

        String packet1 = "{" +
                "    \"chapterIndex\" : 1,                                     " +
                "    \"lessonOrQuestionIndex\" : 2,                            " +
                "    \"questionResponse\" : 1,                                 " +
                "    \"result\" : true,                                        " +
                "    \"interactionKey\" : \"playAnswerExplanation\",           " +

                "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                "    \"contentType\" : \"lesson\",                             " +
                "    \"courseItemState\" : \"START\",                          " +
                "    \"contentData\" : 6,                                      " +
                "    \"certificateCourseId\" : \"e79139b5540bf3fc8d96635bc2926f90\"  " +
                "}";

        String callId = "99865740001234567890";
        MyWebClient.PostParam callIdParam = param("callId", callId);
        MyWebClient.PostParam callerIdParam = param("callerId", "919986574000");
        MyWebClient.PostParam calledNumberParam = param("calledNumber", "57711");
        MyWebClient.PostParam dataToPost = param("dataToPost", "[{\"token\":\"0\",\"type\":\"ccState\",\"data\":" + packet1 + "}]");

        myWebClient.post(getAppServerHostUrl() + "/ananya/transferdata/disconnect", callIdParam, callerIdParam, calledNumberParam, dataToPost);

        CertificationCourseLog byCallId = allCertificateCourseLogs.findByCallId(callId);
        assertEquals(byCallId.items().size(), 1);

        frontLineWorker = allFrontLineWorkers.get(frontLineWorker.getId());

        BookMark bookMark = frontLineWorker.bookMark();
        assertEquals((int) bookMark.getChapterIndex(), 1);
        assertEquals((int) bookMark.getLessonIndex(), 2);
        assertEquals(bookMark.getType(), "playAnswerExplanation");

        assertEquals(frontLineWorker.reportCard().scores().size(), 1);
        assertEquals(frontLineWorker.reportCard().scores().get(0).result(), true);

        markForDeletion(frontLineWorker);
        markForDeletion(byCallId);
    }

    @Test
    @Ignore
    public void shouldSendSMSWhenAtPlayCourseResult() throws IOException {
        String msisdn = "919986574000";
        FrontLineWorker flw = new FrontLineWorker(msisdn, "name", Designation.AWW, new Location(), RegistrationStatus.REGISTERED);
        flw.addBookMark(new BookMark("playFinalScore", 8, 8));

        ReportCard reportCard = flw.reportCard();

        final Score ch1q1score = new Score("0", "4", true);
        final Score ch1q2score = new Score("0", "5", false);
        final Score ch1q3score = new Score("0", "6", true);

        final Score ch2q1score = new Score("1", "4", true);
        final Score ch2q2score = new Score("1", "5", true);
        final Score ch2q3score = new Score("1", "6", true);

        final Score ch3q1score = new Score("2", "4", true);
        final Score ch3q2score = new Score("2", "5", true);
        final Score ch3q3score = new Score("2", "6", true);

        final Score ch4q1score = new Score("3", "4", true);
        final Score ch4q2score = new Score("3", "5", true);
        final Score ch4q3score = new Score("3", "6", true);

        final Score ch5q1score = new Score("4", "4", true);
        final Score ch5q2score = new Score("4", "5", true);
        final Score ch5q3score = new Score("4", "6", true);

        final Score ch6q1score = new Score("5", "4", true);
        final Score ch6q2score = new Score("5", "5", true);
        final Score ch6q3score = new Score("5", "6", true);

        final Score ch7q1score = new Score("6", "4", true);
        final Score ch7q2score = new Score("6", "5", true);
        final Score ch7q3score = new Score("6", "6", true);

        final Score ch8q1score = new Score("7", "4", false);
        final Score ch8q2score = new Score("7", "5", false);
        final Score ch8q3score = new Score("7", "6", false);

        reportCard.addScore(ch1q1score);
        reportCard.addScore(ch1q2score);
        reportCard.addScore(ch1q3score);

        reportCard.addScore(ch2q1score);
        reportCard.addScore(ch2q2score);
        reportCard.addScore(ch2q3score);

        reportCard.addScore(ch3q1score);
        reportCard.addScore(ch3q2score);
        reportCard.addScore(ch3q3score);

        reportCard.addScore(ch4q1score);
        reportCard.addScore(ch4q2score);
        reportCard.addScore(ch4q3score);

        reportCard.addScore(ch5q1score);
        reportCard.addScore(ch5q2score);
        reportCard.addScore(ch5q3score);

        reportCard.addScore(ch6q1score);
        reportCard.addScore(ch6q2score);
        reportCard.addScore(ch6q3score);

        reportCard.addScore(ch7q1score);
        reportCard.addScore(ch7q2score);
        reportCard.addScore(ch7q3score);

        reportCard.addScore(ch8q1score);
        reportCard.addScore(ch8q2score);
        reportCard.addScore(ch8q3score);

        allFrontLineWorkers.add(flw);
        //markForDeletion(flw);

        String packet1 = "{" +
                "    \"chapterIndex\" : 8,                                     " +
                "    \"lessonOrQuestionIndex\" : 8,                            " +
                "    \"questionResponse\" : 1,                                 " +
                "    \"result\" : true,                                        " +
                "    \"interactionKey\" : \"playCourseResult\",                " +

                "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                "    \"contentType\" : \"course\",                             " +
                "    \"courseItemState\" : \"END\",                          " +
                "    \"contentData\" : null,                                      " +
                "    \"certificateCourseId\" : \"e79139b5540bf3fc8d96635bc2926f90\"  " +
                "}";
        MyWebClient.PostParam callerId = param("callerId", msisdn);
        MyWebClient.PostParam callId = param("callId", msisdn + "1234567890");
        MyWebClient.PostParam dataToPost = param("dataToPost",
                "[{\"token\":\"13\",\"type\":\"ccState\",\"data\":" + packet1 + "}]");

        myWebClient.post(getAppServerHostUrl() + "/ananya/transferdata/disconnect", callId, callerId, dataToPost);

//        CertificationCourseLog byCallId = allCertificationCourseLogs.findByCallId("99865740001234567890");
//        assertEquals(byCallId.getCourseLogItems().size(), 1);
    }

}