package org.motechproject.ananya.validators;

import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.response.LocationValidationResponse;

public class LocationValidator {

    private LocationList locationList;

    public LocationValidator(LocationList locationList) {
        this.locationList = locationList;
    }

    public LocationValidationResponse validate(Location location) {
        LocationValidationResponse locationValidationResponse = new LocationValidationResponse();

        if(location.isMissingDetails())
            return locationValidationResponse.withIncompleteDetails();
        if(locationList.isAlreadyPresent(location))
            return locationValidationResponse.withAlreadyPresent();

        return locationValidationResponse;
    }
}
