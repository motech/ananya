package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.LocationRegistrationResponse;
import org.motechproject.ananya.response.LocationResponse;
import org.motechproject.ananya.service.dimension.LocationDimensionService;

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
        when(locationService.getAll()).thenReturn(new ArrayList<Location>());

        LocationRegistrationResponse response = locationRegistrationService.addNewLocation(new LocationRequest("D1", "", "V1"));

        assertEquals("[One or more of District, Block, Panchayat details are missing]", response.getMessage());
        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(locationService, never()).add(captor.capture());
    }

    @Test
    public void shouldNotRegisterALocationIfItIsAlreadyPresent() {
        ArrayList<Location> locations = new ArrayList<Location>();
        locations.add(new Location("D1", "B1", "V1", 1, 1, 1));
        when(locationService.getAll()).thenReturn(locations);
        locationRegistrationService = new LocationRegistrationService(locationDimensionService, locationService);

        LocationRegistrationResponse response = locationRegistrationService.addNewLocation(new LocationRequest("D1", "B1", "V1"));

        assertEquals("[The location is already present]", response.getMessage());
        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(locationService, never()).add(captor.capture());
    }

    @Test
    public void shouldSaveTheLocationWhenRegisteringIt() {
        String district = "D1";
        String block = "B1";
        String panchayat = "V1";
        String externalId = "S01D001B001V001";
        ArrayList<Location> locations = new ArrayList<Location>();
        when(locationService.getAll()).thenReturn(locations);

        LocationRegistrationResponse response = locationRegistrationService.addNewLocation(new LocationRequest(district, block, panchayat));

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
    public void shouldGetFilteredLocationList() {
        List<LocationDimension> locationDimensions = new ArrayList<LocationDimension>();
        String externalId = "121";
        String block = "B1";
        String district = "D1";
        String panchayat = "P1";
        locationDimensions.add(new LocationDimension(externalId, district, block, panchayat));
        when(locationDimensionService.getFilteredLocations(district, null, null)).thenReturn(locationDimensions);
        LocationRequest locationRequest = new LocationRequest(district, null, null);

        List<LocationResponse> locationResponses = locationRegistrationService.getFilteredLocations(locationRequest);

        assertEquals(1, locationResponses.size());
        assertEquals(district, locationResponses.get(0).getDistrict());
        assertEquals(block, locationResponses.get(0).getBlock());
        assertEquals(panchayat, locationResponses.get(0).getPanchayat());
        assertEquals(externalId, locationResponses.get(0).getExternalId());
    }

    @Test
    public void shouldSaveAllLocationsAndCreateDefaultLocationsForTheSame() {
        List<LocationRequest> locations = new ArrayList<LocationRequest>();
        LocationRequest location1 = new LocationRequest("D1", "B1", "P1");
        locations.add(location1);
        LocationRequest location2 = new LocationRequest("D2", "B2", "P5");
        locations.add(location2);
        LocationRequest location3 = new LocationRequest("D1", "B3", "P2");
        locations.add(location3);
        LocationRequest defaultLocation1 = new LocationRequest("D1", "B1", "");
        LocationRequest defaultLocation2 = new LocationRequest("D2", "B2", "");
        LocationRequest defaultLocation3 = new LocationRequest("D1", "B3", "");
        when(locationService.getAll()).thenReturn(new ArrayList<Location>());

        locationRegistrationService.registerAllLocationsWithDefaultLocations(locations);

        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(locationService, times(6)).add(captor.capture());
        List<Location> allValues = captor.getAllValues();
        assertEquals(getLocationFrom(location1), allValues.get(0));
        assertEquals(getLocationFrom(location2), allValues.get(1));
        assertEquals(getLocationFrom(location3), allValues.get(2));
        assertEquals(getLocationFrom(defaultLocation1), allValues.get(3));
        assertEquals(getLocationFrom(defaultLocation2), allValues.get(4));
        assertEquals(getLocationFrom(defaultLocation3), allValues.get(5));
    }

    @Test
    public void shouldSaveAllLocationsWithoutCreatingDefaultLocationsForTheSame() {
        List<LocationRequest> locations = new ArrayList<LocationRequest>();
        LocationRequest location1 = new LocationRequest("D1", "B1", "P1");
        locations.add(location1);
        LocationRequest location2 = new LocationRequest("D2", "B2", "P5");
        locations.add(location2);
        LocationRequest location3 = new LocationRequest("D1", "B3", "P2");
        locations.add(location3);
        when(locationService.getAll()).thenReturn(new ArrayList<Location>());

        locationRegistrationService.registerAllLocations(locations);

        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(locationService, times(3)).add(captor.capture());
        List<Location> allValues = captor.getAllValues();
        assertEquals(getLocationFrom(location1), allValues.get(0));
        assertEquals(getLocationFrom(location2), allValues.get(1));
        assertEquals(getLocationFrom(location3), allValues.get(2));
    }

    private Location getLocationFrom(LocationRequest locationRequest) {
        return new Location(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat(), 0, 0, 0);
    }
}
