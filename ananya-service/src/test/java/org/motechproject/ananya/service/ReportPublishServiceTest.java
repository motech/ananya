package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.publish.QueuePublishService;
import org.motechproject.context.EventContext;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportPublishServiceTest {

    private QueuePublishService reportPublishService;
    @Mock
    private EventContext eventContext;

    @Before
    public void setUp() {
        initMocks(this);
        reportPublishService = new QueuePublishService(eventContext);
    }

    @Test
    public void shouldPublishCertificateCourseDisconnectEvent(){
        String callId = "callID";
        reportPublishService.publishCallDisconnectEvent(callId, ServiceType.CERTIFICATE_COURSE);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(eventContext).send(eq(ReportPublishEventKeys.SEND_CERTIFICATE_COURSE_DATA_KEY), captor.capture());
        verify(eventContext).send(eq(ReportPublishEventKeys.SEND_CALL_DURATION_DATA_KEY), captor.capture());
        List<LogData> captured = captor.getAllValues();
        
        assertEquals(2, captured.size());
        for(LogData logData: captured){
            assertEquals(callId, logData.getDataId());
        }
    }

    @Test
    public void shouldPublishJobAidDisconnectEvent(){
        String callId = "callID";
        reportPublishService.publishCallDisconnectEvent(callId, ServiceType.JOB_AID);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(eventContext).send(eq(ReportPublishEventKeys.SEND_JOB_AID_CONTENT_DATA_KEY), captor.capture());
        verify(eventContext).send(eq(ReportPublishEventKeys.SEND_CALL_DURATION_DATA_KEY), captor.capture());
        List<LogData> captured = captor.getAllValues();

        assertEquals(2, captured.size());
        for(LogData logData: captured){
            assertEquals(callId, logData.getDataId());
        }
    }

    @Test
    public void shouldPublishSMSSent() {
        LogData reportData = new LogData(LogType.SMS_SENT, "callerId");

        reportPublishService.publishSMSSent(reportData);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(eventContext).send(eq(ReportPublishEventKeys.SEND_SMS_SENT_DATA_KEY), captor.capture());
        LogData captured = captor.getValue();
        assertEquals(captured, reportData);
    }

    @Test
    public void shouldPublishRegistration() {
        String callerId = "123";

        reportPublishService.publishNewRegistration(callerId);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(eventContext).send(eq(ReportPublishEventKeys.SEND_REGISTRATION_DATA_KEY), captor.capture());
        LogData captured = captor.getValue();
        assertEquals(captured.getDataId(), callerId);
    }

}
