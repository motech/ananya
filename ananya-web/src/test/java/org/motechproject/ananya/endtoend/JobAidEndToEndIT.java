package org.motechproject.ananya.endtoend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.framework.CouchDb;
import org.motechproject.ananya.framework.ReportDb;
import org.motechproject.ananya.framework.domain.JobAidRequest;
import org.motechproject.ananya.framework.domain.JobAidResponse;
import org.motechproject.ananya.framework.domain.JobAidWebService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class JobAidEndToEndIT extends SpringIntegrationTest {

    @Autowired
    private JobAidWebService jobAidWebService;

    @Autowired
    private CouchDb couchDb;

    @Autowired
    private ReportDb reportDb;

    String callerId = "919686577090";
    String callId = "1234";
    String operator = "airtel";
    String circle = "bihar";

    @After
    public void after() {
        clearFLWData();
    }

    @Before
    public void before() {
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

    private void clearFLWData() {
        reportDb.clearDimensionAndMeasures(callerId);
        couchDb.clearFLWData(callerId);
    }

    
}
