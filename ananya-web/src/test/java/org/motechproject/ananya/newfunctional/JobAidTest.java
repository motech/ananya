package org.motechproject.ananya.newfunctional;

import org.junit.After;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.framework.CouchDb;
import org.motechproject.ananya.framework.ReportDb;
import org.motechproject.ananya.framework.domain.JobAidRequest;
import org.motechproject.ananya.framework.domain.JobAidResponse;
import org.motechproject.ananya.framework.domain.JobAidWebService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class JobAidTest extends SpringIntegrationTest {

    @Autowired
    private JobAidWebService jobAidService;

    @Autowired
    private CouchDb couchDb;

    @Autowired
    private ReportDb reportDb;

    String callerId = "123456";
    String operator = "airtel";

    @After
    public void after() {
        clearFLWData();
    }

    private void clearFLWData() {
        reportDb.clearDimensionAndMeasures(callerId);
        couchDb.clearFLWData(callerId);
    }

    @Test
    public void onFetchingCallerData_shouldPartiallyRegisterFLW_andPersistInCouchAndPostgres() throws IOException {
        int expectedMaxUsage = 39;
        int expectedCurrentUsage = 0;

        JobAidRequest request = new JobAidRequest(callerId, operator);
        JobAidResponse response = jobAidService.whenRequestedForCallerData(request);

        response.confirmPartiallyRegistered()
                .confirmMaxUsage(expectedMaxUsage)
                .confirmCurrentUsage(expectedCurrentUsage);

        couchDb.confirmPartiallyRegistered(callerId, operator)
                .confirmUsage(callerId, expectedCurrentUsage, expectedMaxUsage);

        reportDb.confirmFLWDimensionForPartiallyRegistered(callerId, operator)
                .confirmRegistrationMeasureForPartiallyRegistered(callerId);
    }

    @Test
    public void shouldUpdatePromptCountersForFLW() throws IOException {
        JobAidRequest request = new JobAidRequest(callerId, operator);
        JobAidResponse response = jobAidService.createFLW(request);
        String maxUsagePrompt = "MaxUsage";

        response.confirmNoPromptsHeard();
        request.addPromptHeard(maxUsagePrompt);
        jobAidService.updatePromptsHeard(request);

        response = jobAidService.whenRequestedForCallerData(request);
        response.verifyPromptHeard(maxUsagePrompt, 1);

        jobAidService.updatePromptsHeard(request);
        response = jobAidService.whenRequestedForCallerData(request);
        response.verifyPromptHeard(maxUsagePrompt, 2);
    }

    @Test
    public void shouldUpdateCurrentUsageForFLW() throws IOException {
        int currentUsage = 5;
        JobAidRequest request = new JobAidRequest(callerId, operator);
        jobAidService.createFLW(request);

        request.setCallDuration(currentUsage*60*1000);
        jobAidService.updateCurrentUsage(request);

        JobAidResponse response = jobAidService.whenRequestedForCallerData(request);
        response.confirmCurrentUsage(currentUsage);
    }
}
