package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.LocationStatus;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.service.dimension.LocationDimensionService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationDimensionServiceTest {

    private LocationDimensionService locationDimensionService;

    @Mock
    private AllLocationDimensions allLocationDimensions;


    @Before
    public void setUp() {
        initMocks(this);
        locationDimensionService = new LocationDimensionService(allLocationDimensions);
    }

    @Test
    public void shouldAddNewLocationDimension() {
        LocationDimension locationDimension = new LocationDimension();

        locationDimensionService.add(locationDimension);

        verify(allLocationDimensions).saveOrUpdate(locationDimension);
    }

    @Test
    public void shouldGetTheLocationDimensionBasedOnExternalId() {
        LocationDimension locationDimension = new LocationDimension();
        String locationCode = "S01D001B001V001";
        when(allLocationDimensions.getFor(locationCode)).thenReturn(locationDimension);

        LocationDimension actualLocalDimension = locationDimensionService.getFor(locationCode);

        assertEquals(locationDimension, actualLocalDimension);
    }

    @Test
    public void shouldGetFilteredLocations() {
        ArrayList<LocationDimension> locationDimensions = new ArrayList<LocationDimension>();
        String panchayat = "p";
        String block = "b";
        String district = "d";
        when(allLocationDimensions.getFilteredLocationFor(district, block, panchayat)).thenReturn(locationDimensions);

        List<LocationDimension> filteredLocations = locationDimensionService.getFilteredLocations(district, block, panchayat);

        assertEquals(locationDimensions, filteredLocations);
    }

    @Test
    public void shouldDeleteLocationDimension() {
        String locationId = "locationId";

        locationDimensionService.delete(locationId);

        verify(allLocationDimensions).delete(locationId);
    }

    @Test
    public void shouldUpdateLocationStatus() {
        String locationCode = "locationCode";
        LocationDimension locationDimension = new LocationDimension(locationCode, null, null, null, "VALID");
        when(allLocationDimensions.getFor(locationCode)).thenReturn(locationDimension);

        locationDimensionService.updateStatus(locationCode, LocationStatus.VALID);

        ArgumentCaptor<LocationDimension> locationDimensionArgumentCaptor = ArgumentCaptor.forClass(LocationDimension.class);
        verify(allLocationDimensions).saveOrUpdate(locationDimensionArgumentCaptor.capture());
        LocationDimension actualLocationDimension = locationDimensionArgumentCaptor.getValue();
        assertEquals(LocationStatus.VALID.name(), actualLocationDimension.getStatus());
    }
}
