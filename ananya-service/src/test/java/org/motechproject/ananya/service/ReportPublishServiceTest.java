package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.requests.CallMessage;
import org.motechproject.ananya.requests.ReportPublishEventKeys;
import org.motechproject.ananya.service.publish.QueuePublishService;
import org.motechproject.scheduler.context.EventContext;

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
    public void shouldPublishCertificateCourseDisconnectEvent() {
        String callId = "callID";
        reportPublishService.publishDisconnectEvent(callId, ServiceType.CERTIFICATE_COURSE);

        ArgumentCaptor<CallMessage> captor = ArgumentCaptor.forClass(CallMessage.class);
        verify(eventContext).send(eq(ReportPublishEventKeys.CERTIFICATE_COURSE_CALL_MESSAGE), captor.capture());
        CallMessage logData = captor.getValue();

        assertEquals(callId, logData.getCallId());
    }

    @Test
    public void shouldPublishJobAidDisconnectEvent() {
        String callId = "callID";
        reportPublishService.publishDisconnectEvent(callId, ServiceType.JOB_AID);

        ArgumentCaptor<CallMessage> captor = ArgumentCaptor.forClass(CallMessage.class);
        verify(eventContext).send(eq(ReportPublishEventKeys.JOBAID_CALL_MESSAGE), captor.capture());
        CallMessage logData = captor.getValue();

        assertEquals(callId, logData.getCallId());
    }
}
