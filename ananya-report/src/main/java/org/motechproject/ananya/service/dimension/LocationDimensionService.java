package org.motechproject.ananya.service.dimension;

import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationDimensionService {

    private AllLocationDimensions allLocationDimensions;

    public LocationDimensionService() {
    }

    @Autowired
    public LocationDimensionService(AllLocationDimensions allLocationDimensions) {
        this.allLocationDimensions = allLocationDimensions;
    }

    @Transactional
    public void add(LocationDimension locationDimension) {
        allLocationDimensions.add(locationDimension);
    }

    @Transactional
    public LocationDimension getFor(String externalId) {
        return allLocationDimensions.getFor(externalId);
    }

    @Transactional
    public List<LocationDimension> getFilteredLocations(String district, String block, String panchayat) {
        return allLocationDimensions.getFilteredLocationFor(district, block, panchayat);
    }
}
