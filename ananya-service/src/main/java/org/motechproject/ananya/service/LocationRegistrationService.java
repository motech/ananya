package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.response.LocationRegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public LocationRegistrationResponse registerLocation(String district, String block, String panchayat, LocationList locationList) {
        LocationRegistrationResponse response = new LocationRegistrationResponse();
        Location location = new Location(district, block, panchayat, 0, 0, 0);

        if (location.isMissingDetails())
            return response.withIncompleteDetails();

        if (locationList.isAlreadyPresent(location))
            return response.withAlreadyPresent();

        saveNewLocation(location, locationList);

        return response.withSuccessfulRegistration();
    }

    public void loadDefaultLocation() {
        int defaultCode = 0;
        String emptyPanchayat = "";
        Location location = new Location(FrontLineWorker.DEFAULT_LOCATION, FrontLineWorker.DEFAULT_LOCATION, emptyPanchayat, defaultCode, defaultCode, defaultCode);
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(),
                FrontLineWorker.DEFAULT_LOCATION, FrontLineWorker.DEFAULT_LOCATION, emptyPanchayat);
        locationService.add(location);
        locationDimensionService.add(locationDimension);
    }

    public void registerDefaultLocationForDistrictBlock(LocationList locationList) {
        List<Location> uniqueDistrictBlockLocations = locationList.getUniqueDistrictBlockLocations();
        for (Location location : uniqueDistrictBlockLocations) {
            LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat());
            locationService.add(location);
            locationDimensionService.add(locationDimension);
        }
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
