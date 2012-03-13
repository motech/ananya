package org.motechproject.ananya.service;

import org.motechproject.ananya.request.JobAidPromptRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobAidService {
   
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public JobAidService(FrontLineWorkerService frontLineWorkerService) {
        this.frontLineWorkerService = frontLineWorkerService;
    }
    
    public void updateJobAidPrompts(JobAidPromptRequest jobAidPromptRequest) {
        frontLineWorkerService.updatePromptsForFLW(
                jobAidPromptRequest.getCallerId(), jobAidPromptRequest.getPromptList());
    }
}
