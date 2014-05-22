package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.VerificationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.requests.FLWStatusChangeRequest;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerDimensionServiceTest {

    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;

    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Captor
    private ArgumentCaptor<List<FrontLineWorkerDimension>> frontLineWorkerDimensionsCaptor;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerDimensionService = new FrontLineWorkerDimensionService(allFrontLineWorkerDimensions);
    }

    @Test
    public void shouldReturnAllUnregisteredFrontLineWorkerDimensions() {
        List<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<>();
        when(allFrontLineWorkerDimensions.getAllUnregistered()).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerDimension> allUnregistered = frontLineWorkerDimensionService.getAllUnregistered();
        assertEquals(frontLineWorkerDimensions, allUnregistered);
    }

    @Test
    public void shouldGetOrMakeGivenTheDetails() {
        long msisdn = 123L;
        String operator = "Airtel";
        String circle = "circle";
        String name = "name";
        String designation = Designation.ANM.name();
        String registrationStatus = RegistrationStatus.PARTIALLY_REGISTERED.name();
        UUID flwId = UUID.randomUUID();

        frontLineWorkerDimensionService.createOrUpdate(msisdn, msisdn, operator, circle, name, designation, registrationStatus, flwId, VerificationStatus.OTHER);

        verify(allFrontLineWorkerDimensions).createOrUpdate(msisdn, msisdn, operator, circle, name, designation, registrationStatus, flwId, VerificationStatus.OTHER);
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
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(Long.parseLong(msisdn), null, name, registrationStatus, designation, operator, circle, UUID.randomUUID(), null));
        when(allFrontLineWorkerDimensions.getFilteredFLWFor(null, Long.parseLong(msisdn), name, registrationStatus, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerDimension> filteredFLWs = frontLineWorkerDimensionService.getFilteredFLW(null, Long.parseLong(msisdn), name, registrationStatus, designation, operator, circle);

        assertEquals(filteredFLWs.size(), 1);
        assertTrue(filteredFLWs.contains(frontLineWorkerDimensions.get(0)));
    }

    @Test
    public void shouldUpdateRegistrationStatus() {
        ArrayList<FLWStatusChangeRequest> flwStatusChangeRequests = new ArrayList<>();
        long msisdn = 1234567890L;
        String status = RegistrationStatus.PARTIALLY_REGISTERED.name();
        flwStatusChangeRequests.add(new FLWStatusChangeRequest(msisdn, status));
        FrontLineWorkerDimension expectedFLW = new FrontLineWorkerDimension();
        when(allFrontLineWorkerDimensions.fetchFor(msisdn)).thenReturn(expectedFLW);

        frontLineWorkerDimensionService.updateStatus(flwStatusChangeRequests);

        verify(allFrontLineWorkerDimensions).createOrUpdateAll(frontLineWorkerDimensionsCaptor.capture());
        List<FrontLineWorkerDimension> frontLineWorkerDimensions = frontLineWorkerDimensionsCaptor.getValue();
        assertEquals(1, frontLineWorkerDimensions.size());
        assertEquals(expectedFLW, frontLineWorkerDimensions.get(0));
        assertEquals(status, frontLineWorkerDimensions.get(0).getStatus());
    }

    @Test
    public void shouldUpdateFrontLineWorkerDimension(){
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension();
        frontLineWorkerDimensionService.update(frontLineWorkerDimension);
        verify(allFrontLineWorkerDimensions).update(frontLineWorkerDimension);
    }
}
