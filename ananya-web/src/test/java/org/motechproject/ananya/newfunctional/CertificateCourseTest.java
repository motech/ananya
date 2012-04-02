package org.motechproject.ananya.newfunctional;


import org.junit.After;
import org.junit.Before;
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

    String callerId = "987654";
    String operator = "airtel";
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

        CertificateCourseResponse response =  certificateCourseWebService.requestForTransferData(transferDataRequest);

        ReportCard reportCard = new ReportCard();
        reportCard.addScore(new Score("8", "7", true, newCallId));

        couchDb.confirmBookmarkUpdated(callerId,new BookMark("playAnswerExplanation",8,7))
               .confirmScoresSaved(callerId, reportCard);
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
