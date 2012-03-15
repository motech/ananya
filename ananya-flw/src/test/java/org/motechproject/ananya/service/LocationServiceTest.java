package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.repository.AllLocations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

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

        assertEquals(1,actualLocations.size());
    }

    @Test
    public void shouldAddLocation() {
        Location location = new Location();

        locationService.add(location);

        verify(allLocations).add(location);
    }

    @Test
    public void shouldRemoveAllLocations() {
        locationService.removeAll();
        verify(allLocations).removeAll();
    }

    @Test
    public void shouldFindLocationByExternalId() {
        String locationId = "S01D001B001V001";
        Location location = new Location();
        when(allLocations.findByExternalId(locationId)).thenReturn(location);

        Location actualLocation = locationService.findByExternalId(locationId);

        assertEquals(location, actualLocation);
    }
}
