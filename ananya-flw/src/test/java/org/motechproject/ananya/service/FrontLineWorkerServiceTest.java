package org.motechproject.ananya.service;

import org.apache.commons.fileupload.FileItem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllRecordedContent;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerServiceTest {

    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private AllRecordedContent allRecordedContent;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers, allRecordedContent);
    }

    @Test
    public void shouldVerifyIfAUserIsRegisteredForAGivenMSISDN() {
        String msisdn = "91998654410";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn);
        frontLineWorker.status(FrontLineWorkerStatus.REGISTERED).name("cher");

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        FrontLineWorkerStatus status = frontLineWorkerService.getStatus(msisdn);
        assertEquals(FrontLineWorkerStatus.REGISTERED, status);
    }

    @Test
    public void shouldVerifyIfAUserIsUnRegisteredForAGivenMSISDN() {
        String msisdn = "91998654410";
        FrontLineWorkerStatus status = frontLineWorkerService.getStatus(msisdn);
        assertEquals(FrontLineWorkerStatus.UNREGISTERED, status);
    }

    @Test
    public void shouldExtractCallerIdAndSaveRecordedFiles() {
        FileItem item = mock(FileItem.class);
        when(item.getFieldName()).thenReturn("msisdn");
        when(item.isFormField()).thenReturn(true);
        when(item.getString()).thenReturn("msisdn");
        List<FileItem> items = Arrays.asList(item);

        String msisdn = frontLineWorkerService.createNew(items);
        assertEquals("msisdn", msisdn);

        verify(allRecordedContent).add("msisdn", items);
    }

}
