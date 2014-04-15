package org.motechproject.ananya.service;

import org.motechproject.ananya.action.AllCourseActions;
import org.motechproject.ananya.contract.FrontLineWorkerCreateResponse;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.response.CertificateCourseCallerDataWithUsageForCappingResponse;
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
    private OperatorService operatorService;

    private AllTransformers allTransformers;
    private AllCourseActions allCourseActions;
    private DataPublishService dataPublishService;

    @Autowired
    public CertificateCourseService(AudioTrackerService audioTrackerService, FrontLineWorkerService frontLineWorkerService,
                                    RegistrationLogService registrationLogService, CallLogService callLoggerService,
                                    DataPublishService dataPublishService, AllTransformers allTransformers,
                                    AllCourseActions allCourseActions, OperatorService operatorService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.audioTrackerService = audioTrackerService;
        this.registrationLogService = registrationLogService;
        this.callLoggerService = callLoggerService;
        this.dataPublishService = dataPublishService;
        this.allTransformers = allTransformers;
        this.allCourseActions = allCourseActions;
        this.operatorService = operatorService;
    }

    public CertificateCourseCallerDataResponse createCallerData(CertificateCourseServiceRequest request) {
        allTransformers.process(request);
        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(request.getCallerId());
        log.info(request.getCallId() + "- fetched caller data for " + request.getCallerId());
        return (frontLineWorker != null)
                ? new CertificateCourseCallerDataResponse(frontLineWorker)
                : CertificateCourseCallerDataResponse.forNewUser();
    }
    
    public CertificateCourseCallerDataWithUsageForCappingResponse createCallerDataWithUsage(CertificateCourseServiceRequest request) {
        allTransformers.process(request);
        FrontLineWorker frontLineWorker = frontLineWorkerService.findForCourseCallerDataWithUsage(request.getCallerId());
        Integer maxOperatorUsage = operatorService.findMaximumUsageFor(request.getOperator(),request.getCircle());
  
        log.info(request.getCallId() + "- fetched caller data for " + request.getCallerId()+" with operator info max usage as "+maxOperatorUsage);
        return (frontLineWorker != null)
                ? new CertificateCourseCallerDataWithUsageForCappingResponse(frontLineWorker,maxOperatorUsage)
                : CertificateCourseCallerDataWithUsageForCappingResponse.forNewUser(maxOperatorUsage);
    }

    public void handleDisconnect(CertificateCourseServiceRequest request) {
        allTransformers.process(request);

        FrontLineWorkerCreateResponse frontLineWorkerCreateResponse = frontLineWorkerService.createOrUpdateForCall(request.getCallerId(), request.getOperator(), request.getCircle(), request.getLanguage());
        if (frontLineWorkerCreateResponse.isModified())
            registrationLogService.add(new RegistrationLog(request.getCallId(), request.getCallerId(), request.getOperator(), request.getCircle()));

        CertificateCourseStateRequestList stateRequestList = request.getCertificateCourseStateRequestList();
        if (stateRequestList.isNotEmpty()) {
            FrontLineWorker frontLineWorker = frontLineWorkerCreateResponse.getFrontLineWorker();
            allCourseActions.execute(frontLineWorker, stateRequestList);
            frontLineWorkerService.updateCertificateCourseState(frontLineWorker);
        }

        audioTrackerService.saveAllForCourse(request.getAudioTrackerRequestList());
        callLoggerService.saveAll(request.getCallDurationList());
        dataPublishService.publishDisconnectEvent(request.getCallId(), ServiceType.CERTIFICATE_COURSE);
    }
    
    public void handleDisconnectWithCapping(CertificateCourseServiceRequest request) {
        allTransformers.process(request);

        FrontLineWorkerCreateResponse frontLineWorkerCreateResponse = frontLineWorkerService.createOrUpdateForCall(request.getCallerId(), request.getOperator(), request.getCircle(), request.getLanguage());
        if (frontLineWorkerCreateResponse.isModified())
            registrationLogService.add(new RegistrationLog(request.getCallId(), request.getCallerId(), request.getOperator(), request.getCircle()));

        CertificateCourseStateRequestList stateRequestList = request.getCertificateCourseStateRequestList();
        if (stateRequestList.isNotEmpty()) {
            FrontLineWorker frontLineWorker = frontLineWorkerCreateResponse.getFrontLineWorker();
            allCourseActions.execute(frontLineWorker, stateRequestList);
            frontLineWorkerService.updateCertificateCourseState(frontLineWorker);
        }
        frontLineWorkerService.updateCCState( frontLineWorkerCreateResponse.getFrontLineWorker(), request.getPrompts(), request.getCcCallDuration());
        audioTrackerService.saveAllForCourse(request.getAudioTrackerRequestList());
        callLoggerService.saveAll(request.getCallDurationList());
        dataPublishService.publishDisconnectEvent(request.getCallId(), ServiceType.CERTIFICATE_COURSE);
    }

}