package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.repository.AllCertificateCourseLogs;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class CertificateCourseLogServiceTest {
    CertificateCourseLogService certificateCourseLogService;

    @Mock
    AllCertificateCourseLogs allCertificateCourseLogs;


    @Before
    public void setUp(){
        initMocks(this);
        certificateCourseLogService = new CertificateCourseLogService(allCertificateCourseLogs);
    }

    @Test
    public void shouldGetAllCertificateCourseLogsForACallId(){
        String callId = "callId";
        CertificationCourseLog certificationCourseLog = new CertificationCourseLog("callerId", "calledNumber", DateTime.now(), DateTime.now(), "", callId, "courseId");
        when(allCertificateCourseLogs.findByCallId(callId)).thenReturn(certificationCourseLog);
        CertificationCourseLog courseLog = certificateCourseLogService.getCertificateCourseLogFor(callId);
        assertEquals(certificationCourseLog, courseLog);
    }

    @Test
    public void shouldDeleteCertificateCourseLogsForACallId(){
        String callId = "callId";
        certificateCourseLogService.deleteCertificateCourseLogsFor(callId);
        verify(allCertificateCourseLogs).deleteFor(callId);
    }
}
