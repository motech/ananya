package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.requests.LogData;
import org.motechproject.ananya.requests.LogType;
import org.motechproject.ananya.response.RegistrationResponse;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
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
    public void shouldSaveNewFLWAndPublishForReportIfFlwIsNotAlreadyPresent() {
        String callerId = "123";
        String name = "name";
        Location location = new Location();

        when(locationService.fetchFor("district", "block", "village")).thenReturn(location);

        RegistrationResponse registrationResponse = registrationService.registerFlw(callerId, name, "district", "block", "village");

        assertTrue(registrationResponse.isRegistered());
        assertEquals("New FrontlineWorker added", registrationResponse.message());

        verify(frontLineWorkerService).createNew(callerId, name, location);
        ArgumentCaptor<LogData> logDataArgumentCaptor = ArgumentCaptor.forClass(LogData.class);
        verify(registrationMeasureService).createRegistrationMeasure(logDataArgumentCaptor.capture());
        LogData logData = logDataArgumentCaptor.getValue();
        assertEquals(callerId, logData.getDataId());
        assertEquals(LogType.REGISTRATION, logData.getType());
    }
}
