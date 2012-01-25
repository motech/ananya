package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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
        assertEquals("msisdn",captured.getMsisdn());
        assertTrue(captured.status().equals(FrontLineWorkerStatus.PENDING_REGISTRATION));
    }

    @Test
    public void shouldGetFrontLineWorkerWithGivenCallerId() {
        String msisdn = "123";
        FrontLineWorker expectedFrontLineWorker = new FrontLineWorker(msisdn);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(expectedFrontLineWorker);

        FrontLineWorker frontLineWorker = frontLineWorkerService.getFrontLineWorker(msisdn);

        assertEquals(expectedFrontLineWorker, frontLineWorker);
    }

    @Test
    public void shouldAddScoreToAFrontLineWorker() {
        String msisdn = "123";
        FrontLineWorker expectedFrontLineWorker = new FrontLineWorker(msisdn);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(expectedFrontLineWorker);

        ReportCard.Score score = new ReportCard.Score("1", "2", true);
        
        frontLineWorkerService.addScore(msisdn, score);
        
        assertThat(expectedFrontLineWorker.reportCard().scores(), hasItems(score));
        verify(allFrontLineWorkers).update(expectedFrontLineWorker);
    }
    
    @Test
    public void shouldTellThatUserIsRegisteredBasedOnStatus() {
        String registeredMsisdn = "123";
        FrontLineWorker registeredFrontLineWorker = new FrontLineWorker(registeredMsisdn);
        registeredFrontLineWorker.status(FrontLineWorkerStatus.REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(registeredMsisdn)).thenReturn(registeredFrontLineWorker);
        
        assertThat(frontLineWorkerService.isCallerRegistered(registeredMsisdn), is(true));
    }

    @Test
    public void shouldTellThatUserIsUnRegisteredBasedOnStatus() {
        String registeredMsisdn = "123";
        FrontLineWorker registeredFrontLineWorker = new FrontLineWorker(registeredMsisdn);
        registeredFrontLineWorker.status(FrontLineWorkerStatus.UNREGISTERED);
        when(allFrontLineWorkers.findByMsisdn(registeredMsisdn)).thenReturn(registeredFrontLineWorker);

        assertThat(frontLineWorkerService.isCallerRegistered(registeredMsisdn), is(false));
    }

    @Test
    public void shouldReturnEmptyBookmarkIfFrontLineWorkerDoesNotExist() {
        String msisdn = "999";
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(null);
        
        assertThat(frontLineWorkerService.getBookmark(msisdn) , is(EmptyBookmark.class));
    }

    @Test
    public void shouldReturnBookmarkOfFrontLineWorker() {
        String msisdn = "999";
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        BookMark bookMark = new BookMark("leson", "0", "2");
        frontLineWorker.addBookMark(bookMark);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        assertThat(frontLineWorkerService.getBookmark(msisdn) , is(bookMark));
    }
}
