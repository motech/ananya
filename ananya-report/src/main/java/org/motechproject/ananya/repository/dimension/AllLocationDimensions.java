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

    public LocationDimension addOrUpdate(LocationDimension locationDimension) {
        LocationDimension existingLocationDimension = getFor(locationDimension.getLocationId());
        if (existingLocationDimension == null) {
            template.save(locationDimension);
            return locationDimension;
        }
        existingLocationDimension.cloneValues(locationDimension);
        template.update(existingLocationDimension);
        return existingLocationDimension;
    }
    
    public int getCount() {
        return DataAccessUtils.intResult(template.find("select count(*) from LocationDimension"));
    }

}