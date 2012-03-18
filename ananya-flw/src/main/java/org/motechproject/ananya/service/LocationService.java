package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private AllLocations allLocations;

    @Autowired
    public LocationService(AllLocations allLocations) {
        this.allLocations = allLocations;
    }

    public Location fetchFor(String district, String block, String village) {
        throw new RuntimeException("Not implemented.");
    }

    public Location findByExternalId(String locationId) {
        return allLocations.findByExternalId(locationId);
    }

    public List<Location> getAll() {
        return allLocations.getAll();
    }

    public void add(Location location) {
        allLocations.add(location);
    }
}
