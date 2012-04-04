package org.motechproject.ananya.service.publish;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.CallDurationMeasureService;
import org.motechproject.ananya.service.CourseItemMeasureService;
import org.motechproject.ananya.service.RegistrationMeasureService;
import org.motechproject.ananya.service.SMSSentMeasureService;

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

    private DbPublishService dbPublishService;

    @Before
    public void setUp() {
        initMocks(this);
        dbPublishService = new DbPublishService(registrationMeasureService, courseItemMeasureService, callDurationMeasureService, smsSentMeasureService);
    }

    @Test
    public void shouldPublishSMSSent() throws Exception {
        String callerId = "1234";
        LogData logData = new LogData(LogType.SMS_SENT, callerId);

        dbPublishService.publishSMSSent(logData);

        verify(smsSentMeasureService).createSMSSentMeasure(callerId);
    }

    @Test
    public void shouldPublishCallDisconnectEvent() throws Exception {
        String callId = "141414";

        dbPublishService.publishCallDisconnectEvent(callId);

        verify(courseItemMeasureService).createCourseItemMeasure(callId);
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
