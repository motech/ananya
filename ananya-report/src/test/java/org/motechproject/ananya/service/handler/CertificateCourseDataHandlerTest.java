package org.motechproject.ananya.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.SendSMSLog;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.service.*;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CertificateCourseDataHandlerTest {

    private CertificateCourseDataHandler handler;
    @Mock
    private CourseItemMeasureService courseItemMeasureService;
    @Mock
    private RegistrationMeasureService registrationMeasureService;
    @Mock
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private SMSSentMeasureService smsSentMeasureService;
    @Mock
    private RegistrationLogService registrationLogService;
    @Mock
    private SendSMSLogService sendSMSLogService;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new CertificateCourseDataHandler(courseItemMeasureService,
                callDurationMeasureService,
                registrationMeasureService, smsSentMeasureService, registrationLogService, sendSMSLogService);
    }

    @Test
    public void shouldHandleCertificateCourseData() {
        String callId = "callId";
        String callerId = "callerId";
        LogData logData = new LogData(LogType.CERTIFICATE_COURSE_DATA, callId, callerId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        when(registrationLogService.registrationLogFor(callerId)).thenReturn(new RegistrationLog(callerId, ""));
        when(sendSMSLogService.sendSMSLogFor(callerId)).thenReturn(new SendSMSLog(callerId));

        handler.handleCertificateCourseData(event);

        verify(registrationMeasureService).createRegistrationMeasure(callerId);
        verify(courseItemMeasureService).createCourseItemMeasure(callId);
        verify(callDurationMeasureService).createCallDurationMeasure(callId);
        verify(smsSentMeasureService).createSMSSentMeasure(callerId);
    }
}