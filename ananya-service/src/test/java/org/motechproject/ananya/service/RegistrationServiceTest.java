package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
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
        String callerId = "123";
        String name = "name";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        Designation designation = Designation.ANGANWADI;
        LocationList locationList = new LocationList(Arrays.asList(location));
        registrationService = new RegistrationService(frontLineWorkerService, registrationMeasureService);

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, designation.name(), "district", "block", "village", locationList);

        assertEquals("New FrontlineWorker added", registrationResponse.getMessage());
        verify(frontLineWorkerService).createOrUpdate(callerId, name, designation, location, RegistrationStatus.REGISTERED);
        ArgumentCaptor<LogData> logDataArgumentCaptor = ArgumentCaptor.forClass(LogData.class);
        verify(registrationMeasureService).createRegistrationMeasure(logDataArgumentCaptor.capture());
        LogData logData = logDataArgumentCaptor.getValue();
        assertEquals(callerId, logData.getDataId());
        assertEquals(LogType.REGISTRATION, logData.getType());
    }

    @Test
    public void shouldNotSaveFLWForInvalidLocation() {
        String callerId = "123";
        String name = "name";
        Designation designation = Designation.ANGANWADI;
        LocationList locationList = new LocationList(new ArrayList<Location>());

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, designation.name(), "district", "block", "village", locationList);

        assertEquals("Invalid Location", registrationResponse.getMessage());
        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), eq(RegistrationStatus.REGISTERED));
        verify(registrationMeasureService, never()).createRegistrationMeasure(any(LogData.class));
    }

    @Test
    public void shouldNotSaveFLWForInvalidCallerId() {
        String callerId = "";
        String name = "name";
        Designation designation = Designation.ANGANWADI;
        LocationList locationList = new LocationList(new ArrayList<Location>());
        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, designation.name(), "district", "block", "village", locationList);
        assertEquals("Invalid CallerId", registrationResponse.getMessage());
        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), any(RegistrationStatus.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(any(LogData.class));
        callerId = "abcdef";
        registrationResponse = registrationService.registerFlw(callerId, name, designation.name(), "district", "block", "village", locationList);

        assertEquals("Invalid CallerId", registrationResponse.getMessage());

        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), any(RegistrationStatus.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(any(LogData.class));
    }

    @Test
    public void shouldSaveFLWWithInvalidNameAsPartiallyRegistered() {
        String callerId = "123";
        String name = "";
        Designation designation = Designation.ANGANWADI;
        Location location = new Location("district", "block", "village", 1, 1, 1);
        LocationList locationList = new LocationList(Arrays.asList(location));
        registrationService = new RegistrationService(frontLineWorkerService, registrationMeasureService);

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, designation.name(), "district", "block", "village", locationList);

        assertEquals("New FrontlineWorker added", registrationResponse.getMessage());
        verify(frontLineWorkerService).createOrUpdate(eq(callerId), eq(name), eq(designation), any(Location.class), eq(RegistrationStatus.PARTIALLY_REGISTERED));
    }

    @Test
    public void shouldSaveFLWWithInvalidDesignationAsPartiallyRegistered() {
        String callerId = "123";
        String name = "name";
        String designation = "invalid_designation";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        LocationList locationList = new LocationList(Arrays.asList(location));
        registrationService = new RegistrationService(frontLineWorkerService, registrationMeasureService);

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, designation, "district", "block", "village", locationList);

        assertEquals("New FrontlineWorker added", registrationResponse.getMessage());
        verify(frontLineWorkerService).createOrUpdate(eq(callerId), eq(name), eq(Designation.INVALID), any(Location.class), eq(RegistrationStatus.PARTIALLY_REGISTERED));
    }
}
