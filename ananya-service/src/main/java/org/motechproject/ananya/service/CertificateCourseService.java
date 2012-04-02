package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.mapper.CertificationCourseLogItemMapper;
import org.motechproject.ananya.mapper.CertificationCourseLogMapper;
import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;
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

    @Autowired
    public CertificateCourseService(CertificateCourseLogService certificateCourseLogService,
                                    FrontLineWorkerService frontLineWorkerService,
                                    DataPublishService dataPublishService) {
        this.certificateCourseLogService = certificateCourseLogService;
        this.frontLineWorkerService = frontLineWorkerService;
        this.dataPublishService = dataPublishService;
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
        saveScore(stateRequestList);
        saveBookmark(stateRequestList);
        saveCourseLog(stateRequestList);
    }

    private void saveScore(CertificationCourseStateRequestList stateRequestList) {
        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId(stateRequestList.getCallerId());
        boolean sendSMS = false;

        for (CertificationCourseStateRequest stateRequest : stateRequestList.all()) {
            String callId = stateRequest.getCallId();
            Integer chapterIndex = stateRequest.getChapterIndex();
            Integer lessonOrQuestionIndex = stateRequest.getLessonOrQuestionIndex();
            Boolean result = stateRequest.getResult();

            if (stateRequest.isStartCertificationCourseInteraction())
                frontLineWorker.reportCard().clearAllScores();
            if (stateRequest.isStartQuizInteraction())
                frontLineWorker.reportCard().clearScoresForChapterIndex(chapterIndex.toString());
            if (stateRequest.isPlayAnswerExplanationInteraction()) {
                final Score score = new Score(chapterIndex.toString(), lessonOrQuestionIndex.toString(), result, callId);
                frontLineWorker.reportCard().addScore(score);
            }
            if (stateRequest.isPlayCourseResultInteraction()) {
                frontLineWorker.incrementCertificateCourseAttempts();
                if (frontLineWorker.hasCompletedCertificateCourse()) {
                    sendSMS = true;
                    log.info("Course completion SMS sent for " + frontLineWorker);
                }
            }
        }
        frontLineWorkerService.update(new CertificateCourseStateFlwRequest(frontLineWorker, sendSMS));
    }

    private void saveBookmark(CertificationCourseStateRequestList stateRequestList) {
        CertificationCourseStateRequest stateRequest = stateRequestList.lastRequest();
        final BookMark bookMark = new BookMark(
                stateRequest.getInteractionKey(),
                stateRequest.getChapterIndex(),
                stateRequest.getLessonOrQuestionIndex());
        log.info("Saving bookmark " + bookMark);
        frontLineWorkerService.addBookMark(stateRequest.getCallerId(), bookMark);
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