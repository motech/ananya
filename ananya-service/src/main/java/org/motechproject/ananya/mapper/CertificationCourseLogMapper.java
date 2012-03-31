package org.motechproject.ananya.mapper;

import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.request.CertificationCourseStateRequest;

public class CertificationCourseLogMapper {
    public CertificationCourseLog mapFrom(CertificationCourseStateRequest courseStateRequest, String callId) {
        return new CertificationCourseLog(
                courseStateRequest.getCallerId(),
                courseStateRequest.getCalledNumber(), null, null, "", callId,
                courseStateRequest.getCertificateCourseId());
    }
}
