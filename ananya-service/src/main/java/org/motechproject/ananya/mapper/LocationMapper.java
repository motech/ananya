package org.motechproject.ananya.mapper;

import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.response.LocationResponse;

public class LocationMapper {
    public static LocationResponse mapFrom(LocationDimension locationDimension) {
        return new LocationResponse(locationDimension.getState(),
        		locationDimension.getDistrict(),
                locationDimension.getBlock(),
                locationDimension.getPanchayat()
        );
    }
}
