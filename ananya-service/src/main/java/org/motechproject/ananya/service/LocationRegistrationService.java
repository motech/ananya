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
    private final LocationList locations;

    @Autowired
    public LocationRegistrationService(LocationDimensionService locationDimensionService, LocationService locationService) {
        this.locationService = locationService;
        this.locationDimensionService = locationDimensionService;
        locations = new LocationList(locationService.getAll());
    }

    public LocationRegistrationResponse registerLocation(String district, String block, String panchayat) {
        LocationRegistrationResponse response = new LocationRegistrationResponse();
        Location location = new Location(district, block, panchayat, 0, 0, 0);

        if (location.isMissingDetails())
            return response.withIncompleteDetails();

        if (locations.isAlreadyPresent(location))
            return response.withAlreadyPresent();

        saveNewLocation(location);

        return response.withSuccessfulRegistration();
    }

    public void loadDefaultLocation() {
        int defaultCode = 0;
        Location location = new Location(FrontLineWorker.DEFAULT_LOCATION, FrontLineWorker.DEFAULT_LOCATION, FrontLineWorker.DEFAULT_LOCATION, defaultCode, defaultCode, defaultCode);
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(),
                FrontLineWorker.DEFAULT_LOCATION,FrontLineWorker.DEFAULT_LOCATION,FrontLineWorker.DEFAULT_LOCATION);
        locationService.add(location);
        locationDimensionService.add(locationDimension);
    }

    private void saveNewLocation(Location currentLocation) {
        Location location = createNewLocation(currentLocation);
        LocationDimension locationDimension = new LocationDimension(location.getExternalId(), location.getDistrict(), location.getBlock(), location.getPanchayat());

        locationService.add(location);
        locationDimensionService.add(locationDimension);
        locations.add(location);
    }

    private Location createNewLocation(Location currentLocation) {
        Integer districtCodeFor = locations.getDistrictCodeFor(currentLocation);
        Integer blockCodeFor = locations.getBlockCodeFor(currentLocation);
        Integer panchayatCodeFor = locations.getPanchayatCodeFor(currentLocation);
        Location locationToSave = new Location(currentLocation.getDistrict(), currentLocation.getBlock(), currentLocation.getPanchayat(), districtCodeFor, blockCodeFor, panchayatCodeFor);
        return locationToSave;
    }
}
