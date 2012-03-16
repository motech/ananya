package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.response.RegistrationResponse;

import static junit.framework.Assert.*;
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
        Location location = new Location();

        when(locationService.fetchFor("district", "block", "village")).thenReturn(location);

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, "ANM", "district", "block", "village");

        assertTrue(registrationResponse.isRegistered());
        assertEquals("New FrontlineWorker added", registrationResponse.message());

        verify(frontLineWorkerService).createOrUpdate(callerId, name, "ANM", location);
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

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, "ANM", "district", "block", "village");

        assertFalse(registrationResponse.isRegistered());
        assertEquals("Invalid Location", registrationResponse.message());

        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq("ANM"), any(Location.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(any(LogData.class));
    }

    @Test
    public void shouldNotSaveFLWForInvalidCallerId() {
        String callerId = "";
        String name = "name";

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, "ANM", "district", "block", "village");

        assertFalse(registrationResponse.isRegistered());
        assertEquals("Invalid CallerId", registrationResponse.message());

        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq("ANM"), any(Location.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(any(LogData.class));
    }

    @Test
    public void shouldNotSaveFLWForInvalidName() {
        String callerId = "123";
        String name = "";

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, "ANM", "district", "block", "village");

        assertFalse(registrationResponse.isRegistered());
        assertEquals("Invalid Name", registrationResponse.message());

        verify(frontLineWorkerService, never()).createOrUpdate(eq(callerId), eq(name), eq("ANM"), any(Location.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(any(LogData.class));
    }
}
