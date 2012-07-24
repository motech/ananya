
package org.motechproject.ananya.service;

import org.motechproject.ananya.action.AllCourseActions;
import org.motechproject.ananya.contract.FrontLineWorkerCreateResponse;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.CertificateCourseStateRequestList;
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
        allTransformers.process(request);

        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(request.getCallerId());

        log.info(request.getCallId() + "- fetched caller data for " + frontLineWorker);

        return (frontLineWorker != null)
                ? new CertificateCourseCallerDataResponse(frontLineWorker)
                : CertificateCourseCallerDataResponse.blankCertificateCourseCallerDataResponse();
    }

    public void handleDisconnect(CertificateCourseServiceRequest request) {
        allTransformers.process(request);

        FrontLineWorkerCreateResponse frontLineWorkerCreateResponse = frontLineWorkerService.createOrUpdateForCall(
                request.getCallerId(), request.getOperator(), request.getCircle());

        if (frontLineWorkerCreateResponse.isModified()) {
            registrationLogService.add(new RegistrationLog(
                    request.getCallId(), request.getCallerId(), request.getOperator(), request.getCircle()));
        }

        CertificateCourseStateRequestList stateRequestList = request.getCertificateCourseStateRequestList();
        if (stateRequestList.isNotEmpty()) {
            allCourseActions.execute(frontLineWorkerCreateResponse.getFrontLineWorker(), stateRequestList);
        }
        audioTrackerService.saveAllForCourse(request.getAudioTrackerRequestList());
        callLoggerService.saveAll(request.getCallDurationList());
        dataPublishService.publishDisconnectEvent(request.getCallId(), ServiceType.CERTIFICATE_COURSE);
    }

}