package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.log.LogData;
import org.motechproject.ananya.domain.log.LogType;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportDataHandlerTest {

    private ReportDataHandler handler;
    @Mock
    private TransactionToReportMapper mapper;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new ReportDataHandler(mapper);
    }

    @Test
    public void shouldFetchLogDataFromMotechEventAndPassToCallMapper() {
        LogData logData = new LogData(LogType.REGISTRATION, "1234");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("1", logData);
        MotechEvent event = new MotechEvent("", map);

        handler.handleRegistration(event);

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(mapper).transformAndPushToReportingDB(captor.capture());
        assertEquals(logData, captor.getValue());
    }


}