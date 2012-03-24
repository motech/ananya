package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.request.JobAidPromptRequest;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobAidService {

    private static Logger log = LoggerFactory.getLogger(JobAidService.class);
    
    private FrontLineWorkerService frontLineWorkerService;
    private OperatorService operatorService;
    private DataPublishService dataPublishService;

    @Autowired
    public JobAidService(FrontLineWorkerService frontLineWorkerService,
                         OperatorService operatorService,
                         DataPublishService dataPublishService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.operatorService = operatorService;
        this.dataPublishService = dataPublishService;
    }

    public void updateJobAidPrompts(JobAidPromptRequest jobAidPromptRequest) {
        frontLineWorkerService.updatePromptsForFLW(
                jobAidPromptRequest.getCallerId(),
                jobAidPromptRequest.getPromptList());
    }

    public JobAidCallerDataResponse createCallerData(String callerId, String operator) {
        log.info("Creating caller data for msisdn: " + callerId + " for operator " + operator);

        FrontLineWorker frontLineWorker = frontLineWorkerService.getFLWForJobAidCallerData(callerId, operator);
        dataPublishService.publishNewRegistration(callerId);

        Integer currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage();
        Integer allowedUsagePerMonthForOperator = operatorService.findMaximumUsageFor(operator);

        return new JobAidCallerDataResponse(
                frontLineWorker.status().isRegistered(),
                currentJobAidUsage,
                allowedUsagePerMonthForOperator,
                frontLineWorker.getPromptsHeard());
    }

    public void updateCurrentUsageAndSetLastAccessTimeForUser(String msisdn, Integer callDuration) {
        frontLineWorkerService.updateCurrentUsageForUser(msisdn, callDuration);
        frontLineWorkerService.updateLastJobAidAccessTime(msisdn);
    }

}
