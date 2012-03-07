package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.repository.AllCertificateCourseLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateCourseLogService {
    private AllCertificateCourseLogs allCertificateCourseLogs;

    @Autowired
    public CertificateCourseLogService(AllCertificateCourseLogs allCertificateCourseLogs) {
        this.allCertificateCourseLogs = allCertificateCourseLogs;
    }

    public CertificationCourseLog getCertificateCourseLogFor(String callId) {
        return allCertificateCourseLogs.findByCallId(callId);
    }

    public void deleteCertificateCourseLogsFor(String callId) {
        allCertificateCourseLogs.deleteFor(callId);
    }
}
