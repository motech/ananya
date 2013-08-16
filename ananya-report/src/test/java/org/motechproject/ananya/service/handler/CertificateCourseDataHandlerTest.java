package org.motechproject.ananya.service.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.requests.CallMessageType;
import org.motechproject.ananya.service.SMSLogService;
import org.motechproject.ananya.service.SendSMSService;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.CourseAudioTrackerMeasureService;
import org.motechproject.ananya.service.measure.CourseContentMeasureService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CertificateCourseDataHandlerTest {

    private CertificateCourseDataHandler handler;
    @Mock
    private RegistrationMeasureService registrationMeasureService;
    @Mock
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private SMSLogService smsLogService;
    @Mock
    private SendSMSService sendSMSService;
    @Mock
    private CourseContentMeasureService courseContentMeasureService;
    @Mock
    private CourseAudioTrackerMeasureService courseAudioTrackerMeasureService;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new CertificateCourseDataHandler(
                callDurationMeasureService, registrationMeasureService,
                courseContentMeasureService, courseAudioTrackerMeasureService,
                smsLogService, sendSMSService);
    }

    @Test
    public void shouldHandleCertificateCourseData() {
        String callId = "callId";
        String callerId = "callerId";
        CallMessage logData = new CallMessage(CallMessageType.CERTIFICATE_COURSE_DATA, callId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        map.put("2", new Integer(3)); //some other parameter
        MotechEvent event = new MotechEvent("", map);

        SMSLog smsLog = new SMSLog(callId, callerId, "location", 1, "language");
        when(smsLogService.getSMSLogFor(callId)).thenReturn(smsLog);

        handler.handleCertificateCourseData(event);

        verify(registrationMeasureService).createFor(callId);
        verify(callDurationMeasureService).createFor(callId);
        verify(courseAudioTrackerMeasureService).createFor(callId);
        verify(sendSMSService).buildAndSendSMS(callerId, "location", 1, "language");
        verify(smsLogService, never()).deleteFor(smsLog);
    }
}