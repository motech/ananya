package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.request.AudioTrackerRequestList;
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
    private AudioTrackerService audioTrackerService;

    @Autowired
    public JobAidService(FrontLineWorkerService frontLineWorkerService,
                         OperatorService operatorService,
                         DataPublishService dataPublishService,
                         AudioTrackerService audioTrackerService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.operatorService = operatorService;
        this.dataPublishService = dataPublishService;
        this.audioTrackerService = audioTrackerService;
    }

    public void updateJobAidPrompts(JobAidPromptRequest jobAidPromptRequest) {
        frontLineWorkerService.updatePromptsFor(
                jobAidPromptRequest.getCallerId(),
                jobAidPromptRequest.getPromptList());
    }

    public JobAidCallerDataResponse createCallerData(String callerId, String operator) {
        log.info("Creating caller data for msisdn: " + callerId + " for operator " + operator);

        boolean isNewFLW = frontLineWorkerService.isNewFLW(callerId);
        FrontLineWorker frontLineWorker = frontLineWorkerService.getFLWForJobAidCallerData(callerId, operator);
        if (isNewFLW)
            dataPublishService.publishNewRegistration(callerId);

        Integer allowedUsagePerMonthForOperator = operatorService.findMaximumUsageFor(operator);

        return new JobAidCallerDataResponse(
                frontLineWorker.status().isRegistered(),
                frontLineWorker.getCurrentJobAidUsage(),
                allowedUsagePerMonthForOperator,
                frontLineWorker.getPromptsHeard());
    }

    public void updateCurrentUsageAndSetLastAccessTimeForUser(String callerId, Integer callDuration) {
        frontLineWorkerService.updateJobAidCurrentUsageFor(callerId, callDuration);
        frontLineWorkerService.updateJobAidLastAccessTime(callerId);
    }

    public void saveAudioTrackerState(AudioTrackerRequestList audioTrackerRequestList) {
        audioTrackerService.saveAudioTrackerState(audioTrackerRequestList, ServiceType.JOB_AID);
    }
}

