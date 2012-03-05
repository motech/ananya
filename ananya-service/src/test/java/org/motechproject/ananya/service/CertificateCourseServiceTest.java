package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.LogData;
import org.motechproject.ananya.domain.LogType;
import org.motechproject.ananya.repository.AllCertificationCourseLogs;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CertificateCourseServiceTest {
    CertificateCourseService certificateCourseService;

    @Autowired
    AllCertificationCourseLogs allCertificationCourseLogs;

    @Autowired
    FrontLineWorkerService frontLineWorkerService;

    @Mock
    ReportPublisherService reportPublisherService;
    
    @Before
    public void setUp(){
        initMocks(this);
        certificateCourseService = new CertificateCourseService(allCertificationCourseLogs, frontLineWorkerService, reportPublisherService);
    }
    
    @Test
    public void shouldPublishCertificateCourseData(){
        String callId = "callId";
        certificateCourseService.publishCertificateCourseData(callId);
        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(reportPublisherService).publishCertificateCourseData(captor.capture());

        LogData logData = captor.getValue();
        assertEquals(callId, logData.getDataId());
        assertEquals(LogType.CERTIFICATE_COURSE_DATA, logData.getType());
    }
}
