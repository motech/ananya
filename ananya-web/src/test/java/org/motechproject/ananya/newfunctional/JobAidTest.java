package org.motechproject.ananya.newfunctional;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
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

    private void clearFLWData() {
        reportDb.clearDimensionAndMeasures(callerId);
        couchDb.clearFLWData(callerId);
    }

    @Test
    public void onFetchingCallerData_shouldPartiallyRegisterFLW_andPersistInCouchAndPostgres() throws IOException {
        int expectedMaxUsage = 39;
        int expectedCurrentUsage = 0;

        JobAidRequest request = new JobAidRequest(callerId, operator, circle, callId);
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
    public void shouldUpdateJobAidDataAndPostDisconnectEvent() throws IOException {
        JobAidRequest request = new JobAidRequest(callerId, operator, circle, callId);
        JobAidResponse registrationResponse = jobAidService.whenRequestedForCallerData(request);

        registrationResponse.confirmNoPromptsHeard();
        String currentUsage = "21";
        String maxUsagePrompt = "[MaxUsage]";
        String dataToPost = postedData();

        JobAidDisconnectRequest jobAidDisconnectRequest = new JobAidDisconnectRequest(callerId, operator, callId, "12345", "1260000", maxUsagePrompt);
        String dateTimeString = new Long(DateTime.now().getMillis()).toString();
        dataToPost = String.format(dataToPost, dateTimeString, reportDb.getExistingAudioDimension().getContentId(),
                dateTimeString, dateTimeString);
        jobAidDisconnectRequest.setJsonPostData(dataToPost);
        jobAidService.requestForDisconnect(jobAidDisconnectRequest);

        reportDb.confirmJobAidContentMeasureForDisconnectEvent(callId);
        reportDb.clearJobAidMeasureAndAudioTrackerLogs(callId);

        JobAidResponse response = jobAidService.whenRequestedForCallerData(request);
        response.confirmCurrentUsage(Integer.valueOf(currentUsage));
        response.verifyPromptHeard(maxUsagePrompt, 1);
    }

    private String postedData() {
        String packet1 = "{" +
                "   \"callEvent\" : \"CALL_START\"," +
                "   \"time\"  : %s" +
                "}";

        String packet2 = "{" +
                "    \"contentId\" : \"%s\",     " +
                "    \"duration\" : \"123\",                             " +
                "    \"time\" : \"%s\"                          " +
                "}";

        String packet3 = "{" +
                "   \"callEvent\" : \"DISCONNECT\"," +
                "   \"time\"  : %s" +
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
}
