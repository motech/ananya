package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.FrontLineWorkerStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerServiceTest {

    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
   
    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers);
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
    public void shouldExtractCallerIdAndSaveRecordedFilesAndSaveNewWorker() {
        frontLineWorkerService.createNew("msisdn");

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker captured = captor.getValue();
        assertEquals("msisdn",captured.msisdn());
        assertTrue(captured.status().equals(FrontLineWorkerStatus.PENDING_REGISTRATION));
    }

}
