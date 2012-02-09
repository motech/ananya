package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.log.LogType;
import org.motechproject.ananya.domain.log.LogData;
import org.motechproject.context.EventContext;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportDataPublisherTest {

    private ReportDataPublisher reportDataPublisher;
    @Mock
    private EventContext eventContext;

    @Before
    public void setUp() {
        initMocks(this);
        reportDataPublisher = new ReportDataPublisher(eventContext);
    }

    @Test
    public void shouldPublishReportDataToEventContext() {
        LogData reportData = new LogData(LogType.CERTIFICATE_COURSE, "123");

        reportDataPublisher.publish(reportData);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(eventContext).send(eq(ReportDataPublisher.SEND_REGISTRATION_DATA_KEY), captor.capture());
        LogData captured = captor.getValue();
        assertEquals(captured, reportData);

    }

}
