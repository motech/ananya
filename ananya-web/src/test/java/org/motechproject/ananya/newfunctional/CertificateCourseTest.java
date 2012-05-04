package org.motechproject.ananya.newfunctional;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.CallLogCounter;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.Score;
import org.motechproject.ananya.framework.CouchDb;
import org.motechproject.ananya.framework.ReportDb;
import org.motechproject.ananya.framework.domain.CertificateCourseRequest;
import org.motechproject.ananya.framework.domain.CertificateCourseResponse;
import org.motechproject.ananya.framework.domain.CertificateCourseWebservice;
import org.motechproject.ananya.repository.AllCallLogCounters;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class CertificateCourseTest extends SpringIntegrationTest {

    @Autowired
    private CertificateCourseWebservice certificateCourseWebService;

    @Autowired
    private AllCallLogCounters allCallLogCounters;

    @Autowired
    private CouchDb couchDb;

    @Autowired
    private ReportDb reportDb;

    private String callerId = "987654";
    private String operator = "airtel";
    private String callId = "2345678";


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void after() {
        clearFLWData();
    }

    private void clearFLWData() {
        reportDb.clearDimensionAndMeasures(callerId);
        couchDb.clearFLWData(callerId);
    }

    @Test
    public void onFetchingCallerData_shouldPartiallyRegisterFLW_andUpdateOperatorIfNotPresent_andUpdateCouchDB() throws IOException {
        CertificateCourseRequest request = new CertificateCourseRequest(callerId, operator, callId);
        CertificateCourseResponse response = certificateCourseWebService.requestForCallerData(request);

        response.confirmPartiallyRegistered();

        couchDb.confirmPartiallyRegistered(callerId, operator);

        reportDb.confirmFLWDimensionForPartiallyRegistered(callerId, operator)
                .confirmRegistrationMeasureForPartiallyRegistered(callerId);
    }

    @Test
    public void shouldCreateTransferDataList_toSaveCertificateCourseState() throws IOException {
        CertificateCourseRequest request = new CertificateCourseRequest(callerId, operator, callId);
        certificateCourseWebService.requestForCallerData(request);

        String newCallId = callId + "1";
        CertificateCourseRequest transferDataRequest = new CertificateCourseRequest(callerId, operator, newCallId);

        int token = GenerateToken(newCallId);

        String jsonData = String.format("[{\"token\":%d ,\"type\":\"ccState\",\"data\":{\"chapterIndex\":8,\"lessonOrQuestionIndex\":7,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}}]", token);
        transferDataRequest.setJsonPostData(jsonData);

        CertificateCourseResponse response =  certificateCourseWebService.requestForDisconnect(transferDataRequest);

        ReportCard reportCard = new ReportCard();
        reportCard.addScore(new Score("8", "7", true, newCallId));

        couchDb.confirmBookmarkUpdated(callerId,new BookMark("playAnswerExplanation",8,7))
               .confirmScoresSaved(callerId, reportCard);
    }


    @Test
    public void shouldPostDisconnectEvent() throws Exception {
        CertificateCourseRequest request = new CertificateCourseRequest(callerId, operator, callId);
        certificateCourseWebService.requestForCallerData(request);

        CertificateCourseRequest transferDataRequest = new CertificateCourseRequest(callerId, operator, callId);
        int token = GenerateToken(callId);


        String jsonData = String.format("[{\"token\":%d,\"type\":\"ccState\"," +
                "\"data\":{\"result\":null,\"questionResponse\":null,\"contentId\":\"0cccd9b516233e4bb1c6c04fed6b9eb2\"," +
                "\"contentType\":\"lesson\",\"certificateCourseId\":\"\",\"contentData\":null,\"interactionKey\":\"lessonEndMenu\"," +
                "\"courseItemState\":\"end\",\"contentName\":\"Chapter 2 Lesson 1\",\"time\":\"1331211652245\"," +
                "\"chapterIndex\":1,\"lessonOrQuestionIndex\":0}}," +
                "{\"token\":%d,\"type\":\"callDuration\"," +
                "\"data\":{\"callEvent\":\"DISCONNECT\",\"time\":1331211652263}}]",token,token+1);

        transferDataRequest.setJsonPostData(jsonData);

        certificateCourseWebService.requestForDisconnect(transferDataRequest);

        BookMark bookMark = new BookMark("lessonEndMenu",1,0);
        reportDb.confirmCourseItemMeasureForDisconnectEvent(callerId, bookMark, "END");

    }

    @Test
    @Ignore("Has some timing related issues. Will come back and fix it. <Sneha/Aravind>")
    public void shouldSendSmsAtTheEndOfTheCourse() throws Exception {
        CertificateCourseRequest request = new CertificateCourseRequest(callerId, operator, callId);
        certificateCourseWebService.requestForCallerData(request);

        CertificateCourseRequest transferDataRequest = new CertificateCourseRequest(callerId, operator, callId);

        String jsonDataWithScoresForFullCCFlow = "[{\"token\":23890,\"type\":\"ccState\",\"data\":{\"chapterIndex\":0,\"lessonOrQuestionIndex\":4,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":23891,\"type\":\"ccState\",\"data\":{\"chapterIndex\":0,\"lessonOrQuestionIndex\":5,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":23892,\"type\":\"ccState\",\"data\":{\"chapterIndex\":0,\"lessonOrQuestionIndex\":6,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":23893,\"type\":\"ccState\",\"data\":{\"chapterIndex\":0,\"lessonOrQuestionIndex\":7,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":23894,\"type\":\"ccState\",\"data\":{\"chapterIndex\":1,\"lessonOrQuestionIndex\":4,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":23895,\"type\":\"ccState\",\"data\":{\"chapterIndex\":1,\"lessonOrQuestionIndex\":5,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":23896,\"type\":\"ccState\",\"data\":{\"chapterIndex\":1,\"lessonOrQuestionIndex\":6,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":23897,\"type\":\"ccState\",\"data\":{\"chapterIndex\":1,\"lessonOrQuestionIndex\":7,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":23898,\"type\":\"ccState\",\"data\":{\"chapterIndex\":2,\"lessonOrQuestionIndex\":4,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":23899,\"type\":\"ccState\",\"data\":{\"chapterIndex\":2,\"lessonOrQuestionIndex\":5,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238910,\"type\":\"ccState\",\"data\":{\"chapterIndex\":2,\"lessonOrQuestionIndex\":6,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238911,\"type\":\"ccState\",\"data\":{\"chapterIndex\":2,\"lessonOrQuestionIndex\":7,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238912,\"type\":\"ccState\",\"data\":{\"chapterIndex\":3,\"lessonOrQuestionIndex\":4,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238913,\"type\":\"ccState\",\"data\":{\"chapterIndex\":3,\"lessonOrQuestionIndex\":5,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238914,\"type\":\"ccState\",\"data\":{\"chapterIndex\":3,\"lessonOrQuestionIndex\":6,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238915,\"type\":\"ccState\",\"data\":{\"chapterIndex\":3,\"lessonOrQuestionIndex\":7,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238916,\"type\":\"ccState\",\"data\":{\"chapterIndex\":4,\"lessonOrQuestionIndex\":4,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238917,\"type\":\"ccState\",\"data\":{\"chapterIndex\":4,\"lessonOrQuestionIndex\":5,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238918,\"type\":\"ccState\",\"data\":{\"chapterIndex\":4,\"lessonOrQuestionIndex\":6,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238919,\"type\":\"ccState\",\"data\":{\"chapterIndex\":4,\"lessonOrQuestionIndex\":7,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238920,\"type\":\"ccState\",\"data\":{\"chapterIndex\":5,\"lessonOrQuestionIndex\":4,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238921,\"type\":\"ccState\",\"data\":{\"chapterIndex\":5,\"lessonOrQuestionIndex\":5,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238922,\"type\":\"ccState\",\"data\":{\"chapterIndex\":5,\"lessonOrQuestionIndex\":6,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238923,\"type\":\"ccState\",\"data\":{\"chapterIndex\":5,\"lessonOrQuestionIndex\":7,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238924,\"type\":\"ccState\",\"data\":{\"chapterIndex\":6,\"lessonOrQuestionIndex\":4,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238925,\"type\":\"ccState\",\"data\":{\"chapterIndex\":6,\"lessonOrQuestionIndex\":5,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238926,\"type\":\"ccState\",\"data\":{\"chapterIndex\":6,\"lessonOrQuestionIndex\":6,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238927,\"type\":\"ccState\",\"data\":{\"chapterIndex\":6,\"lessonOrQuestionIndex\":7,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238928,\"type\":\"ccState\",\"data\":{\"chapterIndex\":7,\"lessonOrQuestionIndex\":4,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238929,\"type\":\"ccState\",\"data\":{\"chapterIndex\":7,\"lessonOrQuestionIndex\":5,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238930,\"type\":\"ccState\",\"data\":{\"chapterIndex\":7,\"lessonOrQuestionIndex\":6,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238931,\"type\":\"ccState\",\"data\":{\"chapterIndex\":7,\"lessonOrQuestionIndex\":7,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238932,\"type\":\"ccState\",\"data\":{\"chapterIndex\":8,\"lessonOrQuestionIndex\":4,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238933,\"type\":\"ccState\",\"data\":{\"chapterIndex\":8,\"lessonOrQuestionIndex\":5,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238934,\"type\":\"ccState\",\"data\":{\"chapterIndex\":8,\"lessonOrQuestionIndex\":6,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":238935,\"type\":\"ccState\",\"data\":{\"chapterIndex\":8,\"lessonOrQuestionIndex\":7,\"questionResponse\":1,\"result\":false,\"interactionKey\":\"playAnswerExplanation\"}},{\"token\":235999,\"type\":\"ccState\",\"data\":{\"chapterIndex\":0,\"lessonOrQuestionIndex\":4,\"questionResponse\":1,\"result\":true,\"interactionKey\":\"playCourseResult\"}}]";

        transferDataRequest.setJsonPostData(jsonDataWithScoresForFullCCFlow);
        certificateCourseWebService.requestForDisconnect(transferDataRequest);
        String smsReferenceNumber= "00000098765401";
        reportDb.confirmSMSSent(callerId, smsReferenceNumber);

        clearSMSReferences();
    }

    private void clearSMSReferences() {
        reportDb.clearSMSSentMeasure(callerId);
        couchDb.clearSMSReferences(callerId);
    }

    private int GenerateToken(String callId) {
        CallLogCounter callLogCounter = allCallLogCounters.findByCallId(callId);
        int token;
        if(callLogCounter!= null)
            token = callLogCounter.getToken()+20;
        else
            token = (int)Math.ceil(Math.random()*7);
        return token;
    }
}
