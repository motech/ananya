package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallDetail;
import org.motechproject.ananya.domain.ReportData;
import org.motechproject.ananya.repository.AllCallDetails;
import org.motechproject.model.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportDataHandlerTest {

    @Mock
    private AllCallDetails allCallDetails;
    private ReportDataHandler handler;

    @Before
    public void setUp(){
        initMocks(this);
        handler = new ReportDataHandler(allCallDetails);
    }

    @Test
    public void shouldRetrieveReportDataAndConvertToCallDetailAndPersist() throws Exception {
        DateTime startTime = DateTime.now();
        DateTime endTime = DateTime.now();
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        Map<String, Object> callDetail = new HashMap<String, Object>();
        callDetail.put("callId","111");
        callDetail.put("callerId","222");
        callDetail.put("calledNumber","333");
        callDetail.put("startTime", startTime);
        callDetail.put("endTime", endTime);
        ReportData reportData = new ReportData("table",callDetail, DateTime.now());
        parameters.put("1", reportData);

        handler.execute(new MotechEvent(ReportDataPublisher.SEND_REPORT_DATA_KEY, parameters));

        ArgumentCaptor<CallDetail> captor = ArgumentCaptor.forClass(CallDetail.class);
        verify(allCallDetails).add(captor.capture());
        CallDetail savedCallDetail = captor.getValue();

        assertEquals("333",savedCallDetail.getCalledNumber());
        assertEquals("111",savedCallDetail.getCallId());
        assertEquals("111",savedCallDetail.getCallId());
        assertEquals(startTime,savedCallDetail.getStartTime());
        assertEquals(endTime,savedCallDetail.getEndTime());
    }
}
