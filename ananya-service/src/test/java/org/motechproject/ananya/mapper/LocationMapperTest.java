package org.motechproject.ananya.mapper;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.response.LocationResponse;

public class LocationMapperTest {

    @Test
    public void shouldMapFromLocationDimensionToResponse() {
        String locationId = "123";
        String state= " state";
        String district = "d1";
        String block = "b1";
        String panchayat = "p1";
        LocationDimension locationDimension = new LocationDimension(locationId, state, district, block, panchayat, "VALID");

        LocationResponse locationResponse = LocationMapper.mapFrom(locationDimension);

        assertEquals(state,locationResponse.getState());
        assertEquals(district,locationResponse.getDistrict());
        assertEquals(block,locationResponse.getBlock());
        assertEquals(panchayat,locationResponse.getPanchayat());
    }
}
