package org.motechproject.ananya.importer.csv.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.motechproject.ananya.service.LocationService;
import org.motechproject.importer.domain.ValidationResponse;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationImporterTest {

    @Mock
    private LocationService locationService;
    @Mock
    private LocationRegistrationService locationRegistrationService;

    @Captor
    private ArgumentCaptor<List<LocationRequest>> captor;
    private LocationImporter locationImporter;

    @Before
    public void setUp() {
        initMocks(this);
        locationImporter = new LocationImporter(locationRegistrationService, locationService);
    }

    @Test
    public void shouldValidateLocationRequests() {
        ArrayList<Object> locationRequests = new ArrayList<Object>();
        when(locationService.getAll()).thenReturn(new ArrayList<Location>());
        locationRequests.add(new LocationRequest("D1", "B1", "P1"));

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertTrue(validationResponse.isValid());
        assertEquals(0, validationResponse.getErrors().size());
    }

    @Test
    public void shouldFailValidationIfLocationDoesNotHaveAllTheDetails() {
        ArrayList<Object> locationRequests = new ArrayList<Object>();
        when(locationService.getAll()).thenReturn(new ArrayList<Location>());
        locationRequests.add(new LocationRequest("D1", "B1", null));

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(1, validationResponse.getErrors().size());
        assertEquals("D1,B1,null,One or more of District, Block, Panchayat details are missing", validationResponse.getErrors().get(0).getMessage());
    }

    @Test
    public void shouldFailValidationIfThereAreDuplicateLocations() {
        ArrayList<Object> locationRequests = new ArrayList<Object>();
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        when(locationService.getAll()).thenReturn(locations);
        locationRequests.add(new LocationRequest("D1", "B1", "P1"));

        ValidationResponse validationResponse = locationImporter.validate(locationRequests);

        assertFalse(validationResponse.isValid());
        assertEquals(1, validationResponse.getErrors().size());
        assertEquals("D1,B1,P1,The location is already present", validationResponse.getErrors().get(0).getMessage());
    }

    @Test
    public void shouldSaveLocation() {
        ArrayList<Object> locationRequests = new ArrayList<Object>();
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        when(locationService.getAll()).thenReturn(locations);
        locationRequests.add(new LocationRequest("D1", "B1", "P1"));

        locationImporter.postData(locationRequests);

        verify(locationRegistrationService).registerAllLocations(captor.capture());
        List<LocationRequest> locationRequestsToSave = captor.getValue();
        assertEquals(1, locationRequestsToSave.size());
        assertEquals("D1", locationRequestsToSave.get(0).getDistrict());
        assertEquals("B1", locationRequestsToSave.get(0).getBlock());
        assertEquals("P1", locationRequestsToSave.get(0).getPanchayat());
    }
}
