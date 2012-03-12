package org.motechproject.ananya.request;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class JobAidPromptRequestTest {

    @Test
    public void shouldCreateJobAidPromptRequestObjectWhenPassedCorrectParameters() {
       JobAidPromptRequest jobAidPromptRequest = new JobAidPromptRequest(
               "callId", "callerId", "['prompt1', 'prompt2']"
       );

        assertEquals(jobAidPromptRequest.getCallId(), "callId");
        assertEquals(jobAidPromptRequest.getCallerId(), "callerId");
        assertEquals(jobAidPromptRequest.getPromptList().size(), 2);
        assertEquals(jobAidPromptRequest.getPromptList().get(0), "prompt1");
        assertEquals(jobAidPromptRequest.getPromptList().get(1), "prompt2");
    }
}
