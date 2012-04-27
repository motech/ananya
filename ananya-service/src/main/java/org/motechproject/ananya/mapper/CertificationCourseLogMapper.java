package org.motechproject.ananya.mapper;

import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.ananya.domain.AudioTrackerLogItem;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.request.AudioTrackerRequest;
import org.motechproject.ananya.request.CertificationCourseStateRequest;

public class CertificationCourseLogMapper {
    public CertificationCourseLog mapFrom(CertificationCourseStateRequest courseStateRequest) {
        return new CertificationCourseLog(
                courseStateRequest.getCallerId(),
                courseStateRequest.getCalledNumber(),
                "",
                courseStateRequest.getCallId(),
                courseStateRequest.getCertificateCourseId());
    }
}
