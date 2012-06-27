package org.motechproject.ananya.service;

import org.motechproject.ananya.action.CertificateCourseServiceAction;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.mapper.CertificationCourseLogItemMapper;
import org.motechproject.ananya.mapper.CertificationCourseLogMapper;
import org.motechproject.ananya.request.CertificateCourseServiceRequest;
import org.motechproject.ananya.request.CertificateCourseStateRequest;
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

    private CertificateCourseLogService certificateCourseLogService;
    private RegistrationLogService registrationLogService;
    private FrontLineWorkerService frontLineWorkerService;
    private AudioTrackerService audioTrackerService;
    private DataPublishService dataPublishService;
    private CallLoggerService callLoggerService;
    private SMSLogService smsLogService;
    private AllTransformers allTransformers;

    @Autowired
    public CertificateCourseService(CertificateCourseLogService certificateCourseLogService,
                                    AudioTrackerService audioTrackerService, FrontLineWorkerService frontLineWorkerService,
                                    RegistrationLogService registrationLogService, SMSLogService smsLogService,
                                    CallLoggerService callLoggerService, DataPublishService dataPublishService, AllTransformers allTransformers) {
        this.certificateCourseLogService = certificateCourseLogService;
        this.frontLineWorkerService = frontLineWorkerService;
        this.audioTrackerService = audioTrackerService;
        this.registrationLogService = registrationLogService;
        this.smsLogService = smsLogService;
        this.callLoggerService = callLoggerService;
        this.dataPublishService = dataPublishService;
        this.allTransformers = allTransformers;
    }

    public CertificateCourseCallerDataResponse createCallerData(CertificateCourseServiceRequest request) {
        String callId = request.getCallId();
        allTransformers.process(request);

        FrontLineWorker frontLineWorker = frontLineWorkerService.createOrUpdateUnregistered(
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
            updateBookmark(frontLineWorker, stateRequestList);
            updateScores(frontLineWorker, stateRequestList);
            sendSMS(frontLineWorker, stateRequestList);
            createCourseLog(stateRequestList);
        }
        audioTrackerService.saveAllForCourse(request.getAudioTrackerRequestList());
        callLoggerService.saveAll(request.getCallDurationList());
        dataPublishService.publishDisconnectEvent(request.getCallId(), ServiceType.CERTIFICATE_COURSE);
    }

    private void updateBookmark(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList stateRequestList) {
        String callId = stateRequestList.getCallId();
        CertificateCourseStateRequest lastRequest = stateRequestList.lastRequest();

        final BookMark bookMark = new BookMark(
                lastRequest.getInteractionKey(),
                lastRequest.getChapterIndex(),
                lastRequest.getLessonOrQuestionIndex());

        frontLineWorker.addBookMark(bookMark);
        log.info(callId + "- updated bookmark for " + frontLineWorker);
    }

    private void updateScores(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList stateRequestList) {
        String callId = stateRequestList.getCallId();
        for (CertificateCourseStateRequest stateRequest : stateRequestList.all()) {
            CertificateCourseServiceAction serviceAction = CertificateCourseServiceAction.findFor(stateRequest.getInteractionKey());
            serviceAction.update(frontLineWorker, stateRequest);
        }
        frontLineWorkerService.updateCertificateCourseStateFor(frontLineWorker);
        log.info(callId + "- updated scores for " + frontLineWorker);
    }

    private void sendSMS(FrontLineWorker frontLineWorker, CertificateCourseStateRequestList stateRequestList) {
        if (frontLineWorker.hasPassedTheCourse() && stateRequestList.hasCourseCompletionInteraction()) {
            String callId = stateRequestList.getCallId();
            SMSLog smsLog = new SMSLog(stateRequestList.getCallId(),
                    frontLineWorker.getMsisdn(),
                    frontLineWorker.getLocationId(),
                    frontLineWorker.currentCourseAttempt());
            smsLogService.add(smsLog);
            log.info(callId + "- course completion sms sent for " + frontLineWorker);
        }
    }

    private void createCourseLog(CertificateCourseStateRequestList stateRequestList) {
        String callId = stateRequestList.getCallId();
        String callerId = stateRequestList.getCallerId();

        CertificationCourseLogMapper logMapper = new CertificationCourseLogMapper();
        CertificationCourseLogItemMapper logItemMapper = new CertificationCourseLogItemMapper();
        CertificationCourseLog courseLog = logMapper.mapFrom(stateRequestList.firstRequest());

        for (CertificateCourseStateRequest stateRequest : stateRequestList.all())
            if (stateRequest.hasContentId())
                courseLog.addCourseLogItem(logItemMapper.mapFrom(stateRequest));

        certificateCourseLogService.createNew(courseLog);
        log.info(callId + "- courseLog saved for " + callerId);
    }
}