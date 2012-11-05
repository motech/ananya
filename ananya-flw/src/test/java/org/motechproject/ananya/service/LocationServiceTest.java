package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationStatus;
import org.motechproject.ananya.repository.AllLocations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationServiceTest {

    private LocationService locationService;

    @Mock
    private AllLocations allLocations;

    @Before
    public void setUp() {
        initMocks(this);
        locationService = new LocationService(allLocations);
    }


    @Test
    public void shouldGetAllLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location());
        when(allLocations.getAll()).thenReturn(locations);

        List<Location> actualLocations = locationService.getAll();

        assertEquals(1, actualLocations.size());
    }

    @Test
    public void shouldAddLocation() {
        Location location = new Location();

        locationService.add(location);

        verify(allLocations).add(location);
    }

    @Test
    public void shouldGetLocation() {
        Location expectedLocation = new Location();
        when(allLocations.findByDistrictBlockPanchayat("D1", "B1", "P1")).thenReturn(expectedLocation);

        Location actualLocation = locationService.findFor("D1", "B1", "P1");

        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    public void shouldFindLocationByExternalId() {
        String locationId = "S01D001B001V001";
        Location location = new Location();
        when(allLocations.findByExternalId(locationId)).thenReturn(location);

        Location actualLocation = locationService.findByExternalId(locationId);

        assertEquals(location, actualLocation);
    }

    @Test
    public void shouldUpdateLocationStatus() {
        Location location = new Location();

        locationService.updateStatus(location, LocationStatus.NOT_VERIFIED);

        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(allLocations).update(locationArgumentCaptor.capture());
        Location value = locationArgumentCaptor.getValue();
        assertEquals(LocationStatus.NOT_VERIFIED.name(), value.getLocationStatus());
    }
}
