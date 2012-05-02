package org.motechproject.ananya.functional;

import com.gargoylesoftware.htmlunit.WebClient;
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
    public void shouldUpdatePromptCountersForFLWs() throws IOException {
        final String prompt1 = "prompt1";
        final String prompt2 = "prompt2";
        FrontLineWorker frontLineWorker = TestUtils.getSampleFLW();
        frontLineWorker.markPromptHeard("prompt1");

        allFrontLineWorkers.add(frontLineWorker);
        markForDeletion(frontLineWorker);

        new WebClient().getPage(getAppServerHostUrl() + String.format(
            "/ananya/jobaid/updateprompt?callId=1234&callerId=#{0}&['prompt1', 'prompt2']", frontLineWorker.getMsisdn()));

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.findByMsisdn(frontLineWorker.getMsisdn());
        Map<String, Integer> prompts = updatedFrontLineWorker.getPromptsHeard();
        
        assertEquals((int)prompts.get(prompt1), 2);
        assertEquals((int)prompts.get(prompt2), 1);
    }
    
    @Test
    public void shouldUpdateCurrentUsageAndLastJobAidAccessTimeForFLWs() throws IOException {
        Integer currentJobAidUsage = 12;
        Integer currentCallDuration = 23;
        FrontLineWorker frontLineWorker = TestUtils.getSampleFLW();
        frontLineWorker.setCurrentJobAidUsage(currentJobAidUsage);

        allFrontLineWorkers.add(frontLineWorker);
        markForDeletion(frontLineWorker);

        new WebClient().getPage(getAppServerHostUrl() + String.format(
            "/ananya/jobaid/updateusage?callId=1234&callerId=#{0}&currentUsage={1}", frontLineWorker.getMsisdn(), currentCallDuration));

        FrontLineWorker updatedFrontLineWorker = allFrontLineWorkers.findByMsisdn(frontLineWorker.getMsisdn());
        Integer updatedJobAidUsage = updatedFrontLineWorker.getCurrentJobAidUsage();
        DateTime lastJobAidAccessTime = updatedFrontLineWorker.getLastJobAidAccessTime();

        Integer newCallDuration = currentJobAidUsage + currentCallDuration;
        assertEquals(newCallDuration,updatedJobAidUsage);
        
        assertEquals(DateTime.now().getMonthOfYear(),lastJobAidAccessTime.getMonthOfYear());
        assertEquals(DateTime.now().getYear(),lastJobAidAccessTime.getYear());
    }

    private String postedData() {
        String packet1 = "{" +
                "   \"callEvent\" : \"CALL_START\"," +
                "   \"time\"  : 1231413" +
                "}";

        String packet2 = "{" +
                "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                "    \"duration\" : \"123\",                             " +
                "    \"timeStamp\" : \"123456789\"                          " +
                "}";

        String packet3 = "{" +
                "    \"contentId\" : \"e79139b5540bf3fc8d96635bc2926f90\",     " +
                "    \"duration\" : \"123\",                             " +
                "    \"timeStamp\" : \"123456789\"                          " +
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
                "   }" +
                "   {" +
                "       \"token\" : 3," +
                "       \"type\"  : \"callDuration\", " +
                "       \"data\"  : " + packet4 +
                "   }," +
                "]";
    }

    @Test
    public void shouldSaveJobAidState() throws IOException {
        String callId = "1234";
        String callerId = "123";
        String calledNumber = "12345";

        MyWebClient.PostParam callIdParam = param("callId", callId);
        MyWebClient.PostParam callerIdParam = param("callerId", callerId);
        MyWebClient.PostParam calledNumberParam = param("calledNumber", calledNumber);
        MyWebClient.PostParam jsonParam = param("dataToPost", postedData());

        new MyWebClient().post(getAppServerHostUrl()+"/jobaid/transferdata/disconnect",
                callIdParam, callerIdParam, calledNumberParam, jsonParam);

        CallLog callLog =  allCallLogs.findByCallId(callId);
        assertNotNull(callLog);
        assertEquals(callerId, callLog.getCallerId());
        assertEquals(calledNumber, callLog.getCalledNumber());
        assertEquals(1, callLog.getCallLogItems().size());
        assertEquals(CallFlowType.CALL, callLog.getCallLogItems().get(0).getCallFlowType());

        AudioTrackerLog audioTrackerLog = allAudioTrackerLogs.findByCallId(callId);
        assertNotNull(audioTrackerLog);
        List<AudioTrackerLogItem> audioTrackerLogItems = audioTrackerLog.getAudioTrackerLogItems();
        assertEquals(2, audioTrackerLogItems.size());
        assertEquals("e79139b5540bf3fc8d96635bc2926f90", audioTrackerLogItems.get(0).getContentId());
        assertEquals(123, (int)audioTrackerLogItems.get(0).getDuration());
        assertEquals("123456789", audioTrackerLogItems.get(0).getTimeStamp());
    }
}
