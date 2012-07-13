package org.motechproject.ananya.service;

import org.motechproject.ananya.action.AllCourseActions;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.request.CertificateCourseServiceRequest;
import org.motechproject.ananya.request.CertificateCourseStateRequestList;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.motechproject.ananya.transformers.AllTransformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CertificateCourseService {

    private static Logger log = LoggerFactory.getLogger(CertificateCourseService.class);

    private FrontLineWorkerService frontLineWorkerService;
    private RegistrationLogService registrationLogService;
    private AudioTrackerService audioTrackerService;
    private CallLogService callLoggerService;

    private AllTransformers allTransformers;
    private AllCourseActions allCourseActions;
    private DataPublishService dataPublishService;

    @Autowired
    public CertificateCourseService(AudioTrackerService audioTrackerService, FrontLineWorkerService frontLineWorkerService,
                                    RegistrationLogService registrationLogService, CallLogService callLoggerService,
                                    DataPublishService dataPublishService, AllTransformers allTransformers,
                                    AllCourseActions allCourseActions) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.audioTrackerService = audioTrackerService;
        this.registrationLogService = registrationLogService;
        this.callLoggerService = callLoggerService;
        this.dataPublishService = dataPublishService;
        this.allTransformers = allTransformers;
        this.allCourseActions = allCourseActions;
    }

    public CertificateCourseCallerDataResponse createCallerData(CertificateCourseServiceRequest request) {
        String callId = request.getCallId();
        allTransformers.process(request);

        FrontLineWorker frontLineWorker = frontLineWorkerService.createOrUpdateForCall(
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
        return new CertificateCourseCallerDataResponse(frontLineWorker);
    }

    public void handleDisconnect(CertificateCourseServiceRequest request) {
        allTransformers.process(request);

        CertificateCourseStateRequestList stateRequestList = request.getCertificateCourseStateRequestList();
        if (stateRequestList.isNotEmpty()) {
            FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(stateRequestList.getCallerId());
            allCourseActions.execute(frontLineWorker, stateRequestList);
        }
        audioTrackerService.saveAllForCourse(request.getAudioTrackerRequestList());
        callLoggerService.saveAll(request.getCallDurationList());
        dataPublishService.publishDisconnectEvent(request.getCallId(), ServiceType.CERTIFICATE_COURSE);
    }

}