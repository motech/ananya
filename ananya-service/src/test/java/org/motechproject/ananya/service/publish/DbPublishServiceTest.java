package org.motechproject.ananya.service.publish;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.*;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DbPublishServiceTest {
    @Mock
    private RegistrationMeasureService registrationMeasureService;
    @Mock
    private CourseItemMeasureService courseItemMeasureService;
    @Mock
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private SMSSentMeasureService smsSentMeasureService;
    @Mock
    private JobAidContentMeasureService jobAidContentMeasureService;

    private DbPublishService dbPublishService;

    @Before
    public void setUp() {
        initMocks(this);
        dbPublishService = new DbPublishService(registrationMeasureService, courseItemMeasureService,
                callDurationMeasureService, smsSentMeasureService, jobAidContentMeasureService);
    }

    @Test
    public void shouldPublishSMSSent() throws Exception {
        String callerId = "1234";
        LogData logData = new LogData(LogType.SMS_SENT, callerId);

        dbPublishService.publishSMSSent(logData);

        verify(smsSentMeasureService).createSMSSentMeasure(callerId);
    }

    @Test
    public void shouldPublishCallDisconnectEventForCertificateCourse() throws Exception {
        String callId = "141414";

        dbPublishService.publishCallDisconnectEvent(callId, ServiceType.CERTIFICATE_COURSE);

        verify(courseItemMeasureService).createCourseItemMeasure(callId);
        verify(callDurationMeasureService).createCallDurationMeasure(callId);
    }

    @Test
    public void shouldPublishCallDisconnectEventForJobAidCourse() throws Exception {
        String callId = "141414";

        dbPublishService.publishCallDisconnectEvent(callId, ServiceType.JOB_AID);

        verify(jobAidContentMeasureService).createJobAidContentMeasure(callId);
        verify(callDurationMeasureService).createCallDurationMeasure(callId);
    }

    @Test
    public void shouldPublishNewRegistration() throws Exception {
        String callerId = "1234";

        dbPublishService.publishNewRegistration(callerId);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(registrationMeasureService).createRegistrationMeasure(captor.capture());
        LogData logData = captor.getValue();
        assertEquals(LogType.REGISTRATION, logData.getType());
        assertEquals(callerId, logData.getDataId());
    }
}
