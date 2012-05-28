package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.response.RegistrationResponse;

import java.util.ArrayList;
import java.util.Arrays;

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
        registrationService = new RegistrationService(frontLineWorkerService, registrationMeasureService);
    }

    @Test
    public void shouldSaveNewFLWAndPublishForReport() {
        String callerId = "919986574410";
        String name = "name";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        Designation designation = Designation.AWW;
        LocationList locationList = new LocationList(Arrays.asList(location));

        when(frontLineWorkerService.createOrUpdate(callerId, name, designation, location, RegistrationStatus.REGISTERED)).thenReturn(new FrontLineWorker(callerId, "operator"));

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, designation.name(), "district", "block", "village", locationList);

        assertEquals("New FrontlineWorker added", registrationResponse.getMessage());
        verify(frontLineWorkerService).createOrUpdate(callerId, name, designation, location, RegistrationStatus.REGISTERED);
        verify(registrationMeasureService).createRegistrationMeasure(callerId);
    }

    @Test
    public void shouldNotSaveFLWForInvalidLocation() {
        String callerId = "919986574410";
        String name = "name";
        Designation designation = Designation.AWW;
        LocationList locationList = new LocationList(new ArrayList<Location>());

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, designation.name(), "district", "block", "village", locationList);

        assertEquals("Invalid Location", registrationResponse.getMessage());
        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), eq(RegistrationStatus.REGISTERED));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId);
    }

    @Test
    public void shouldNotSaveFLWForInvalidCallerId() {
        String callerId = "";
        String name = "name";
        Designation designation = Designation.AWW;
        LocationList locationList = new LocationList(new ArrayList<Location>());

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, designation.name(), "district", "block", "village", locationList);

        assertEquals("Invalid CallerId", registrationResponse.getMessage());
        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), any(RegistrationStatus.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId);

        callerId = "abcdef";
        registrationResponse = registrationService.registerFlw(callerId, name, designation.name(), "district", "block", "village", locationList);

        assertEquals("Invalid CallerId", registrationResponse.getMessage());

        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), any(RegistrationStatus.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId);
    }

    @Test
    public void shouldSaveFLWWithInvalidNameAsPartiallyRegistered() {
        String callerId = "919986574410";
        String name = "";
        Designation designation = Designation.AWW;
        Location location = new Location("district", "block", "village", 1, 1, 1);
        LocationList locationList = new LocationList(Arrays.asList(location));
        registrationService = new RegistrationService(frontLineWorkerService, registrationMeasureService);

        when(frontLineWorkerService.createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), eq(RegistrationStatus.PARTIALLY_REGISTERED))).thenReturn(new FrontLineWorker(callerId,"operator"));

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, designation.name(), "district", "block", "village", locationList);

        assertEquals("New FrontlineWorker added", registrationResponse.getMessage());
        verify(frontLineWorkerService).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), eq(RegistrationStatus.PARTIALLY_REGISTERED));
    }

    @Test
    public void shouldSaveFLWWithInvalidDesignationAsPartiallyRegistered() {
        String callerId = "919986574410";
        String name = "name";
        String designation = "invalid_designation";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        LocationList locationList = new LocationList(Arrays.asList(location));
        registrationService = new RegistrationService(frontLineWorkerService, registrationMeasureService);

        when(frontLineWorkerService.createOrUpdate(eq(callerId), eq(name), eq(Designation.INVALID), any(Location.class), eq(RegistrationStatus.PARTIALLY_REGISTERED))).thenReturn(new FrontLineWorker(callerId,"operator"));

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, designation, "district", "block", "village", locationList);

        assertEquals("New FrontlineWorker added", registrationResponse.getMessage());
        verify(frontLineWorkerService).createOrUpdate(eq(callerId), eq(name), eq(Designation.INVALID), any(Location.class), eq(RegistrationStatus.PARTIALLY_REGISTERED));
    }
}
