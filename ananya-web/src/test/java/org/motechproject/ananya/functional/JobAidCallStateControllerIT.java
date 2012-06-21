package org.motechproject.ananya.functional;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.TestUtils;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.framework.MyWebClient;
import org.motechproject.ananya.repository.AllAudioTrackerLogs;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.motechproject.ananya.framework.MyWebClient.PostParam.param;

public class JobAidCallStateControllerIT extends SpringIntegrationTest {

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private AllCallLogs allCallLogs;

    @Autowired
    private AllAudioTrackerLogs allAudioTrackerLogs;

    @Test
    public void shouldHandleDisconnect() throws IOException {
        String callId = "1234";
        String calledNumber = "12345";
        Integer currentJobAidUsage = 12;
        Integer currentCallDuration = 23;

        FrontLineWorker frontLineWorker = TestUtils.getSampleFLW();
        frontLineWorker.setCurrentJobAidUsage(currentJobAidUsage);
        allFrontLineWorkers.add(frontLineWorker);
        markForDeletion(frontLineWorker);

        MyWebClient.PostParam callIdParam = param("callId", callId);
        MyWebClient.PostParam callerIdParam = param("callerId", frontLineWorker.getMsisdn());
        MyWebClient.PostParam calledNumberParam = param("calledNumber", calledNumber);
        MyWebClient.PostParam jsonParam = param("dataToPost", postedData());
        MyWebClient.PostParam callDurationParam = param("CallDuration", currentCallDuration.toString());
        MyWebClient.PostParam promptListParam = param("promptList", "['prompt1', 'prompt2']");

        new MyWebClient().post(getAppServerHostUrl() + "/ananya/jobaid/transferdata/disconnect",
                callIdParam, callerIdParam, calledNumberParam, jsonParam, callDurationParam, promptListParam);

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.findByMsisdn(frontLineWorker.getMsisdn());
        assertOnPromptUpdate(updatedFrontLineWorker);
        assertOnUsageUpdate(currentJobAidUsage, currentCallDuration, updatedFrontLineWorker);
        assertOnCallLogsInDB(callId, calledNumber, frontLineWorker);
        assertOnAudioTrackersInDB(callId);
    }

    private void assertOnAudioTrackersInDB(String callId) {
        AudioTrackerLog audioTrackerLog = allAudioTrackerLogs.findByCallId(callId);
        assertNotNull(audioTrackerLog);
        List<AudioTrackerLogItem> audioTrackerLogItems = audioTrackerLog.items();
        assertEquals(2, audioTrackerLogItems.size());
        assertEquals("e79139b5540bf3fc8d96635bc2926f90", audioTrackerLogItems.get(0).getContentId());
        assertEquals(123, (int) audioTrackerLogItems.get(0).getDuration());
        assertEquals(123456789l, audioTrackerLogItems.get(0).getTime().getMillis());
    }

    private void assertOnCallLogsInDB(String callId, String calledNumber, FrontLineWorker frontLineWorker) {
        CallLog callLog = allCallLogs.findByCallId(callId);
        assertNotNull(callLog);
        assertEquals(frontLineWorker.getMsisdn(), callLog.getCallerId());
        assertEquals(calledNumber, callLog.getCalledNumber());
        assertEquals(1, callLog.getCallLogItems().size());
        assertEquals(CallFlowType.CALL, callLog.getCallLogItems().get(0).getCallFlowType());
    }

    private void assertOnUsageUpdate(Integer currentJobAidUsage, Integer currentCallDuration, FrontLineWorker updatedFrontLineWorker) {
        Integer updatedJobAidUsage = updatedFrontLineWorker.getCurrentJobAidUsage();
        DateTime lastJobAidAccessTime = updatedFrontLineWorker.getLastJobAidAccessTime();

        Integer newCallDuration = currentJobAidUsage + currentCallDuration;
        assertEquals(newCallDuration, updatedJobAidUsage);

        assertEquals(DateTime.now().getMonthOfYear(), lastJobAidAccessTime.getMonthOfYear());
        assertEquals(DateTime.now().getYear(), lastJobAidAccessTime.getYear());
    }

    private void assertOnPromptUpdate(FrontLineWorker updatedFrontLineWorker) {
        Map<String, Integer> prompts = updatedFrontLineWorker.getPromptsHeard();

        assertEquals((int) prompts.get("prompt1"), 2);
        assertEquals((int) prompts.get("prompt2"), 1);
    }

    private String postedData() {
        String packet1 = "{" +
                "   \"callEvent\" : \"CALL_START\"," +
                "   \"time\"  : 1231413" +
                "}";

        String packet2 = "{" +
                "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                "    \"duration\" : \"123\",                             " +
                "    \"time\" : \"123456789\"                          " +
                "}";

        String packet3 = "{" +
                "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                "    \"duration\" : \"123\",                             " +
                "    \"time\" : \"123456789\"                          " +
                "}";

        String packet4 = "{" +
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
                "       \"type\"  : \"audioTracker\", " +
                "       \"data\"  : " + packet3 +
                "   }," +
                "   {" +
                "       \"token\" : 3," +
                "       \"type\"  : \"callDuration\", " +
                "       \"data\"  : " + packet4 +
                "   }" +
                "]";
    }
}
