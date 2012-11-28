package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationStatus;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.request.LocationSyncRequest;
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

    @Mock
    private FrontLineWorkerService frontLineWorkerService;

    @Mock
    private RegistrationService registrationService;

    private LocationRegistrationService locationRegistrationService;

    @Before
    public void setUp() {
        initMocks(this);
        locationRegistrationService = new LocationRegistrationService(locationDimensionService, locationService, registrationService);
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
        locationDimensions.add(new LocationDimension(externalId, district, block, panchayat, "VALID"));
        when(locationDimensionService.getFilteredLocations(district, null, null)).thenReturn(locationDimensions);
        LocationRequest locationRequest = new LocationRequest(district, null, null);

        List<LocationResponse> locationResponses = locationRegistrationService.getFilteredLocations(locationRequest);

        assertEquals(1, locationResponses.size());
        assertEquals(district, locationResponses.get(0).getDistrict());
        assertEquals(block, locationResponses.get(0).getBlock());
        assertEquals(panchayat, locationResponses.get(0).getPanchayat());
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
    public void shouldAddNewLocation() {
        LocationRequest locationRequest = new LocationRequest("district", "block", "panchayat");
        when(locationService.getAll()).thenReturn(new ArrayList<Location>());
        String status = LocationStatus.NOT_VERIFIED.name();
        DateTime lastModifiedTime = DateTime.now();

        locationRegistrationService.addOrUpdate(new LocationSyncRequest(locationRequest, locationRequest, status, lastModifiedTime));

        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(locationService).add(locationArgumentCaptor.capture());
        Location location = locationArgumentCaptor.getValue();
        verifyCouchdbLocation(locationRequest, location, status, lastModifiedTime);
        verifyPostgresLocation(locationRequest, status);
        verify(registrationService).updateLocationOnFLW(location, location);
    }

    @Test
    public void shouldUpdateLocationStatusForAnExistingLocation() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        LocationRequest locationRequest = new LocationRequest(district, block, panchayat);
        ArrayList<Location> locationList = new ArrayList<>();
        DateTime lastModifiedTime = DateTime.now();
        Location expectedLocation = new Location(district, block, panchayat, 1, 1, 1, LocationStatus.NOT_VERIFIED, lastModifiedTime);
        locationList.add(expectedLocation);
        when(locationService.getAll()).thenReturn(locationList);

        locationRegistrationService.addOrUpdate(new LocationSyncRequest(locationRequest, locationRequest, LocationStatus.VALID.name(), lastModifiedTime));

        verifyCouchAndPostgresLocationStatusUpdate(expectedLocation, LocationStatus.VALID);
        verify(registrationService).updateLocationOnFLW(expectedLocation, expectedLocation);
    }

    @Test
    public void shouldNotUpdateLocationIfLastModifiedTimeIsGreaterThanRequestLastModifiedTime() {
        String district = "district";
        String block = "block";
        String panchayat = "panchayat";
        LocationRequest locationRequest = new LocationRequest(district, block, panchayat);
        Location expectedLocation = new Location(district, block, panchayat, 1, 1, 1, LocationStatus.NOT_VERIFIED, DateTime.now());
        when(locationService.findFor(district, block, panchayat)).thenReturn(expectedLocation);

        locationRegistrationService.addOrUpdate(new LocationSyncRequest(locationRequest, locationRequest, LocationStatus.VALID.name(), DateTime.now().minusDays(1)));

        verify(locationService, never()).updateStatus(any(Location.class), any(LocationStatus.class));
        verify(locationDimensionService, never()).updateStatus(anyString(), any(LocationStatus.class));
        verify(registrationService, never()).updateLocationOnFLW(expectedLocation, expectedLocation);
    }

    @Test
    public void shouldCreateNewLocationAndUpdateExistingLocationReferencesToTheNewLocation() {
        String oldDistrict = "oldDistrict";
        String oldBlock = "oldBlock";
        String oldPanchayat = "oldPanchayat";
        DateTime lastModifiedTime = DateTime.now();
        LocationRequest oldLocationRequest = new LocationRequest(oldDistrict, oldBlock, oldPanchayat);
        LocationRequest newLocationRequest = new LocationRequest("D1", "B1", "P1");
        ArrayList<Location> locationList = new ArrayList<>();
        Location expectedLocation = new Location(oldDistrict, oldBlock, oldPanchayat, 1, 1, 1, LocationStatus.NOT_VERIFIED, lastModifiedTime);
        locationList.add(expectedLocation);
        String expectedStatus = LocationStatus.VALID.name();
        when(locationService.getAll()).thenReturn(locationList);

        locationRegistrationService.addOrUpdate(new LocationSyncRequest(oldLocationRequest, newLocationRequest, LocationStatus.INVALID.name(), lastModifiedTime));

        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(locationService).add(locationArgumentCaptor.capture());
        Location location = locationArgumentCaptor.getValue();
        verifyCouchdbLocation(newLocationRequest, location, expectedStatus, lastModifiedTime);
        verifyPostgresLocation(newLocationRequest, expectedStatus);
        verify(registrationService).updateLocationOnFLW(expectedLocation, location);
        verify(registrationService).updateAllLocationReferences(expectedLocation.getExternalId(), location.getExternalId());
        verifyCouchAndPostgresLocationStatusUpdate(expectedLocation, LocationStatus.INVALID);
    }

    @Test
    public void shouldCreateNewLocationAndSkipUpdateingExistingLocationReferencesToTheNewLocationIfTheOldLocationIsNotPresent() {
        String oldDistrict = "oldDistrict";
        String oldBlock = "oldBlock";
        String oldPanchayat = "oldPanchayat";
        DateTime lastModifiedTime = DateTime.now();
        LocationRequest oldLocationRequest = new LocationRequest(oldDistrict, oldBlock, oldPanchayat);
        LocationRequest newLocationRequest = new LocationRequest("D1", "B1", "P1");
        ArrayList<Location> locationList = new ArrayList<>();
        Location expectedLocation = new Location(oldDistrict, oldBlock, oldPanchayat, 1, 1, 1, LocationStatus.NOT_VERIFIED, lastModifiedTime);
        String expectedStatus = LocationStatus.VALID.name();
        when(locationService.getAll()).thenReturn(locationList);

        locationRegistrationService.addOrUpdate(new LocationSyncRequest(oldLocationRequest, newLocationRequest, LocationStatus.INVALID.name(), lastModifiedTime));

        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(locationService, times(2)).add(locationArgumentCaptor.capture());
        List<Location> locations = locationArgumentCaptor.getAllValues();
        verifyCouchdbLocation(newLocationRequest, locations.get(0), expectedStatus, lastModifiedTime);
        verifyCouchdbLocation(oldLocationRequest, locations.get(1), LocationStatus.INVALID.name(), lastModifiedTime);

        ArgumentCaptor<LocationDimension> locationDimensionArgumentCaptor = ArgumentCaptor.forClass(LocationDimension.class);
        verify(locationDimensionService, times(2)).add(locationDimensionArgumentCaptor.capture());
        List<LocationDimension> locationDimensions = locationDimensionArgumentCaptor.getAllValues();
        assertLocationEquals(newLocationRequest, expectedStatus, locationDimensions.get(0));
        assertLocationEquals(oldLocationRequest, LocationStatus.INVALID.name(), locationDimensions.get(1));

        verify(registrationService).updateLocationOnFLW(expectedLocation, locations.get(0));
        verify(registrationService, never()).updateAllLocationReferences(expectedLocation.getExternalId(), locations.get(0).getExternalId());
    }

    @Test
    public void shouldNotCreateANewLocationIfTheNewLocationAlreadyExists() {
        String oldDistrict = "oldDistrict";
        String oldBlock = "oldBlock";
        String oldPanchayat = "oldPanchayat";
        LocationRequest oldLocationRequest = new LocationRequest(oldDistrict, oldBlock, oldPanchayat);
        LocationRequest newLocationRequest = new LocationRequest("D1", "B1", "P1");
        ArrayList<Location> locationList = new ArrayList<>();
        DateTime lastModifiedTime = DateTime.now();
        Location expectedLocation = new Location(oldDistrict, oldBlock, oldPanchayat, 1, 1, 1, LocationStatus.NOT_VERIFIED, lastModifiedTime);
        Location newLocation = new Location("D1", "B1", "P1", 1, 2, 3, LocationStatus.VALID, null);
        locationList.add(expectedLocation);
        locationList.add(newLocation);
        when(locationService.getAll()).thenReturn(locationList);

        locationRegistrationService.addOrUpdate(new LocationSyncRequest(oldLocationRequest, newLocationRequest, LocationStatus.INVALID.name(), lastModifiedTime));

        verify(locationService, never()).add(any(Location.class));
        verify(locationDimensionService, never()).add(any(LocationDimension.class));
        verify(registrationService).updateLocationOnFLW(expectedLocation, newLocation);
        verify(registrationService).updateAllLocationReferences(expectedLocation.getExternalId(), newLocation.getExternalId());
        verifyCouchAndPostgresLocationStatusUpdate(expectedLocation, LocationStatus.INVALID);
    }

    @Test
    public void shouldUpdateAllLocationsStatusToValid() {
        locationRegistrationService.updateAllExistingLocationStatusToValid();

        verify(locationService).updateAllLocationStatusToValid();
    }

    private void verifyCouchAndPostgresLocationStatusUpdate(Location expectedLocation, LocationStatus locationStatus) {
        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        verify(locationService).updateStatus(locationArgumentCaptor.capture(), eq(locationStatus));
        Location actualLocation = locationArgumentCaptor.getValue();
        assertEquals(expectedLocation, actualLocation);
        assertEquals(expectedLocation.getLastModifiedTime(), actualLocation.getLastModifiedTime());
        verify(locationDimensionService).updateStatus(expectedLocation.getExternalId(), locationStatus);
    }

    private void verifyPostgresLocation(LocationRequest newLocationRequest, String status) {
        ArgumentCaptor<LocationDimension> locationDimensionArgumentCaptor = ArgumentCaptor.forClass(LocationDimension.class);
        verify(locationDimensionService).add(locationDimensionArgumentCaptor.capture());
        LocationDimension locationDimension = locationDimensionArgumentCaptor.getValue();
        assertLocationEquals(newLocationRequest, status, locationDimension);
    }

    private void assertLocationEquals(LocationRequest newLocationRequest, String status, LocationDimension locationDimension) {
        assertEquals(newLocationRequest.getDistrict(), locationDimension.getDistrict());
        assertEquals(newLocationRequest.getBlock(), locationDimension.getBlock());
        assertEquals(newLocationRequest.getPanchayat(), locationDimension.getPanchayat());
        assertEquals(status, locationDimension.getStatus());
    }

    private void verifyCouchdbLocation(LocationRequest newLocationRequest, Location location, String status, DateTime lastModifiedTime) {
        assertEquals(newLocationRequest.getDistrict(), location.getDistrict());
        assertEquals(newLocationRequest.getBlock(), location.getBlock());
        assertEquals(newLocationRequest.getPanchayat(), location.getPanchayat());
        assertEquals(status, location.getLocationStatus());
        assertEquals(lastModifiedTime, location.getLastModifiedTime());
    }

    private Location getLocationFrom(LocationRequest locationRequest) {
        return new Location(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat(), 0, 0, 0, null, null);
    }
}
