package org.motechproject.ananya.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;

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

        verify(allLocationDimensions).add(locationDimension);
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
        LocationDimension locationDimension = new LocationDimension();
        ArrayList<LocationDimension> locationDimensions = new ArrayList<LocationDimension>();
        String panchayat = "p";
        String block = "b";
        String district = "d";
        when(allLocationDimensions.getFilteredLocationFor(district, block, panchayat)).thenReturn(locationDimensions);

        List<LocationDimension> filteredLocations = locationDimensionService.getFilteredLocations(district, block, panchayat);

        assertEquals(locationDimensions, filteredLocations);
    }
}
