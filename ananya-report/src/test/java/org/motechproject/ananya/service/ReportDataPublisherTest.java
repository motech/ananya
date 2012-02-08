package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.ReportData;
import org.motechproject.context.EventContext;

import java.util.HashMap;
import java.util.Map;

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
        String table = "table";
        Map<String, Object> record = new HashMap<String, Object>();
        DateTime time = DateTime.now();
        ReportData reportData = new ReportData(table, record, time);

        reportDataPublisher.publish(reportData);

        ArgumentCaptor<ReportData> captor = ArgumentCaptor.forClass(ReportData.class);
        verify(eventContext).send(eq(ReportDataPublisher.SEND_REPORT_DATA_KEY), captor.capture());
        ReportData captured = captor.getValue();
        assertEquals(captured, reportData);

    }

}
