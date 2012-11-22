package org.motechproject.ananya.endtoend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.framework.CouchDb;
import org.motechproject.ananya.framework.ReportDb;
import org.motechproject.ananya.framework.TestJsonData;
import org.motechproject.ananya.framework.domain.JobAidDisconnectRequest;
import org.motechproject.ananya.framework.domain.JobAidRequest;
import org.motechproject.ananya.framework.domain.JobAidResponse;
import org.motechproject.ananya.framework.domain.JobAidWebService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JobAidEndToEndIT extends SpringIntegrationTest {

    @Autowired
    private JobAidWebService jobAidWebService;
    @Autowired
    private CouchDb couchDb;
    @Autowired
    private ReportDb reportDb;
    @Autowired
    private TestJsonData testJsonData;

    private String callerId = "919686577090";
    private String callId = "919686577090-1234567890";
    private String circle = "bihar";
    private String operator = "airtel";

    @Before
    @After
    public void after() {
        clearFLWData();
    }

    @Test
    public void shouldReturnADefaultResponseForANewCaller() throws IOException {
        JobAidRequest request = new JobAidRequest(callerId, operator, circle, callId);
        JobAidResponse jobAidResponse = jobAidWebService.requestForCallerData(request);

        jobAidResponse.confirmCurrentUsage(0).confirmNoPromptsHeard().confirmPartiallyRegistered().confirmMaxUsage(39);
        couchDb.confirmFlwDoesNotExist(callerId);
        reportDb.confirmFlwDoesNotExist(callerId);
    }

    @Test
    public void shouldReturnValidResponseForExistingCaller() throws IOException {
        couchDb.createPartiallyRegisteredFlwFor(callerId, operator, circle)
                .updatePromptsHeard(callerId, "welcome.wav")
                .updateCurrentJobAidUsage(callerId, 23);
        reportDb.createMeasuresAndDimensionsForFlw(callerId, callId, operator, circle);

        JobAidRequest request = new JobAidRequest(callerId, operator, circle, callId);
        JobAidResponse jobAidResponse = jobAidWebService.requestForCallerData(request);
        jobAidResponse.confirmPartiallyRegistered().confirmMaxUsage(39).confirmPromptHeard("welcome.wav", 1).confirmCurrentUsage(23);
    }

    @Test
    public void shouldCreateLogsDimensionsMeasuresAtDisconnectForNewCaller() throws IOException {
        String calledNumber = "5771122334455";
        String callDuration = "30000";
        String promptList = "['prompt1', 'prompt2']";
        Integer expectedJobAidUsageByPulse = 60000;
        int allowedUsagePerMonth = 39 * 60 * 1000;

        List<String> nodeNames = Arrays.asList("Level 3 Chapter 2 Lesson2", "Level 3 Chapter 2 Lesson3");
        String json = testJsonData.forJobAidDisconnect(nodeNames);

        JobAidDisconnectRequest request = new JobAidDisconnectRequest(callerId, callId, operator, circle, calledNumber, callDuration, promptList, json);
        jobAidWebService.requestForDisconnect(request);

        couchDb.confirmPartiallyRegistered(callerId, operator)
                .confirmJobAidUsage(callerId, expectedJobAidUsageByPulse, allowedUsagePerMonth)
                .confirmPromptsHeard(callerId, Arrays.asList("prompt1", "prompt2"));

        reportDb.confirmFLWDimensionForPartiallyRegistered(callerId, operator)
                .confirmRegistrationMeasureForPartiallyRegistered(callerId);

        couchDb.confirmNoRegistrationLogFor(callId)
                .confirmNoAudioTrackerLogFor(callId)
                .confirmNoCallLogFor(callId)
                .confirmNoCourseLogFor(callId)
                .confirmNoSMSLog(callId);

        reportDb.confirmJobAidContentMeasure(callId, callerId, nodeNames)
                .confirmCallDurationMeasure(callId, callerId, "5771122");

        reportDb.clearJobAidMeasure(callId)
                .clearCallDurationMeasure(callId);
    }

    private void clearFLWData() {
        reportDb.clearFLWDimensionAndMeasures(callerId);
        couchDb.clearFLWData(callerId);
        couchDb.clearAllLogs();
    }

}
