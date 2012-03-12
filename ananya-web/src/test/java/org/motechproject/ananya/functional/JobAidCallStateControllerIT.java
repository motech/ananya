package org.motechproject.ananya.functional;

import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.Test;
import org.motechproject.ananya.SpringIntegrationTest;
import org.motechproject.ananya.TestUtils;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class JobAidCallStateControllerIT extends SpringIntegrationTest {

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

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
}
