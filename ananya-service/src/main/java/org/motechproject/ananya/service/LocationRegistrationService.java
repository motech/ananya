package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.mapper.LocationMapper;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.LocationRegistrationResponse;
import org.motechproject.ananya.response.LocationResponse;
import org.motechproject.ananya.response.LocationValidationResponse;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.motechproject.ananya.validators.LocationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationRegistrationService {

    private LocationService locationService;
    private LocationDimensionService locationDimensionService;

    @Autowired
    public LocationRegistrationService(LocationDimensionService locationDimensionService, LocationService locationService) {
        this.locationService = locationService;
        this.locationDimensionService = locationDimensionService;
    }

    public void loadDefaultLocation() {
        Location location = Location.getDefaultLocation();
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat(), "VALID");
        locationService.add(location);
        locationDimensionService.add(locationDimension);
    }

    public LocationRegistrationResponse addNewLocation(LocationRequest request) {
        LocationList locationList = new LocationList(locationService.getAll());
        return registerLocation(request.getDistrict(), request.getBlock(), request.getPanchayat(), locationList);
    }

    public List<LocationRegistrationResponse> registerAllLocationsWithDefaultLocations(List<LocationRequest> locationsToSave) {
        LocationList locationList = new LocationList(locationService.getAll());
        List<LocationRegistrationResponse> responses = saveLocations(locationsToSave, locationList);
        registerDefaultLocationForDistrictBlock(locationList);
        return responses;
    }

    public List<LocationRegistrationResponse> registerAllLocations(List<LocationRequest> locationsToSave) {
        LocationList locationList = new LocationList(locationService.getAll());
        List<LocationRegistrationResponse> responses = saveLocations(locationsToSave, locationList);
        return responses;
    }

    public List<LocationResponse> getFilteredLocations(LocationRequest request) {
        List<LocationDimension> filteredLocations = locationDimensionService.getFilteredLocations(request.getDistrict(), request.getBlock(), request.getPanchayat());
        List<LocationResponse> locationResponses = new ArrayList<>();
        for (LocationDimension locationDimension : filteredLocations) {
            locationResponses.add(LocationMapper.mapFrom(locationDimension));
        }
        return locationResponses;
    }

    private List<LocationRegistrationResponse> saveLocations(List<LocationRequest> locationsToSave, LocationList locationList) {
        List<LocationRegistrationResponse> responses = new ArrayList<LocationRegistrationResponse>();
        for (LocationRequest location : locationsToSave) {
            LocationRegistrationResponse locationRegistrationResponse = registerLocation(location.getDistrict(), location.getBlock(), location.getPanchayat(), locationList);
            responses.add(locationRegistrationResponse);
        }
        return responses;
    }

    private void registerDefaultLocationForDistrictBlock(LocationList locationList) {
        List<Location> uniqueDistrictBlockLocations = locationList.getUniqueDistrictBlockLocations();
        for (Location location : uniqueDistrictBlockLocations) {
            LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat(), "VALID");
            locationService.add(location);
            locationDimensionService.add(locationDimension);
        }
    }

    private LocationRegistrationResponse registerLocation(String district, String block, String panchayat, LocationList locationList) {
        LocationRegistrationResponse response = new LocationRegistrationResponse(district, block, panchayat);
        Location location = new Location(district, block, panchayat, 0, 0, 0, null);
        LocationValidator locationValidator = new LocationValidator(locationList);
        LocationValidationResponse validationResponse = locationValidator.validate(location);
        if(validationResponse.isInValid())
            return response.withValidationResponse(validationResponse);

        saveNewLocation(location, locationList);

        return response.withSuccessfulRegistration();
    }

    private void saveNewLocation(Location currentLocation, LocationList locationList) {
        Location location = createNewLocation(currentLocation, locationList);
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat(), "VALID");

        locationService.add(location);
        locationDimensionService.add(locationDimension);
        locationList.add(location);
    }

    private Location createNewLocation(Location currentLocation, LocationList locationList) {
        Integer districtCodeFor = locationList.getDistrictCodeFor(currentLocation);
        Integer blockCodeFor = locationList.getBlockCodeFor(currentLocation);
        Integer panchayatCodeFor = locationList.getPanchayatCodeFor(currentLocation);
        Location locationToSave = new Location(currentLocation.getDistrict(), currentLocation.getBlock(), currentLocation.getPanchayat(), districtCodeFor, blockCodeFor, panchayatCodeFor, null);
        return locationToSave;
    }
}
