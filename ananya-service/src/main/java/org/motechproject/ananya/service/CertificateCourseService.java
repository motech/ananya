package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequestList;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CertificateCourseService {

    private static Logger log = LoggerFactory.getLogger(CertificateCourseService.class);

    private CertificateCourseLogService certificateCourseLogService;
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public CertificateCourseService(CertificateCourseLogService certificateCourseLogService,
                                    FrontLineWorkerService frontLineWorkerService) {
        this.certificateCourseLogService = certificateCourseLogService;
        this.frontLineWorkerService = frontLineWorkerService;
    }

    private void saveBookmark(CertificationCourseStateRequest courseStateRequest) {
        final BookMark bookMark = new BookMark(courseStateRequest.getInteractionKey(),
                courseStateRequest.getChapterIndex(),
                courseStateRequest.getLessonOrQuestionIndex());

        log.info("Saving bookmark " + bookMark);
        frontLineWorkerService.addBookMark(courseStateRequest.getCallerId(), bookMark);
    }

    public void saveState(CertificationCourseStateRequestList stateRequestList) {
        log.info("State Request List " + stateRequestList);
        if (stateRequestList.isEmpty()) return;

        for (CertificationCourseStateRequest stateRequest : stateRequestList.all())
            frontLineWorkerService.saveScore(createCertificateCourseStateFlwRequest(stateRequest));

        saveBookmark(stateRequestList.recentRequest());
        saveCourseCertificateLog(stateRequestList.all());
    }

    public CertificateCourseCallerDataResponse createCallerData(String msisdn, String operator) {
        log.info("Creating caller data for msisdn: " + msisdn + " for operator " + operator);

        FrontLineWorker frontLineWorker = frontLineWorkerService.createOrUpdatePartiallyRegistered(msisdn, operator);
        return new CertificateCourseCallerDataResponse(
                frontLineWorker.bookMark().asJson(),
                frontLineWorker.status().isRegistered(),
                frontLineWorker.reportCard().scoresByChapterIndex());
    }

    private CertificateCourseStateFlwRequest createCertificateCourseStateFlwRequest(final CertificationCourseStateRequest courseStateRequest) {
        return new CertificateCourseStateFlwRequest(
                courseStateRequest.getChapterIndex(),
                courseStateRequest.getLessonOrQuestionIndex(),
                courseStateRequest.isResult(),
                courseStateRequest.getInteractionKey(),
                courseStateRequest.getCallId(),
                courseStateRequest.getCallerId());
    }

    private void saveCourseCertificateLog(List<CertificationCourseStateRequest> stateRequestList) {
        CertificationCourseLog courseLogDocument;
        CertificationCourseStateRequest courseStateRequest = stateRequestList.get(0);
        final String callId = courseStateRequest.getCallId();

        courseLogDocument = certificateCourseLogService.getCertificateCourseLogFor(callId);
        if (courseLogDocument == null) {
            courseLogDocument = new CertificationCourseLog(
                    courseStateRequest.getCallerId(),
                    courseStateRequest.getCalledNumber(), null, null, "", callId,
                    courseStateRequest.getCertificateCourseId());
            certificateCourseLogService.createNew(courseLogDocument);
        }
        for (CertificationCourseStateRequest stateRequest : stateRequestList) {
            if (StringUtils.isNotBlank(stateRequest.getContentId())) {
                CertificationCourseLogItem courseLogItem = new CertificationCourseLogItem(
                        stateRequest.getContentId(),
                        CourseItemType.valueOf(stateRequest.getContentType().toUpperCase()),
                        stateRequest.getContentName(),
                        stateRequest.getContentData(),
                        CourseItemState.valueOf(stateRequest.getCourseItemState().toUpperCase()),
                        stateRequest.getTimeAsDateTime()
                );
                courseLogDocument.addCourseLogItem(courseLogItem);
            }
        }
        certificateCourseLogService.update(courseLogDocument);
    }
}