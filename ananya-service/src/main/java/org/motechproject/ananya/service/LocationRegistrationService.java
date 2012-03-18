package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.response.LocationRegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationRegistrationService {

    private LocationService locationService;
    private LocationDimensionService locationDimensionService;

    @Autowired
    public LocationRegistrationService(LocationDimensionService locationDimensionService, LocationService locationService) {
        this.locationService = locationService;
        this.locationDimensionService = locationDimensionService;
    }

    public LocationRegistrationResponse registerLocation(String district, String block, String panchayat) {
        LocationRegistrationResponse response = new LocationRegistrationResponse();
        Location location = new Location(district, block, panchayat, 0, 0, 0);

        if (location.isMissingDetails())
            return response.withIncompleteDetails();

        LocationList locations = new LocationList(locationService.getAll());
        if (locations.isAlreadyPresent(location))
            return response.withAlreadyPresent();

        saveNewLocation(location, locations);

        return response.withSuccessfulRegistration();
    }

    public void loadDefaultLocation() {
        int defaultCode = 0;
        Location location = new Location(FrontLineWorker.DEFAULT_LOCATION, FrontLineWorker.DEFAULT_LOCATION, FrontLineWorker.DEFAULT_LOCATION, defaultCode, defaultCode, defaultCode);
        LocationDimension locationDimension = new LocationDimension(FrontLineWorker.DEFAULT_LOCATION);
        locationService.add(location);
        locationDimensionService.add(locationDimension);
    }

    private void saveNewLocation(Location currentLocation, LocationList locations) {
        Location location = createNewLocation(currentLocation, locations);
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat());

        locationService.add(location);
        locationDimensionService.add(locationDimension);
        locations.add(location);
    }

    private Location createNewLocation(Location currentLocation, LocationList locations) {
        Integer districtCodeFor = locations.getDistrictCodeFor(currentLocation);
        Integer blockCodeFor = locations.getBlockCodeFor(currentLocation);
        Integer panchayatCodeFor = locations.getPanchayatCodeFor(currentLocation);
        Location locationToSave = new Location(currentLocation.getDistrict(), currentLocation.getBlock(), currentLocation.getPanchayat(), districtCodeFor, blockCodeFor, panchayatCodeFor);
        return locationToSave;
    }
}
