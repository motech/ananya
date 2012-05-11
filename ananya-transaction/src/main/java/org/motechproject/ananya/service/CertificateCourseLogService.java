package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.repository.AllCertificateCourseLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateCourseLogService {
    private AllCertificateCourseLogs allCertificateCourseLogs;

    @Autowired
    public CertificateCourseLogService(AllCertificateCourseLogs allCertificateCourseLogs) {
        this.allCertificateCourseLogs = allCertificateCourseLogs;
    }

    public CertificationCourseLog getLogFor(String callId) {
        return allCertificateCourseLogs.findByCallId(callId);
    }

    public void createNew(CertificationCourseLog courseLog) {
        allCertificateCourseLogs.add(courseLog);
    }

    public void update(CertificationCourseLog courseLog) {
        allCertificateCourseLogs.update(courseLog);
    }

    public void remove(CertificationCourseLog courseLog) {
        allCertificateCourseLogs.remove(courseLog);
    }

    public List<CertificationCourseLog> getAll() {
        return allCertificateCourseLogs.getAll();
    }

    public void removeAll() {
        allCertificateCourseLogs.removeAll();
    }
}
