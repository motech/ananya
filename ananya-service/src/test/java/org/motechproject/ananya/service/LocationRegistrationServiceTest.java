package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.response.LocationRegistrationResponse;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationRegistrationServiceTest {

    @Mock
    private LocationService locationService;

    @Mock
    private LocationDimensionService locationDimensionService;

    private LocationRegistrationService locationRegistrationService;

    @Before
    public void setUp() {
        initMocks(this);
        locationRegistrationService = new LocationRegistrationService(locationDimensionService, locationService);
    }

    @Test
    public void shouldNotRegisterALocationIfItIsMissingDetails() {
        LocationRegistrationResponse response = locationRegistrationService.registerLocation("D1", "", "V1");

        assertEquals("One or more of District, Block details are missing", response.getMessage());
        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(locationService, never()).add(captor.capture());
    }

    @Test
    public void shouldNotRegisterALocationIfItIsAlreadyPresent() {
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "V1", 1, 1, 1));
        when(locationService.getAll()).thenReturn(locations);
        locationRegistrationService = new LocationRegistrationService(locationDimensionService, locationService);

        LocationRegistrationResponse response = locationRegistrationService.registerLocation("D1", "B1", "V1");

        assertEquals("The location is already present", response.getMessage());
        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(locationService, never()).add(captor.capture());
    }

    @Test
    public void shouldSaveTheLocationWhenRegisteringIt() {
        String district = "D1";
        String block = "B1";
        String panchayat = "V1";
        String externalId = "S01D001B001V001";

        LocationRegistrationResponse response = locationRegistrationService.registerLocation(district, block, panchayat);

        assertEquals("Successfully registered location", response.getMessage());

        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        verify(locationService).add(locationCaptor.capture());
        Location location = locationCaptor.getValue();
        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
        assertEquals(externalId, location.getExternalId());

        ArgumentCaptor<LocationDimension> locationDimensionCaptor = ArgumentCaptor.forClass(LocationDimension.class);
        verify(locationDimensionService).add(locationDimensionCaptor.capture());
        LocationDimension locationDimension = locationDimensionCaptor.getValue();
        assertEquals(district, locationDimension.getDistrict());
        assertEquals(block, locationDimension.getBlock());
        assertEquals(panchayat, locationDimension.getPanchayat());
        assertEquals(externalId, locationDimension.getLocationId());
    }

    @Test
    public void shouldSaveDefaultLocationAndDefaultLocationDimension() {
        locationRegistrationService.loadDefaultLocation();

        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        ArgumentCaptor<LocationDimension> locationDimensionCaptor = ArgumentCaptor.forClass(LocationDimension.class);
        verify(locationService).add(locationCaptor.capture());
        verify(locationDimensionService).add(locationDimensionCaptor.capture());

        Location location = locationCaptor.getValue();
        LocationDimension locationDimension = locationDimensionCaptor.getValue();

        assertEquals("S01D000B000V000", location.getExternalId());
        assertEquals("S01D000B000V000", locationDimension.getLocationId());
    }

    @Test
    public void shouldSaveDefaultLocationForAllLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "P1", 1, 1, 1));
        locations.add(new Location("D1", "B1", "P2", 1, 1, 2));
        locations.add(new Location("D1", "B2", "P2", 1, 2, 2));
        locations.add(new Location("D2", "B3", "P3", 2, 1, 1));
        when(locationService.getAll()).thenReturn(locations);
        locationRegistrationService = new LocationRegistrationService(locationDimensionService, locationService);

        locationRegistrationService.registerDefaultLocationForDistrictBlock();

        ArgumentCaptor<Location> locationCaptor = ArgumentCaptor.forClass(Location.class);
        ArgumentCaptor<LocationDimension> locationDimensionCaptor = ArgumentCaptor.forClass(LocationDimension.class);
        verify(locationService, times(3)).add(locationCaptor.capture());
        verify(locationDimensionService, times(3)).add(locationDimensionCaptor.capture());
        List<Location> defaultLocations = locationCaptor.getAllValues();
        List<LocationDimension> defaultLocationDimensions = locationDimensionCaptor.getAllValues();
        assertEquals("S01D001B001V000", defaultLocations.get(0).getExternalId());
        assertEquals("S01D001B001V000", defaultLocationDimensions.get(0).getLocationId());
        assertEquals("S01D001B002V000", defaultLocations.get(1).getExternalId());
        assertEquals("S01D001B002V000", defaultLocationDimensions.get(1).getLocationId());
        assertEquals("S01D002B001V000", defaultLocations.get(2).getExternalId());
        assertEquals("S01D002B001V000", defaultLocationDimensions.get(2).getLocationId());
    }
}
