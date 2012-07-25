package org.motechproject.ananya.service;

import org.motechproject.ananya.contract.FrontLineWorkerCreateResponse;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.contract.JobAidServiceRequest;
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

    public JobAidCallerDataResponse getCallerData(JobAidServiceRequest request) {
        allTransformers.process(request);

        FrontLineWorker frontLineWorker = frontLineWorkerService.findForJobAidCallerData(request.getCallerId());
        log.info(request.getCallId() + "- fetched caller data for " + frontLineWorker);

        Integer maxOperatorUsage = operatorService.findMaximumUsageFor(request.getOperator());
        return (frontLineWorker != null)
                ? new JobAidCallerDataResponse(frontLineWorker, maxOperatorUsage)
                : JobAidCallerDataResponse.forNewUser(maxOperatorUsage);
    }

    public void handleDisconnect(JobAidServiceRequest request) {
        allTransformers.process(request);

        FrontLineWorkerCreateResponse frontLineWorkerCreateResponse = frontLineWorkerService.createOrUpdateForCall(
                request.getCallerId(), request.getOperator(), request.getCircle());

        if (frontLineWorkerCreateResponse.isModified()) {
            registrationLogService.add(new RegistrationLog(
                    request.getCallId(), request.getCallerId(), request.getOperator(), request.getCircle()));
        }

        frontLineWorkerService.updateJobAidState(
                frontLineWorkerCreateResponse.getFrontLineWorker(), request.getPrompts(), request.getCallDuration());
        audioTrackerService.saveAllForJobAid(request.getAudioTrackerRequestList());
        callLoggerService.saveAll(request.getCallDurationList());
        dataPublishService.publishDisconnectEvent(request.getCallId(), ServiceType.JOB_AID);
    }
}

