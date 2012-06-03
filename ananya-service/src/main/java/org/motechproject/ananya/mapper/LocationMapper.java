package org.motechproject.ananya.mapper;

import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.LocationResponse;

public class LocationMapper {
    public static LocationResponse mapFrom(LocationDimension locationDimension) {
        return new LocationResponse(locationDimension.getDistrict(),
                locationDimension.getBlock(),
                locationDimension.getPanchayat(),
                locationDimension.getLocationId());
    }

    public static Location mapFrom(LocationRequest locationRequest) {
        return new Location(locationRequest.getDistrict(),
                locationRequest.getBlock(),
                locationRequest.getPanchayat(),
                0, 0, 0);
    }
}
