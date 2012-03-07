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

public class CertificateCourseServiceTest {
    CertificateCourseService certificateCourseService;

    @Mock
    AllCertificateCourseLogs allCertificateCourseLogs;

    @Mock
    FrontLineWorkerService frontLineWorkerService;

    @Mock
    ReportPublisherService reportPublisherService;

    @Mock
    SendSMSService sendSMSService;
    
    @Before
    public void setUp(){
        initMocks(this);
        certificateCourseService = new CertificateCourseService(allCertificateCourseLogs, frontLineWorkerService, sendSMSService);
    }
    
    @Test
    public void shouldGetAllCertificateCourseLogsForACallId(){
        String callId = "callId";
        CertificationCourseLog certificationCourseLog = new CertificationCourseLog("callerId", "calledNumber", DateTime.now(), DateTime.now(), "", callId, "courseId");
        when(allCertificateCourseLogs.findByCallId(callId)).thenReturn(certificationCourseLog);
        CertificationCourseLog courseLog = certificateCourseService.getCertificateCourseLogFor(callId);
        assertEquals(certificationCourseLog, courseLog);
    }

    @Test
    public void shouldDeleteCertificateCourseLogsForACallId(){
        String callId = "callId";
        certificateCourseService.deleteCertificateCourseLogsFor(callId);
        verify(allCertificateCourseLogs).deleteFor(callId);
    }
    
}
