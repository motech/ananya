package org.motechproject.ananya.service.publish;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.service.*;

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
    public void shouldPublishCallDisconnectEventForCertificateCourse() throws Exception {
        String callId = "141414";
        String callerId = "9876543210";

        dbPublishService.publishCallDisconnectEvent(callId, callerId, ServiceType.CERTIFICATE_COURSE);

        verify(registrationMeasureService).createRegistrationMeasure(callerId);
        verify(courseItemMeasureService).createCourseItemMeasure(callId);
        verify(callDurationMeasureService).createCallDurationMeasure(callId);
    }

    @Test
    public void shouldPublishCallDisconnectEventForJobAidCourse() throws Exception {
        String callId = "141414";
        String callerId = "9876543210";

        dbPublishService.publishCallDisconnectEvent(callId, callerId, ServiceType.JOB_AID);

        verify(registrationMeasureService).createRegistrationMeasure(callerId);
        verify(jobAidContentMeasureService).createJobAidContentMeasure(callId);
        verify(callDurationMeasureService).createCallDurationMeasure(callId);
    }
}
