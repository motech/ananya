package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerDimensionServiceTest {

    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;

    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerDimensionService = new FrontLineWorkerDimensionService(allFrontLineWorkerDimensions);
    }

    @Test
    public void shouldReturnAllUnregisteredFrontLineWorkerDimensions() {
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        when(allFrontLineWorkerDimensions.getAllUnregistered()).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerDimension> allUnregistered = frontLineWorkerDimensionService.getAllUnregistered();

        assertEquals(frontLineWorkerDimensions, allUnregistered);
    }

    @Test
    public void shouldUpdateAllTheFrontLineWorkersWithTheOperatorDetailsFromCouch() {
        Long msisdn = 1234L;
        String operator = "airtel";
        ArrayList<FrontLineWorker> allFrontLineWorkers = new ArrayList<FrontLineWorker>();
        allFrontLineWorkers.add(new FrontLineWorker(msisdn.toString(), operator));
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension();
        when(allFrontLineWorkerDimensions.fetchFor(msisdn)).thenReturn(frontLineWorkerDimension);

        frontLineWorkerDimensionService.updateFrontLineWorkers(allFrontLineWorkers);

        verify(allFrontLineWorkerDimensions).update(frontLineWorkerDimension);
        assertEquals(operator, frontLineWorkerDimension.getOperator());
    }

    @Test
    public void shouldNotUpdateReportDbIfTheOperatorIsEmptyInCouch() {
        Long msisdn = 1234L;
        ArrayList<FrontLineWorker> allFrontLineWorkers = new ArrayList<FrontLineWorker>();
        allFrontLineWorkers.add(new FrontLineWorker(msisdn.toString(), null));
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension();
        when(allFrontLineWorkerDimensions.fetchFor(msisdn)).thenReturn(frontLineWorkerDimension);

        frontLineWorkerDimensionService.updateFrontLineWorkers(allFrontLineWorkers);

        verify(allFrontLineWorkerDimensions, never()).update(frontLineWorkerDimension);
    }

    @Test
    public void shouldGetOrMakeGivenTheDetails() {
        long msisdn = 123L;
        String operator = "Airtel";
        String circle = "circle";
        String name = "name";
        String designation = Designation.ANM.name();
        String registrationStatus = RegistrationStatus.PARTIALLY_REGISTERED.name();

        frontLineWorkerDimensionService.createOrUpdate(msisdn, operator, circle, name, designation, registrationStatus);

        verify(allFrontLineWorkerDimensions).createOrUpdate(msisdn, operator, circle, name, designation, registrationStatus);
    }

    @Test
    public void shouldCheckIfFrontLineWorkerExists() {
        when(allFrontLineWorkerDimensions.fetchFor(123L)).thenReturn(new FrontLineWorkerDimension());

        boolean exists = frontLineWorkerDimensionService.exists(123L);

        assertTrue(exists);
    }

    @Test
    public void shouldReturnFalseIfFrontLineWorkerExists() {
        when(allFrontLineWorkerDimensions.fetchFor(123L)).thenReturn(null);

        boolean exists = frontLineWorkerDimensionService.exists(123L);

        assertFalse(exists);
    }

    @Test
    public void shouldGetFilteredFlws() {
        String msisdn = "123";
        String operator = "Airtel";
        String circle = "circle";
        String name = "name";
        String designation = Designation.ANM.name();
        String registrationStatus = RegistrationStatus.PARTIALLY_REGISTERED.name();
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(Long.parseLong(msisdn), name, registrationStatus, designation, operator, circle));
        when(allFrontLineWorkerDimensions.getFilteredFLWFor(null, name, registrationStatus, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerDimension> filteredFLWs = frontLineWorkerDimensionService.getFilteredFLW(null, name, registrationStatus, designation, operator, circle);

        assertEquals(filteredFLWs.size(), 1);
        assertTrue(filteredFLWs.contains(frontLineWorkerDimensions.get(0)));
    }
}
