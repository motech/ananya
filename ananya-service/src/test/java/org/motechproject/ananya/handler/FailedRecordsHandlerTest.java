package org.motechproject.ananya.handler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.service.FailedRecordsService;
import org.motechproject.event.MotechEvent;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FailedRecordsHandlerTest {

    private FailedRecordsHandler failedRecordsHandler;

    @Mock
    private FailedRecordsService failedRecordsService;

    @Captor
    private ArgumentCaptor<List<FailedRecordCSVRequest>> failedRecordsArgumentCaptor;

    @Before
    public void setUp() {
        initMocks(this);
        failedRecordsHandler = new FailedRecordsHandler(failedRecordsService);
    }

    @Test
    public void shouldHandleFailedRecords() {
        List<FailedRecordCSVRequest> expectedFailedRecords = new ArrayList<>();
        FailedRecordCSVRequest failedRecordCSVRequest = new FailedRecordCSVRequest();
        String msisdn = "1234567890";
        failedRecordCSVRequest.setMsisdn(msisdn);
        expectedFailedRecords.add(failedRecordCSVRequest);
        MotechEvent event = new MotechEvent(FailedRecordsService.FAILED_RECORDS_PUBLISH_MESSAGE);
        event.getParameters().put("0", expectedFailedRecords);
        event.getParameters().put("1", new Integer(2)); //some other parameters
        event.getParameters().put("2", new ArrayList<>());
        event.getParameters().put("3", new ArrayList<Integer>() {{
            add(new Integer(1));
        }});

        failedRecordsHandler.handleFailedRecords(event);

        verify(failedRecordsService).processFailedCSVRequests(failedRecordsArgumentCaptor.capture());
        List<FailedRecordCSVRequest> actualFailedRecords = failedRecordsArgumentCaptor.getValue();
        assertEquals(1, actualFailedRecords.size());
        assertEquals(msisdn, actualFailedRecords.get(0).getMsisdn());
    }
}