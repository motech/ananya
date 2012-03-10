package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllCertificateCourseLogs;
import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CertificateCourseService {
    private AllCertificateCourseLogs allCertificateCourseLogs;
    private FrontLineWorkerService frontLineWorkerService;
    private SendSMSService sendSMSService;

    @Autowired
    public CertificateCourseService(AllCertificateCourseLogs allCertificateCourseLogs,
                                    FrontLineWorkerService frontLineWorkerService,
                                    SendSMSService sendSMSService) {
        this.allCertificateCourseLogs = allCertificateCourseLogs;
        this.frontLineWorkerService = frontLineWorkerService;
        this.sendSMSService = sendSMSService;
    }

    private void saveBookmark(CertificationCourseStateRequest courseStateRequest) {
        final BookMark bookMark = new BookMark(courseStateRequest.getInteractionKey(),
                                               courseStateRequest.getChapterIndex(),
                                               courseStateRequest.getLessonOrQuestionIndex());
        frontLineWorkerService.addBookMark(courseStateRequest.getCallerId(), bookMark);
    }

    public void saveState(List<CertificationCourseStateRequest> certificationCourseStateRequestCollection) {

        if (certificationCourseStateRequestCollection == null || certificationCourseStateRequestCollection.size() == 0)
            return;

        for (CertificationCourseStateRequest certificationCourseStateRequest : certificationCourseStateRequestCollection) {
            CertificateCourseStateFlwRequest certificateCourseStateFlwRequest = createCertificateCourseStateFlwRequest(certificationCourseStateRequest);
            frontLineWorkerService.saveScore(certificateCourseStateFlwRequest);
        }

        CertificationCourseStateRequest recentCourseRequest =
                certificationCourseStateRequestCollection.get(certificationCourseStateRequestCollection.size() - 1);
        saveBookmark(recentCourseRequest);
        saveCourseCertificateLog(certificationCourseStateRequestCollection);
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

    private void saveCourseCertificateLog(List<CertificationCourseStateRequest> certificationCourseStateRequestCollection) {
        CertificationCourseLog courseLogDocument;
        CertificationCourseStateRequest courseStateRequest = certificationCourseStateRequestCollection.get(0);
        final String callId = courseStateRequest.getCallId();

        courseLogDocument = allCertificateCourseLogs.findByCallId(callId);
        if (courseLogDocument == null) {
            courseLogDocument = new CertificationCourseLog(courseStateRequest.getCallerId(),
                    courseStateRequest.getCalledNumber(), null, null, "", callId, courseStateRequest.getCertificateCourseId()
            );
            allCertificateCourseLogs.add(courseLogDocument);
        }

        for (CertificationCourseStateRequest certificationCourseStateRequest : certificationCourseStateRequestCollection) {
            // Only a bookmark, not a log item. continue
            if (!StringUtils.isBlank(certificationCourseStateRequest.getContentId())) {
                CertificationCourseLogItem courseLogItem = new CertificationCourseLogItem(
                        certificationCourseStateRequest.getContentId(),
                        CourseItemType.valueOf(certificationCourseStateRequest.getContentType().toUpperCase()),
                        certificationCourseStateRequest.getContentName(),
                        certificationCourseStateRequest.getContentData(),
                        CourseItemState.valueOf(certificationCourseStateRequest.getCourseItemState().toUpperCase()),
                        certificationCourseStateRequest.getTimeAsDateTime()
                );

                courseLogDocument.addCourseLogItem(courseLogItem);
            }
        }
        allCertificateCourseLogs.update(courseLogDocument);
    }
}