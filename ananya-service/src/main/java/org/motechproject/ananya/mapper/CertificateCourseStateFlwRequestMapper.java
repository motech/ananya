package org.motechproject.ananya.mapper;

import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequest;

public class CertificateCourseStateFlwRequestMapper {

    public CertificateCourseStateFlwRequest mapFrom(final CertificationCourseStateRequest courseStateRequest) {
        return new CertificateCourseStateFlwRequest(
                courseStateRequest.getChapterIndex(),
                courseStateRequest.getLessonOrQuestionIndex(),
                courseStateRequest.isResult(),
                courseStateRequest.getInteractionKey(),
                courseStateRequest.getCallId(),
                courseStateRequest.getCallerId());
    }
}
