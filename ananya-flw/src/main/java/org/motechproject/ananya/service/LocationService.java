package org.motechproject.ananya.service;

import org.motechproject.ananya.repository.AllLocations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private AllLocations allLocations;

    @Autowired
    public LocationService(AllLocations allLocations) {
        this.allLocations = allLocations;
    }

    
}
