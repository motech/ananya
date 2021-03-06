package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationStatus;
import org.motechproject.ananya.repository.AllLocations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
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
        when(allLocations.findByStateDistrictBlockPanchayat("S1", "D1", "B1", "P1")).thenReturn(expectedLocation);
        
        Location actualLocation = locationService.findFor("S1", "D1", "B1", "P1");

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

    @Test
    public void shouldUpdateAllLocationStatusToValidAndCallGetAllAtTheEndForViewIndexing() {
        ArrayList<Location> locations = new ArrayList<>();
        InOrder inOrder = inOrder(allLocations);
        ArgumentCaptor<Location> locationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        Location expectedLocation = new Location("S1", "D1", "B1", "P1");
        locations.add(expectedLocation);
        when(allLocations.getAll()).thenReturn(locations);

        locationService.updateAllLocationStatusToValid();

        inOrder.verify(allLocations).getAll();
        inOrder.verify(allLocations).update(locationArgumentCaptor.capture());
        inOrder.verify(allLocations).getAll();

        Location actualLocation = locationArgumentCaptor.getValue();
        assertEquals(expectedLocation, actualLocation);
        assertEquals(LocationStatus.VALID, actualLocation.getLocationStatusAsEnum());
    }

    @Test
    public void shouldUpdateAllLocations() {
        ArrayList<Location> locationList = new ArrayList<>();
        Location location1 = new Location("S1", "D11", "B1", "P1");
        Location location2 = new Location("S2", "D12", "B2", "P2");
        locationList.add(location1);
        locationList.add(location2);

        locationService.updateAll(locationList);

        verify(allLocations).update(location1);
        verify(allLocations).update(location2);
    }
    
    @Test
    public void shouldUpdateStateNameAndExternalIdForDefaultLocation(){
        String oldDefaultLocExtId="S01D000B000V000";
		String newDefaultLocExtId="S00D000B000V000";
		String defaultLocStateName = "C00";
    	
    	Location olderDefaultLocation = new Location(null, "C00", "C00", null, 1, 0, 0, 0, null, new DateTime());
    	olderDefaultLocation.setExternalId(oldDefaultLocExtId);
    	
    	Location newDefaultLocation = new Location(null, "C00", "C00", null, 0, 0, 0, 0, null, new DateTime());
    	newDefaultLocation.setExternalId(newDefaultLocExtId);
    	
        when(allLocations.findByExternalId(oldDefaultLocExtId)).thenReturn(olderDefaultLocation);
        when(allLocations.findByExternalId(newDefaultLocExtId)).thenReturn(newDefaultLocation);
        
        locationService.updateLocationExternalId(oldDefaultLocExtId, newDefaultLocExtId);
        locationService.updateStateNameByExternalId(newDefaultLocExtId, defaultLocStateName);
        
        assertEquals(olderDefaultLocation.getExternalId(), newDefaultLocExtId);
        assertEquals(newDefaultLocation.getState(), defaultLocStateName);
    }
}
