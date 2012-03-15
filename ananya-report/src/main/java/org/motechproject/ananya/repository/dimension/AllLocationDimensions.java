package org.motechproject.ananya.repository.dimension;

import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

@Repository
public class AllLocationDimensions {

    @Autowired
    private DataAccessTemplate template;

    public LocationDimension getFor(String locationCode) {
        return (LocationDimension) template.getUniqueResult(LocationDimension.FIND_BY_LOCATION_ID, new String[]{"location_id"}, new Object[]{locationCode});
    }

    public LocationDimension add(LocationDimension locationDimension) {
        template.save(locationDimension);
        return locationDimension;
    }
}
