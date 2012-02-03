package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerServiceTest {

    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private AllLocations allLocations;
   
    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers,allLocations);
    }

    @Test
    public void shouldVerifyIfAUserIsRegisteredForAGivenMSISDN() {
        String msisdn = "91998654410";
        FrontLineWorker frontLineWorker = FrontLineWorker();
        frontLineWorker.status(RegistrationStatus.REGISTERED).name("cher");

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        RegistrationStatus status = frontLineWorkerService.getStatus(msisdn);
        assertEquals(RegistrationStatus.REGISTERED, status);
    }

    @Test
    public void shouldVerifyIfAUserIsUnRegisteredForAGivenMSISDN() {
        String msisdn = "91998654410";
        RegistrationStatus status = frontLineWorkerService.getStatus(msisdn);
        assertEquals(RegistrationStatus.UNREGISTERED, status);
    }

    @Test
    public void shouldExtractCallerIdAndSaveRecordedFilesAndSaveNewWorker() {
        String panchayatCode = "S01D001V0001";
        Location patna = new Location(panchayatCode, "Patna", "Dulhin Bazar", "Singhara Kopa");
        patna.setId("id");
        when(allLocations.findByExternalId(panchayatCode)).thenReturn(patna);

        frontLineWorkerService.createNew("msisdn", Designation.ASHA, panchayatCode);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker captured = captor.getValue();

        assertEquals("msisdn",captured.getMsisdn());
        assertEquals("id",captured.getLocationId());
        assertTrue(captured.status().equals(RegistrationStatus.PENDING_REGISTRATION));
    }

    @Test
    public void shouldGetFrontLineWorkerWithGivenCallerId() {
        String msisdn = "123";
        FrontLineWorker expectedFrontLineWorker = FrontLineWorker();
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(expectedFrontLineWorker);

        FrontLineWorker frontLineWorker = frontLineWorkerService.getFrontLineWorker(msisdn);

        assertEquals(expectedFrontLineWorker, frontLineWorker);
    }

    @Test
    public void shouldAddScoreToAFrontLineWorker() {
        String msisdn = "123";
        FrontLineWorker expectedFrontLineWorker = FrontLineWorker();
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(expectedFrontLineWorker);

        ReportCard.Score score = new ReportCard.Score("1", "2", true);
        
        frontLineWorkerService.addScore(msisdn, score);
        
        assertThat(expectedFrontLineWorker.reportCard().scores(), hasItems(score));
        verify(allFrontLineWorkers).update(expectedFrontLineWorker);
    }

    private FrontLineWorker FrontLineWorker() {
        return new FrontLineWorker("123", Designation.ANM, "123");
    }

    @Test
    public void shouldTellThatUserIsRegisteredBasedOnStatus() {
        String registeredMsisdn = "123";
        FrontLineWorker registeredFrontLineWorker = FrontLineWorker();
        registeredFrontLineWorker.status(RegistrationStatus.REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(registeredMsisdn)).thenReturn(registeredFrontLineWorker);
        
        assertThat(frontLineWorkerService.isCallerRegistered(registeredMsisdn), is(true));
    }

    @Test
    public void shouldTellThatUserIsUnRegisteredBasedOnStatus() {
        String registeredMsisdn = "123";
        FrontLineWorker registeredFrontLineWorker = FrontLineWorker();
        registeredFrontLineWorker.status(RegistrationStatus.UNREGISTERED);
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
        FrontLineWorker frontLineWorker = FrontLineWorker();
        BookMark bookMark = new BookMark("leson", "0", "2");
        frontLineWorker.addBookMark(bookMark);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        assertThat(frontLineWorkerService.getBookmark(msisdn) , is(bookMark));
    }
}
