package org.motechproject.ananya.mapper;

import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.contract.CertificateCourseStateRequest;

public class CertificationCourseLogMapper {
    public CertificationCourseLog mapFrom(CertificateCourseStateRequest courseStateRequest) {
        return new CertificationCourseLog(
                courseStateRequest.getCallerId(),
                courseStateRequest.getCalledNumber(),
                "",
                courseStateRequest.getCallId(),
                courseStateRequest.getCertificateCourseId());
    }
}
