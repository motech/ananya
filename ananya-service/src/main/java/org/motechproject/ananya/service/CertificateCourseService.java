package org.motechproject.ananya.service;

import org.motechproject.ananya.action.ServiceAction;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.mapper.CertificationCourseLogItemMapper;
import org.motechproject.ananya.mapper.CertificationCourseLogMapper;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.CertificationCourseStateRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequestList;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CertificateCourseService {

    private static Logger log = LoggerFactory.getLogger(CertificateCourseService.class);

    private CertificateCourseLogService certificateCourseLogService;
    private FrontLineWorkerService frontLineWorkerService;
    private AudioTrackerService audioTrackerService;
    private RegistrationLogService registrationLogService;
    private SMSLogService smsLogService;

    @Autowired
    public CertificateCourseService(CertificateCourseLogService certificateCourseLogService,
                                    AudioTrackerService audioTrackerService,
                                    FrontLineWorkerService frontLineWorkerService,
                                    RegistrationLogService registrationLogService, SMSLogService smsLogService) {
        this.certificateCourseLogService = certificateCourseLogService;
        this.frontLineWorkerService = frontLineWorkerService;
        this.audioTrackerService = audioTrackerService;
        this.registrationLogService = registrationLogService;
        this.smsLogService = smsLogService;
    }

    public CertificateCourseCallerDataResponse createCallerData(String callId, String msisdn, String operator, String circle) {
        log.info("Creating caller data for msisdn: " + msisdn + " for operator " + operator + " for circle" + circle
                + " for callId " + callId);

        FrontLineWorker frontLineWorker = frontLineWorkerService.createOrUpdateUnregistered(msisdn, operator, circle);
        if (frontLineWorker.isModified())
            registrationLogService.add(new RegistrationLog(callId, msisdn, operator, circle));

        return new CertificateCourseCallerDataResponse(
                frontLineWorker.bookMark().asJson(),
                frontLineWorker.getStatus().isRegistered(),
                frontLineWorker.reportCard().scoresByChapterIndex());
    }

    public void saveState(CertificationCourseStateRequestList stateRequestList) {
        log.info("State Request List " + stateRequestList);
        if (stateRequestList.isEmpty())
            return;
        saveBookmarkAndScore(stateRequestList);
        saveCourseLog(stateRequestList);
    }

    public void saveAudioTrackerState(AudioTrackerRequestList audioTrackerRequestList) {
        audioTrackerService.saveAudioTrackerState(audioTrackerRequestList, ServiceType.CERTIFICATE_COURSE);
    }

    private void saveBookmarkAndScore(CertificationCourseStateRequestList stateRequestList) {
        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(stateRequestList.getCallerId());
        addBookmark(frontLineWorker, stateRequestList);

        for (CertificationCourseStateRequest stateRequest : stateRequestList.all()) {
            ServiceAction serviceAction = ServiceAction.findFor(stateRequest.getInteractionKey());
            serviceAction.update(frontLineWorker, stateRequest);
        }
        frontLineWorkerService.updateCertificateCourseStateFor(frontLineWorker);

        if (frontLineWorker.hasPassedTheCourse() && stateRequestList.hasCourseCompletionInteraction()) {
            smsLogService.add(
                    new SMSLog(
                            stateRequestList.getCallId(),
                            frontLineWorker.getMsisdn(),
                            frontLineWorker.getLocationId(),
                            frontLineWorker.currentCourseAttempt()
                    )
            );
            log.info("Course completion SMS sent for " + frontLineWorker);
        }
    }

    private void addBookmark(FrontLineWorker frontLineWorker, CertificationCourseStateRequestList stateRequestList) {
        CertificationCourseStateRequest lastRequest = stateRequestList.lastRequest();
        final BookMark bookMark = new BookMark(lastRequest.getInteractionKey(), lastRequest.getChapterIndex(), lastRequest.getLessonOrQuestionIndex());
        frontLineWorker.addBookMark(bookMark);
    }

    private void saveCourseLog(CertificationCourseStateRequestList stateRequestList) {
        CertificationCourseStateRequest firstRequest = stateRequestList.firstRequest();
        CertificationCourseLogMapper logMapper = new CertificationCourseLogMapper();
        CertificationCourseLogItemMapper logItemMapper = new CertificationCourseLogItemMapper();

        CertificationCourseLog courseLog = logMapper.mapFrom(firstRequest);
        for (CertificationCourseStateRequest stateRequest : stateRequestList.all()) {
            if (stateRequest.hasContentId())
                courseLog.addCourseLogItem(logItemMapper.mapFrom(stateRequest));
        }
        certificateCourseLogService.createNew(courseLog);
    }
}