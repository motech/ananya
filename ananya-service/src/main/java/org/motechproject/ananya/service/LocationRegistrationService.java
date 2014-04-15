package org.motechproject.ananya.service;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.LocationStatus;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.mapper.LocationMapper;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.request.LocationSyncRequest;
import org.motechproject.ananya.response.LocationRegistrationResponse;
import org.motechproject.ananya.response.LocationResponse;
import org.motechproject.ananya.response.LocationValidationResponse;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.motechproject.ananya.validators.LocationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class LocationRegistrationService {

    private LocationService locationService;
    private LocationDimensionService locationDimensionService;
    private RegistrationService registrationService;
 // private static final Object SYNC_LOCK = new Object();
 	private static HashMap<String, Object> hmLocationDetailsLock = new HashMap<String, Object>();
    Logger logger = Logger.getLogger(LocationRegistrationService.class);

    @Autowired
    public LocationRegistrationService(LocationDimensionService locationDimensionService, LocationService locationService, RegistrationService registrationService) {
        this.locationService = locationService;
        this.locationDimensionService = locationDimensionService;
        this.registrationService = registrationService;
    }

    public void loadDefaultLocation() {
        Location location = Location.getDefaultLocation();
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat(), location.getLocationStatus());
        locationService.add(location);
        locationDimensionService.add(locationDimension);
    }

    public void addOrUpdate(LocationSyncRequest locationSyncRequest) {
    	Object syncLockObject = getSyncLockObject(locationSyncRequest);
		synchronized (syncLockObject) {
            if (isNotLatestRequest(locationSyncRequest)) {
                logger.info("Not syncing " + locationSyncRequest + " since it is not the latest request.");
                return;
            }

            LocationList locationList = new LocationList(locationService.getAll());
            LocationRequest existingLocationRequest = locationSyncRequest.getExistingLocation();
            LocationRequest newLocationRequest = locationSyncRequest.getNewLocation();
            DateTime lastModifiedTime = locationSyncRequest.getLastModifiedTime();
            if (locationSyncRequest.getLocationStatusAsEnum().equals(LocationStatus.INVALID)) {
                registerLocationForSync(newLocationRequest.getState(), newLocationRequest.getDistrict(), newLocationRequest.getBlock(), newLocationRequest.getPanchayat(), locationList, LocationStatus.VALID, lastModifiedTime);
                Location oldLocation = locationList.getFor(existingLocationRequest.getState(), existingLocationRequest.getDistrict(), existingLocationRequest.getBlock(), existingLocationRequest.getPanchayat());
                Location newLocation = locationList.getFor(newLocationRequest.getState(), newLocationRequest.getDistrict(), newLocationRequest.getBlock(), newLocationRequest.getPanchayat());
                logger.info(String.format("Remapping location references from : %s to: %s", oldLocation, newLocation));
                reMapOldLocationReferences(oldLocation, newLocation);
            }
            createOrUpdateLocation(locationSyncRequest.getExistingLocation(), locationSyncRequest.getLocationStatusAsEnum(), locationList, lastModifiedTime);
            updateLocationDetailsOnFLW(existingLocationRequest, newLocationRequest, locationList);
        }
    }

    private boolean isNotLatestRequest(LocationSyncRequest locationSyncRequest) {
        LocationRequest existingLocationRequest = locationSyncRequest.getExistingLocation();
        Location existingLocation = locationService.findFor(existingLocationRequest.getState(), existingLocationRequest.getDistrict(), existingLocationRequest.getBlock(), existingLocationRequest.getPanchayat());
        return existingLocation != null && existingLocation.getLastModifiedTime() != null && existingLocation.getLastModifiedTime().isAfter(locationSyncRequest.getLastModifiedTime());
    }

    private void updateLocationDetailsOnFLW(LocationRequest existingLocationRequest, LocationRequest newLocationRequest, LocationList locationList) {
        Location oldLocation = locationList.getFor(existingLocationRequest.getState(), existingLocationRequest.getDistrict(), existingLocationRequest.getBlock(), existingLocationRequest.getPanchayat());
        Location newLocation = locationList.getFor(newLocationRequest.getState(), newLocationRequest.getDistrict(), newLocationRequest.getBlock(), newLocationRequest.getPanchayat());
        registrationService.updateLocationOnFLW(oldLocation, newLocation);
    }

    private void createOrUpdateLocation(LocationRequest existingLocationRequest, LocationStatus locationStatus, LocationList locationList, DateTime lastModifiedTime) {
        Location location = locationList.getFor(existingLocationRequest.getState(), existingLocationRequest.getDistrict(), existingLocationRequest.getBlock(), existingLocationRequest.getPanchayat());
        if (location != null)
            updateLocationStatus(location, locationStatus, lastModifiedTime);
        else
            registerLocationForSync(existingLocationRequest.getState(), existingLocationRequest.getDistrict(), existingLocationRequest.getBlock(), existingLocationRequest.getPanchayat(), locationList, locationStatus, lastModifiedTime);
    }

    private void updateLocationStatus(Location location, LocationStatus status, DateTime lastModifiedTime) {
        logger.info(String.format("updating location: %s with status : %s", location.getId(), status));
        location.setLastModifiedTime(lastModifiedTime);
        locationService.updateStatus(location, status);
        locationDimensionService.updateStatus(location.getExternalId(), status);
    }

    private void reMapOldLocationReferences(Location existingLocation, Location newLocation) {
        if (oldLocationHasNotSyncedYet(existingLocation)) {
            logger.info(String.format("Location %s did not exist previously. No remapping to be done", newLocation));
            return;
        }
        registrationService.updateAllLocationReferences(existingLocation.getExternalId(), newLocation.getExternalId());
    }

    private boolean oldLocationHasNotSyncedYet(Location existingLocation) {
        return existingLocation == null;
    }

    public List<LocationRegistrationResponse> registerAllLocationsWithDefaultLocations(List<LocationRequest> locationsToSave) {
        LocationList locationList = new LocationList(locationService.getAll());
        List<LocationRegistrationResponse> responses = saveLocations(locationsToSave, locationList);
        registerDefaultLocationForStateDistrictBlock(locationList);
        return responses;
    }

    private void registerLocationForSync(String state, String district, String block, String panchayat, LocationList locationList, LocationStatus locationStatus, DateTime lastModifiedTime) {
        if (locationList.getFor(state, district, block, panchayat) != null) {
            logger.info(String.format("Not saving new location since state: %s, district : %s, block : %s, panchayat: %s already exists.", state, district, block, panchayat));
            return;
        }
        saveNewLocation(new Location(state, district, block, panchayat, 0, 0, 0, 0, locationStatus, lastModifiedTime), locationList);
    }

    private LocationRegistrationResponse registerLocation(String state, String district, String block, String panchayat, LocationList locationList, LocationStatus locationStatus) {
        LocationRegistrationResponse response = new LocationRegistrationResponse(state, district, block, panchayat);
        Location location = new Location(state ,district, block, panchayat, 0, 0, 0, 0, locationStatus, null);
        LocationValidator locationValidator = new LocationValidator(locationList);
        LocationValidationResponse validationResponse = locationValidator.validate(location);
        if (validationResponse.isInValid())
            return response.withValidationResponse(validationResponse);

        saveNewLocation(location, locationList);

        return response.withSuccessfulRegistration();
    }

    public List<LocationResponse> getFilteredLocations(LocationRequest request) {
        List<LocationDimension> filteredLocations = locationDimensionService.getFilteredLocations(request.getState(), request.getDistrict(), request.getBlock(), request.getPanchayat());
        List<LocationResponse> locationResponses = new ArrayList<>();
        for (LocationDimension locationDimension : filteredLocations) {
            locationResponses.add(LocationMapper.mapFrom(locationDimension));
        }
        return locationResponses;
    }

    public void updateAllExistingLocationStatusToValid() {
        locationService.updateAllLocationStatusToValid();
    }

    public void updateAllNullLocationStateName(String stateName){
    	locationService.updateAllLocationStateName(stateName);
    }
    
    private List<LocationRegistrationResponse> saveLocations(List<LocationRequest> locationsToSave, LocationList locationList) {
        List<LocationRegistrationResponse> responses = new ArrayList<>();
        for (LocationRequest location : locationsToSave) {
            LocationRegistrationResponse locationRegistrationResponse = registerLocation(location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat(), locationList, LocationStatus.VALID);
            responses.add(locationRegistrationResponse);
        }
        return responses;
    }

    private void registerDefaultLocationForStateDistrictBlock(LocationList locationList) {
        List<Location> uniqueStateDistrictBlockLocations = locationList.getUniqueStateDistrictBlockLocations();
        for (Location location : uniqueStateDistrictBlockLocations) {
            LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat(), location.getLocationStatus());
            locationService.add(location);
            locationDimensionService.add(locationDimension);
        }
    }

    private void saveNewLocation(Location currentLocation, LocationList locationList) {
        Location location = createNewLocation(currentLocation, locationList);
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getState(), location.getDistrict(), location.getBlock(), location.getPanchayat(), currentLocation.getLocationStatus());
        locationService.add(location);
        logger.info("Saved location to couchDB : " + location);
        locationDimensionService.add(locationDimension);
        logger.info("Saved location to postgresDB : " + locationDimension);
        locationList.add(location);
    }

    private Location createNewLocation(Location currentLocation, LocationList locationList) {
    	Integer stateCodeFor = locationList.getStateCodeFor(currentLocation);
        Integer districtCodeFor = locationList.getDistrictCodeFor(currentLocation);
        Integer blockCodeFor = locationList.getBlockCodeFor(currentLocation);
        Integer panchayatCodeFor = locationList.getPanchayatCodeFor(currentLocation);
        Location locationToSave = new Location(currentLocation.getState(), currentLocation.getDistrict(), currentLocation.getBlock(), currentLocation.getPanchayat(), stateCodeFor, districtCodeFor, blockCodeFor, panchayatCodeFor, currentLocation.getLocationStatusAsEnum(), currentLocation.getLastModifiedTime());
        return locationToSave;
    }

    public void updateAllLocationsToTitleCase() {
        List<Location> locationList = locationService.getAll();
        for (Location location : locationList) {
            location.convertToTitleCase();
        }
        locationService.updateAll(locationList);
    }

	public void updateExternalId(String currentExternalId, String newExternalId) {
		locationService.updateLocationExternalId(currentExternalId, newExternalId);
	}

	public void updateStateNameByExternalId(String externalId, String state) {
		locationService.updateStateNameByExternalId(externalId, state);
	}
	
	private Object getSyncLockObject(LocationSyncRequest locationSyncRequest){
		LocationRequest existingLocationRequest = locationSyncRequest.getExistingLocation();
		String key = getKey(new Location(existingLocationRequest.getState(), existingLocationRequest.getDistrict(), existingLocationRequest.getBlock(), existingLocationRequest.getPanchayat()));
		if(!hmLocationDetailsLock.containsKey(key))
			hmLocationDetailsLock.put(key, new Object());
		return hmLocationDetailsLock.get(key);
	}

	private String getKey(Location location){
		return location.getState()+"_"+location.getDistrict()+"_"+location.getBlock()+"_"+location.getPanchayat();
	}
}
