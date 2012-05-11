package org.motechproject.ananya.newfunctional;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.framework.CouchDb;
import org.motechproject.ananya.framework.ReportDb;
import org.motechproject.ananya.framework.domain.JobAidDisconnectRequest;
import org.motechproject.ananya.framework.domain.JobAidRequest;
import org.motechproject.ananya.framework.domain.JobAidResponse;
import org.motechproject.ananya.framework.domain.JobAidWebService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
@Ignore
public class JobAidTest extends SpringIntegrationTest {

    @Autowired
    private JobAidWebService jobAidService;

    @Autowired
    private CouchDb couchDb;

    @Autowired
    private ReportDb reportDb;

    String callerId = "123456";
    String callId = "1234";
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

        request.setCallDuration(currentUsage * 60 * 1000);
        jobAidService.updateCurrentUsage(request);

        JobAidResponse response = jobAidService.whenRequestedForCallerData(request);
        response.confirmCurrentUsage(currentUsage);
    }

    private String postedData() {
        String packet1 = "{" +
                "   \"callEvent\" : \"CALL_START\"," +
                "   \"time\"  : 1231413" +
                "}";

        String packet2 = "{" +
                "    \"contentId\" : \"%s\",     " +
                "    \"duration\" : \"123\",                             " +
                "    \"time\" : \"%s\"                          " +
                "}";

        String packet3 = "{" +
                "   \"callEvent\" : \"DISCONNECT\"," +
                "   \"time\"  : 1231413" +
                "}";

        return "[" +
                "   {" +
                "       \"token\" : 0," +
                "       \"type\"  : \"callDuration\", " +
                "       \"data\"  : " + packet1 +
                "   }," +
                "" +
                "   {" +
                "       \"token\" : 1," +
                "       \"type\"  : \"audioTracker\", " +
                "       \"data\"  : " + packet2 +
                "   }," +
                "" +
                "   {" +
                "       \"token\" : 2," +
                "       \"type\"  : \"callDuration\", " +
                "       \"data\"  : " + packet3 +
                "   }" +
                "]";
    }

    @Test
    public void shouldUpdateJobAidDataAndPostDisconnectEvent() throws IOException {
        JobAidRequest request = new JobAidRequest(callerId, operator);
        JobAidResponse registrationResponse = jobAidService.whenRequestedForCallerData(request);

        JobAidDisconnectRequest jobAidDisconnectRequest = new JobAidDisconnectRequest(callerId, operator, callId, "12345");
        String dataToPost = postedData();
        dataToPost = String.format(dataToPost, reportDb.getExistingAudioDimension().getContentId(), new Long(DateTime.now().getMillis()).toString());
        jobAidDisconnectRequest.setJsonPostData(dataToPost);
        jobAidService.requestForDisconnect(jobAidDisconnectRequest);

        reportDb.confirmJobAidContentMeasureForDisconnectEvent(callId);
        reportDb.clearJobAidMeasureAndAudioTrackerLogs(callId);
    }
}
