package org.motechproject.ananya.functional;

import com.gargoylesoftware.htmlunit.WebClient;
import org.joda.time.DateTime;
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
}
