package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.ReportData;
import org.motechproject.ananya.domain.log.RegistrationLog;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportDataHandlerTest {

    @Mock
    private AllCallLogs allCallLogs;
    private ReportDataHandler handler;

    @Before
    public void setUp(){
        initMocks(this);
        handler = new ReportDataHandler(allCallLogs);
    }

    @Test
    public void shouldRetrieveReportDataAndConvertToCallDetailAndPersist() throws Exception {

        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now();

        Map<String, Object> record = new HashMap<String, Object>();
        record.put("callId","111");
        record.put("callerId","222");
        record.put("calledNumber","333");
        record.put("designation", "designation");
        record.put("district", "district");
        record.put("block", "block");
        record.put("panchayat", "panchayat");
        record.put("startTime", startTime);
        record.put("endTime", endTime);
        ReportData reportData = new ReportData(RegistrationLog.class.getName(),record, DateTime.now());

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("1", reportData);

        handler.execute(new MotechEvent(ReportDataPublisher.SEND_REPORT_DATA_KEY, parameters));

        ArgumentCaptor<RegistrationLog> captor = ArgumentCaptor.forClass(RegistrationLog.class);
        verify(allCallLogs).add(captor.capture());
        RegistrationLog savedRegistrationLog = captor.getValue();

        assertEquals("111",savedRegistrationLog.getCallId());
        assertEquals("222",savedRegistrationLog.getCallerId());
        assertEquals("333",savedRegistrationLog.getCalledNumber());
        assertEquals("designation",savedRegistrationLog.getDesignation());
        assertEquals("block",savedRegistrationLog.getBlock());
        assertEquals("district",savedRegistrationLog.getDistrict());
        assertEquals("panchayat",savedRegistrationLog.getPanchayat());
        assertEquals(endTime,savedRegistrationLog.getEndTime());
        assertEquals(startTime,savedRegistrationLog.getStartTime());
    }
}
