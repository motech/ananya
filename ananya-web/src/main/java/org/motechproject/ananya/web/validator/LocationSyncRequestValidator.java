package org.motechproject.ananya.web.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.LocationStatus;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.request.LocationSyncRequest;

public class LocationSyncRequestValidator {
    public static Errors validate(LocationSyncRequest locationSyncRequest) {
        Errors errors = new Errors();
        checkForBlankFields(locationSyncRequest.getExistingLocation(), errors);
        checkForBlankFields(locationSyncRequest.getNewLocation(), errors);
        if(!LocationStatus.isValid(locationSyncRequest.getLocationStatus()))
            errors.add("Location Status is invalid");
        if(locationSyncRequest.getLastModifiedTime() == null)
            errors.add("Last Modified Time is blank");
        return errors;
    }

    private static void checkForBlankFields(LocationRequest locationRequest, Errors errors) {
        if(locationRequest == null
                ||StringUtils.isBlank(locationRequest.getDistrict())
                || StringUtils.isBlank(locationRequest.getBlock())
                || StringUtils.isBlank(locationRequest.getPanchayat()))
            errors.add("LocationRequest is null or has blank fields");
    }
}
