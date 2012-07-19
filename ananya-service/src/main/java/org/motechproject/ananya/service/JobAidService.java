package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.request.JobAidServiceRequest;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.motechproject.ananya.transformers.AllTransformers;
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
    private CallLogService callLoggerService;
    private DataPublishService dataPublishService;
    private AllTransformers allTransformers;

    @Autowired
    public JobAidService(FrontLineWorkerService frontLineWorkerService,
                         OperatorService operatorService,
                         DataPublishService dataPublishService,
                         AudioTrackerService audioTrackerService,
                         RegistrationLogService registrationLogService,
                         CallLogService callLoggerService, AllTransformers allTransformers) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.operatorService = operatorService;
        this.dataPublishService = dataPublishService;
        this.audioTrackerService = audioTrackerService;
        this.registrationLogService = registrationLogService;
        this.callLoggerService = callLoggerService;
        this.allTransformers = allTransformers;
    }

    public JobAidCallerDataResponse createCallerData(JobAidServiceRequest request) {
        String callId = request.getCallId();
        allTransformers.process(request);

        FrontLineWorker frontLineWorker = frontLineWorkerService.findForJobAidCallerData(
                request.getCallerId(),
                request.getOperator(),
                request.getCircle());
        log.info(callId + "- fetched caller data for " + frontLineWorker);

        if (frontLineWorker.isModified()) {
            registrationLogService.add(new RegistrationLog(callId,
                    request.getCallerId(),
                    request.getOperator(),
                    request.getCircle()));
            log.info(callId + "- created registrationLog");
        }
        Integer maxOperatorUsage = operatorService.findMaximumUsageFor(request.getOperator());
        return new JobAidCallerDataResponse(frontLineWorker, maxOperatorUsage);
    }

    public void handleDisconnect(JobAidServiceRequest request) {
        allTransformers.process(request);
        frontLineWorkerService.updateJobAidState(request.getCallerId(), request.getPrompts(), request.getCallDuration());
        audioTrackerService.saveAllForJobAid(request.getAudioTrackerRequestList());
        callLoggerService.saveAll(request.getCallDurationList());
        dataPublishService.publishDisconnectEvent(request.getCallId(), ServiceType.JOB_AID);
    }
}

