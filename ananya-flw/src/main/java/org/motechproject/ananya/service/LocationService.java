package org.motechproject.ananya.service;

import org.apache.commons.lang.WordUtils;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationStatus;
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

    public Location findByExternalId(String locationId) {
        return allLocations.findByExternalId(locationId);
    }

    public List<Location> getAll() {
        return allLocations.getAll();
    }

    public void add(Location location) {
        allLocations.add(location);
    }

    public Location findFor(String state, String district, String block, String panchayat) {
    	 return allLocations.findByStateDistrictBlockPanchayat(state, district, block, panchayat);
    }

    public void updateStatus(Location location, LocationStatus status) {
        location.setLocationStatus(status.name());
        allLocations.update(location);
    }

    public void updateAllLocationStatusToValid() {
        List<Location> locationList = allLocations.getAll();
        for (Location location : locationList) {
            location.setLocationStatus(LocationStatus.VALID.name());
            allLocations.update(location);
        }
        allLocations.getAll();
    }

    public void updateAllLocationStateName(String state) {
        List<Location> locationList = allLocations.getAll();
        for (Location location : locationList) {
            if(location.getState()==null){
            	location.setState(WordUtils.capitalizeFully(state));
            	allLocations.update(location);
            }
        }
        allLocations.getAll();
    }
    
    public void updateAll(List<Location> locationList) {
        for (Location location : locationList) {
            allLocations.update(location);
        }
    }

	public void updateLocationExternalId(String currentLocationCode, String newLocationCode) {
		Location location = findByExternalId(currentLocationCode);
		if(location!=null){
			location.setExternalId(newLocationCode);
			allLocations.update(location);
		}
	}

	public void updateStateNameByExternalId(String externalId, String state) {
		Location location = findByExternalId(externalId);
		location.setState(WordUtils.capitalizeFully(state));
		allLocations.update(location);
	}
}