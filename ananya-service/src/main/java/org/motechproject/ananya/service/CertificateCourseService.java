package org.motechproject.ananya.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequest;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CertificateCourseService {

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
        frontLineWorkerService.addBookMark(courseStateRequest.getCallerId(), bookMark);
    }

    public void saveState(List<CertificationCourseStateRequest> stateRequestList) {
        if (CollectionUtils.isEmpty(stateRequestList))
            return;
        for (CertificationCourseStateRequest stateRequest : stateRequestList)
            frontLineWorkerService.saveScore(createCertificateCourseStateFlwRequest(stateRequest));

        CertificationCourseStateRequest recentCourseRequest = stateRequestList.get(stateRequestList.size() - 1);
        saveBookmark(recentCourseRequest);
        saveCourseCertificateLog(stateRequestList);
    }

    public CertificateCourseCallerDataResponse createCallerData(String msisdn, String operator) {
        FrontLineWorker frontLineWorker = frontLineWorkerService.createNew(msisdn, operator);
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