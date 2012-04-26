package org.motechproject.ananya.service;

import org.motechproject.ananya.action.ServiceAction;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.mapper.CertificationCourseLogItemMapper;
import org.motechproject.ananya.mapper.CertificationCourseLogMapper;
import org.motechproject.ananya.request.CertificationCourseStateRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequestList;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.service.publish.DataPublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CertificateCourseService {

    private static Logger log = LoggerFactory.getLogger(CertificateCourseService.class);

    private CertificateCourseLogService certificateCourseLogService;
    private FrontLineWorkerService frontLineWorkerService;
    private DataPublishService dataPublishService;
    private SendSMSService sendSMSService;

    @Autowired
    public CertificateCourseService(CertificateCourseLogService certificateCourseLogService,
                                    FrontLineWorkerService frontLineWorkerService,
                                    DataPublishService dataPublishService, SendSMSService sendSMSService) {
        this.certificateCourseLogService = certificateCourseLogService;
        this.frontLineWorkerService = frontLineWorkerService;
        this.dataPublishService = dataPublishService;
        this.sendSMSService = sendSMSService;
    }

    public CertificateCourseCallerDataResponse createCallerData(String msisdn, String operator) {
        log.info("Creating caller data for msisdn: " + msisdn + " for operator " + operator);

        boolean isNewFLW = frontLineWorkerService.isNewFLW(msisdn);
        FrontLineWorker frontLineWorker = frontLineWorkerService.createOrUpdatePartiallyRegistered(msisdn, operator);
        if (isNewFLW)
            dataPublishService.publishNewRegistration(msisdn);

        return new CertificateCourseCallerDataResponse(
                frontLineWorker.bookMark().asJson(),
                frontLineWorker.status().isRegistered(),
                frontLineWorker.reportCard().scoresByChapterIndex());
    }

    public void saveState(CertificationCourseStateRequestList stateRequestList) {
        log.info("State Request List " + stateRequestList);
        if (stateRequestList.isEmpty())
            return;
        saveBookmarkAndScore(stateRequestList);
        saveCourseLog(stateRequestList);
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
            sendSMSService.buildAndSendSMS(
                    frontLineWorker.getMsisdn(),
                    frontLineWorker.getLocationId(),
                    frontLineWorker.currentCourseAttempt());
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

        CertificationCourseLog courseLog = certificateCourseLogService.getLogFor(firstRequest.getCallId());
        if (courseLog == null) {
            courseLog = logMapper.mapFrom(firstRequest);
            certificateCourseLogService.createNew(courseLog);
        }
        for (CertificationCourseStateRequest stateRequest : stateRequestList.all()) {
            if (stateRequest.hasContentId())
                courseLog.addCourseLogItem(logItemMapper.mapFrom(stateRequest));
        }
        certificateCourseLogService.update(courseLog);
    }
}