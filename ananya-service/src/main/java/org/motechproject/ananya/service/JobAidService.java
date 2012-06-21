package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.request.JobAidServiceRequest;
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
    private AudioTrackerService audioTrackerService;
    private RegistrationLogService registrationLogService;
    private CallLoggerService callLoggerService;
    private DataPublishService dataPublishService;

    @Autowired
    public JobAidService(FrontLineWorkerService frontLineWorkerService,
                         OperatorService operatorService,
                         DataPublishService dataPublishService,
                         AudioTrackerService audioTrackerService,
                         RegistrationLogService registrationLogService, CallLoggerService callLoggerService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.operatorService = operatorService;
        this.dataPublishService = dataPublishService;
        this.audioTrackerService = audioTrackerService;
        this.registrationLogService = registrationLogService;
        this.callLoggerService = callLoggerService;
    }

    public JobAidCallerDataResponse createCallerData(String callId, String callerId, String operator, String circle) {
        log.info(callId + "- fetching caller data |" + callerId + "|" + operator + "|" + circle);

        FrontLineWorker frontLineWorker = frontLineWorkerService.findForJobAidCallerData(callerId, operator, circle);
        if (frontLineWorker.isModified())
            registrationLogService.add(new RegistrationLog(callId, callerId, operator, circle));

        return new JobAidCallerDataResponse(frontLineWorker.status().isRegistered(), frontLineWorker.getCurrentJobAidUsage(),
                operatorService.findMaximumUsageFor(operator), frontLineWorker.getPromptsHeard());
    }

    public void handleDisconnect(JobAidServiceRequest jobAidServiceRequest) {
        frontLineWorkerService.updateJobAidUsageAndAccessTime(jobAidServiceRequest.getCallerId(), jobAidServiceRequest.getCallDuration());
        frontLineWorkerService.updatePromptsFor(jobAidServiceRequest.getCallerId(), jobAidServiceRequest.getPrompts());
        audioTrackerService.saveAudioTrackerState(jobAidServiceRequest.getAudioTrackerRequestList(), ServiceType.JOB_AID);
        callLoggerService.saveAll(jobAidServiceRequest.getCallDurationList());
        dataPublishService.publishCallDisconnectEvent(jobAidServiceRequest.getCallId(), ServiceType.JOB_AID);
    }
}

