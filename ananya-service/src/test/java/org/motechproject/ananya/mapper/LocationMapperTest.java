package org.motechproject.ananya.mapper;

import org.junit.Test;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.LocationResponse;

import static junit.framework.Assert.assertEquals;

public class LocationMapperTest {

    @Test
    public void shouldMapFromLocationDimensionToResponse() {
        String locationId = "123";
        String district = "d1";
        String block = "b1";
        String panchayat = "p1";
        LocationDimension locationDimension = new LocationDimension(locationId, district, block, panchayat);

        LocationResponse locationResponse = LocationMapper.mapFrom(locationDimension);

        assertEquals(district,locationResponse.getDistrict());
        assertEquals(block,locationResponse.getBlock());
        assertEquals(panchayat,locationResponse.getPanchayat());
        assertEquals(locationId,locationResponse.getExternalId());
    }

    @Test
    public void shouldMapLocationRequestToLocation() {
        String district = "D1";
        String block = "B1";
        String panchayat = "P1";
        LocationRequest locationRequest = new LocationRequest(district, block, panchayat);

        Location location = LocationMapper.mapFrom(locationRequest);

        assertEquals(district, location.getDistrict());
        assertEquals(block, location.getBlock());
        assertEquals(panchayat, location.getPanchayat());
    }
}
