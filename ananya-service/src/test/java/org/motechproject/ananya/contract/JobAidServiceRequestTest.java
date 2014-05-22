package org.motechproject.ananya.contract;

import org.junit.Test;
import org.motechproject.ananya.domain.CallDurationList;

import static junit.framework.Assert.assertEquals;

public class JobAidServiceRequestTest {
    @Test
    public void shouldReturnAudioTrackerListFromTransferDataList() {
        String json = postedData();
        JobAidServiceRequest jobAidServiceRequest = new JobAidServiceRequest("callId", "callerId", "calledNumber").withJson(json).withPromptList("").withCallDuration(0);
        AudioTrackerRequestList audioTrackerRequestList = jobAidServiceRequest.getAudioTrackerRequestList();

        assertEquals(3, audioTrackerRequestList.all().size());
    }

    @Test
    public void shouldReturnCallDurationListFromTransferDataList() {
        String json = postedData();
        JobAidServiceRequest jobAidServiceRequest = new JobAidServiceRequest("callId", "callerId", "calledNumber").withJson(json).withCallDuration(0);
        CallDurationList callDurationList = jobAidServiceRequest.getCallDurationList();

        assertEquals(2, callDurationList.all().size());
    }

    @Test
    public void shouldCreateJobAidPromptRequestObjectWhenPassedCorrectParameters() {
        JobAidServiceRequest jobAidServiceRequest = new JobAidServiceRequest("callId", "callerId", "calledNumber").withJson(postedData()).withPromptList("['prompt1', 'prompt2']").withCallDuration(111);

        assertEquals(jobAidServiceRequest.getPrompts().size(), 2);
        assertEquals(jobAidServiceRequest.getPrompts().get(0), "prompt1");
        assertEquals(jobAidServiceRequest.getPrompts().get(1), "prompt2");
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
                "       \"type\"  : \"audioTracker\", " +
                "       \"data\"  : " + packet3 +
                "   }," +
                "   {" +
                "       \"token\" : 4," +
                "       \"type\"  : \"callDuration\", " +
                "       \"data\"  : " + packet4 +
                "   }" +
                "]";
    }
}
