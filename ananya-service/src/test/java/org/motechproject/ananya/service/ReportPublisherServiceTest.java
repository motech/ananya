package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.LogData;
import org.motechproject.ananya.domain.LogType;
import org.motechproject.context.EventContext;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportPublisherServiceTest {

    private ReportPublisherService reportPublisherService;
    @Mock
    private EventContext eventContext;

    @Before
    public void setUp() {
        initMocks(this);
        reportPublisherService = new ReportPublisherService(eventContext);
    }

    @Test
    public void shouldPublishReportDataToEventContext() {
        LogData reportData = new LogData(LogType.CERTIFICATE_COURSE, "123");

        reportPublisherService.publishRegistration(reportData);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(eventContext).send(eq(ReportPublisherService.SEND_REGISTRATION_DATA_KEY), captor.capture());
        LogData captured = captor.getValue();
        assertEquals(captured, reportData);
    }

    @Test
    public void shouldPublishReportUpdationDataToEventContext() {
        LogData reportData = new LogData(LogType.REGISTRATION_SAVE_NAME, "123");

        reportPublisherService.publishRegistrationUpdate(reportData);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(eventContext).send(eq(ReportPublisherService.SEND_REGISTRATION_COMPLETION_DATA_KEY), captor.capture());
        LogData captured = captor.getValue();
        assertEquals(captured, reportData);
    }

    @Test
    public void shouldPublishCallDurationData(){
        LogData reportData = new LogData(LogType.CALL_DURATION, "callId");

        reportPublisherService.publishCallDuration(reportData);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(eventContext).send(eq(ReportPublisherService.SEND_CALL_DURATION_DATA_KEY), captor.capture());
        LogData captured = captor.getValue();
        assertEquals(captured, reportData);
    }

    @Test
    public void shouldPublishCertificateCourseData(){
        LogData reportData = new LogData(LogType.CERTIFICATE_COURSE, "");

        reportPublisherService.publishCertificateCourseData(reportData);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(eventContext).send(eq(ReportPublisherService.SEND_CERTIFICATE_COURSE_DATA_KEY), captor.capture());
        LogData captured = captor.getValue();
        assertEquals(captured, reportData);
    }

    @Test
    public void shouldPublishSMSSent() {
        LogData reportData = new LogData(LogType.SMS_SENT, "callerId");

        reportPublisherService.publishSMSSent(reportData);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(eventContext).send(eq(ReportPublisherService.SEND_SMS_SENT_DATA_KEY), captor.capture());
        LogData captured = captor.getValue();
        assertEquals(captured, reportData);
    }
}
