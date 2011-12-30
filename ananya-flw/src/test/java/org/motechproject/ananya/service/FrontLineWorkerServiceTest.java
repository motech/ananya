package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.FLWStatus;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.repository.AllFrontLineWorkers;

import static junit.framework.Assert.assertEquals;
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
    public void shouldSaveNewFrontLineWorker() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("91998654410");
        frontLineWorkerService.add(frontLineWorker);
        verify(allFrontLineWorkers).add(frontLineWorker);
    }


    @Test
    public void shouldVerifyIfAUserIsRegisteredForAGivenMSISDN() {
        String msisdn = "91998654410";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn);
        frontLineWorker.status(FLWStatus.REGISTERED).name("deepali");

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        FLWStatus status = frontLineWorkerService.getStatus(msisdn);
        assertEquals(FLWStatus.REGISTERED, status);
    }
}
