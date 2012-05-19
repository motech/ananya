package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.RegistrationResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegistrationServiceTest {

    private RegistrationService registrationService;

    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private RegistrationMeasureService registrationMeasureService;
    @Mock
    private LocationService locationService;

    @Before
    public void setUp() {
        initMocks(this);
        registrationService = new RegistrationService(frontLineWorkerService, registrationMeasureService, locationService);
    }

    @Test
    public void shouldSaveNewFLWAndPublishForReport() {
        String callerId = "123";
        String name = "name";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        Designation designation = Designation.ANGANWADI;
        when(locationService.getAll()).thenReturn(Arrays.asList(location));
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), null, new LocationRequest("district", "block", "village"));

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertEquals("New FrontlineWorker added", registrationResponse.getMessage());
        verify(frontLineWorkerService).createOrUpdate(callerId, name, designation, location, RegistrationStatus.REGISTERED);
        verify(registrationMeasureService).createRegistrationMeasure(callerId);
    }

    @Test
    public void shouldNotSaveFLWForInvalidLocation() {
        String callerId = "123";
        String name = "name";
        Designation designation = Designation.ANGANWADI;
        when(locationService.getAll()).thenReturn(new ArrayList<Location>());
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), null, new LocationRequest("district", "block", "village"));

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertEquals("Invalid Location", registrationResponse.getMessage());
        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), eq(RegistrationStatus.REGISTERED));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId);
    }

    @Test
    public void shouldNotSaveFLWForInvalidCallerId() {
        String callerId = "";
        String name = "name";
        Designation designation = Designation.ANGANWADI;
        when(locationService.getAll()).thenReturn(new ArrayList<Location>());
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), null, new LocationRequest("district", "block", "village"));

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertEquals("Invalid CallerId", registrationResponse.getMessage());
        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), any(RegistrationStatus.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId);

        callerId = "abcdef";
        frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), null, new LocationRequest("district", "block", "village"));
        registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertEquals("Invalid CallerId", registrationResponse.getMessage());
        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), any(RegistrationStatus.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId);
    }

    @Test
    public void shouldSaveFLWWithInvalidNameAsPartiallyRegistered() {
        String callerId = "123";
        String name = "";
        Designation designation = Designation.ANGANWADI;
        Location location = new Location("district", "block", "village", 1, 1, 1);
        registrationService = new RegistrationService(frontLineWorkerService, registrationMeasureService, locationService);
        when(locationService.getAll()).thenReturn(Arrays.asList(location));
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), null, new LocationRequest("district", "block", "village"));

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertEquals("New FrontlineWorker added", registrationResponse.getMessage());
        verify(frontLineWorkerService).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), eq(RegistrationStatus.PARTIALLY_REGISTERED));
    }

    @Test
    public void shouldSaveFLWWithInvalidDesignationAsPartiallyRegistered() {
        String callerId = "123";
        String name = "name";
        String designation = "invalid_designation";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        registrationService = new RegistrationService(frontLineWorkerService, registrationMeasureService, locationService);
        when(locationService.getAll()).thenReturn(Arrays.asList(location));
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation, null, new LocationRequest("district", "block", "village"));

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertEquals("New FrontlineWorker added", registrationResponse.getMessage());
        verify(frontLineWorkerService).createOrUpdate(eq(callerId), eq(name), eq(Designation.INVALID), any(Location.class), eq(RegistrationStatus.PARTIALLY_REGISTERED));
    }

    @Test
    public void shouldRegisterMultipleFLWs() {
        String callerId = "123";
        String callerId1 = "1234";
        String name = "name";
        String name1 = "name1";
        String designation = Designation.ANGANWADI.name();
        Location location = new Location("district", "block", "village", 1, 1, 1);
        when(locationService.getAll()).thenReturn(Arrays.asList(location));
        List<FrontLineWorkerRequest> frontLineWorkerRequestList = new ArrayList<FrontLineWorkerRequest>();
        frontLineWorkerRequestList.add(new FrontLineWorkerRequest(callerId, name, designation, null, new LocationRequest("district", "block", "village")));
        frontLineWorkerRequestList.add(new FrontLineWorkerRequest(callerId1, name1, designation, null, new LocationRequest("district", "block", "village")));

        List<RegistrationResponse> registrationResponses = registrationService.registerAllFLWs(frontLineWorkerRequestList);

        assertEquals("New FrontlineWorker added", registrationResponses.get(0).getMessage());
        verify(frontLineWorkerService).createOrUpdate(callerId, name, Designation.valueOf(designation), location, RegistrationStatus.REGISTERED);
        verify(registrationMeasureService).createRegistrationMeasure(callerId);
        assertEquals("New FrontlineWorker added", registrationResponses.get(1).getMessage());
        verify(frontLineWorkerService).createOrUpdate(callerId1, name1, Designation.valueOf(designation), location, RegistrationStatus.REGISTERED);
        verify(registrationMeasureService).createRegistrationMeasure(callerId1);

    }
}
