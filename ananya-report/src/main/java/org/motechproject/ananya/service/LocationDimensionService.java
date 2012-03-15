package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationDimensionService {

    private AllLocationDimensions allLocationDimensions;

    @Autowired
    public LocationDimensionService(AllLocationDimensions allLocationDimensions) {
        this.allLocationDimensions = allLocationDimensions;
    }

    public void add(LocationDimension locationDimension) {
        allLocationDimensions.add(locationDimension);
    }

    public LocationDimension getFor(String externalId) {
        return allLocationDimensions.getFor(externalId);
    }
}
