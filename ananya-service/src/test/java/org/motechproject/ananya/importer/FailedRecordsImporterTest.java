package org.motechproject.ananya.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.service.FailedRecordsService;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FailedRecordsImporterTest {
    @Mock
    private FailedRecordsService failedRecordsService;
    @Captor
    private ArgumentCaptor<ArrayList<FailedRecordCSVRequest>> failedrecordRequestsCaptor;

    private FailedRecordsImporter failedRecordsImporter;

    @Before
    public void setUp() {
        initMocks(this);
        failedRecordsImporter = new FailedRecordsImporter(failedRecordsService);
    }

    @Test
    public void shouldProcess() {
        ArrayList<FailedRecordCSVRequest> failedRecordCSVRequests = new ArrayList<>();
        String msisdn = "1234567890";
        FailedRecordCSVRequest failedRecordCSVRequest = new FailedRecordCSVRequest();
        failedRecordCSVRequest.setMsisdn(msisdn);
        failedRecordCSVRequests.add(failedRecordCSVRequest);

        failedRecordsImporter.postData(failedRecordCSVRequests);

        verify(failedRecordsService).publishFailedRecordsForProcessing(failedrecordRequestsCaptor.capture());
        ArrayList<FailedRecordCSVRequest> actualFailedRecordRequests = failedrecordRequestsCaptor.getValue();
        assertEquals(1, actualFailedRecordRequests.size());
        assertEquals(msisdn, actualFailedRecordRequests.get(0).getMsisdn());
    }
}
