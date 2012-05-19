package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.mapper.LocationMapper;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.LocationRegistrationResponse;
import org.motechproject.ananya.response.LocationResponse;
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
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat());
        locationService.add(location);
        locationDimensionService.add(locationDimension);
    }

    public LocationRegistrationResponse addNewLocation(LocationRequest request) {
        LocationList locationList = new LocationList(locationService.getAll());
        return registerLocation(request.getDistrict(), request.getBlock(), request.getPanchayat(), locationList);
    }

    public List<LocationRegistrationResponse> registerAllLocations(List<Location> locationsToSave) {
        LocationList locationList = new LocationList(locationService.getAll());
        List<LocationRegistrationResponse> responses = new ArrayList<LocationRegistrationResponse>();
        for (Location location : locationsToSave) {
            LocationRegistrationResponse locationRegistrationResponse = registerLocation(location.getDistrict(), location.getBlock(), location.getPanchayat(), locationList);
            responses.add(locationRegistrationResponse);
        }
        registerDefaultLocationForDistrictBlock(locationList);
        return responses;
    }

    public List<LocationResponse> getFilteredLocations(LocationRequest request) {
        List<LocationDimension> filteredLocations = locationDimensionService.getFilteredLocations(request.getDistrict(), request.getBlock(), request.getPanchayat());
        List<LocationResponse> locationResponses = new ArrayList<LocationResponse>();
        for (LocationDimension locationDimension : filteredLocations) {
            locationResponses.add(LocationMapper.mapFrom(locationDimension));
        }
        return locationResponses;
    }

    private void registerDefaultLocationForDistrictBlock(LocationList locationList) {
        List<Location> uniqueDistrictBlockLocations = locationList.getUniqueDistrictBlockLocations();
        for (Location location : uniqueDistrictBlockLocations) {
            LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat());
            locationService.add(location);
            locationDimensionService.add(locationDimension);
        }
    }

    private LocationRegistrationResponse registerLocation(String district, String block, String panchayat, LocationList locationList) {
        LocationRegistrationResponse response = new LocationRegistrationResponse(district, block, panchayat);
        Location location = new Location(district, block, panchayat, 0, 0, 0);

        if (location.isMissingDetails())
            return response.withIncompleteDetails();

        if (locationList.isAlreadyPresent(location)) {
            return response.withAlreadyPresent();
        }

        saveNewLocation(location, locationList);

        return response.withSuccessfulRegistration();
    }

    private void saveNewLocation(Location currentLocation, LocationList locationList) {
        Location location = createNewLocation(currentLocation, locationList);
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat());

        locationService.add(location);
        locationDimensionService.add(locationDimension);
        locationList.add(location);
    }

    private Location createNewLocation(Location currentLocation, LocationList locationList) {
        Integer districtCodeFor = locationList.getDistrictCodeFor(currentLocation);
        Integer blockCodeFor = locationList.getBlockCodeFor(currentLocation);
        Integer panchayatCodeFor = locationList.getPanchayatCodeFor(currentLocation);
        Location locationToSave = new Location(currentLocation.getDistrict(), currentLocation.getBlock(), currentLocation.getPanchayat(), districtCodeFor, blockCodeFor, panchayatCodeFor);
        return locationToSave;
    }
}
