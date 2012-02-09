package org.motechproject.ananya.repository;

import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllLocationDimensions {

    @Autowired
    private DataAccessTemplate template;

    public LocationDimension getLocationDimension(String locationCode, String district,
                                                  String block, String panchayat) {
        LocationDimension locationDimension = (LocationDimension) template.getUniqueResult(
                LocationDimension.FIND_BY_LOCATION_ID, new String[]{"location_id"}, new Object[]{locationCode});

        return locationDimension != null ? locationDimension :
                new LocationDimension(locationCode, district, block, panchayat);
    }

    public LocationDimension fetchLocationDimensionFromDB(String locationCode) {
        return (LocationDimension) template.getUniqueResult(
                LocationDimension.FIND_BY_LOCATION_ID, new String[]{"location_id"}, new Object[]{locationCode});
    }
}
