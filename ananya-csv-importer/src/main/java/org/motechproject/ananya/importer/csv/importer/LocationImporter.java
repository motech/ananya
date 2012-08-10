package org.motechproject.ananya.importer.csv.importer;

import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.mapper.LocationMapper;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.LocationValidationResponse;
import org.motechproject.ananya.service.LocationRegistrationService;
import org.motechproject.ananya.service.LocationService;
import org.motechproject.ananya.validators.LocationValidator;
import org.motechproject.importer.annotation.CSVImporter;
import org.motechproject.importer.annotation.Post;
import org.motechproject.importer.annotation.Validate;
import org.motechproject.importer.domain.Error;
import org.motechproject.importer.domain.ValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@CSVImporter(entity = "Location", bean = LocationRequest.class)
@Component
public class LocationImporter {

    private LocationRegistrationService locationRegistrationService;
    private LocationService locationService;
    private Logger logger = LoggerFactory.getLogger(LocationImporter.class);

    @Autowired
    public LocationImporter(LocationRegistrationService locationRegistrationService, LocationService locationService) {
        this.locationRegistrationService = locationRegistrationService;
        this.locationService = locationService;
    }

    @Validate
    public ValidationResponse validate(List<Object> objects) {
        boolean isValid = true;
        int recordCounter = 0;
        List<Error> errors = new ArrayList<Error>();

        List<LocationRequest> locationRequests = convertToLocationRequest(objects);
        LocationValidator locationValidator = new LocationValidator(new LocationList(locationService.getAll()));

        addHeader(errors);
        logger.info("Started validating location csv records");
        for (LocationRequest locationRequest : locationRequests) {
            LocationValidationResponse locationValidationResponse = locationValidator.validate(LocationMapper.mapFrom(locationRequest));
            if (locationValidationResponse.isInValid()) {
                isValid = false;
            }
            logger.info("Validated location record number : " + recordCounter++ + " with validation status : " + isValid);
            errors.add(new Error(locationRequest.toCSV() + ",\"" + locationValidationResponse.getMessage() + "\""));
        }
        logger.info("Completed validating location csv records");
        return constructValidationResponse(isValid, errors);
    }

    @Post
    public void postData(List<Object> objects) {
        logger.info("Started posting location data");
        List<LocationRequest> locationRequests = convertToLocationRequest(objects);
        locationRegistrationService.registerAllLocations(locationRequests);
        logger.info("Finished posting location data");
    }

    private ValidationResponse constructValidationResponse(boolean isValid, List<Error> errors) {
        ValidationResponse validationResponse = new ValidationResponse(isValid);
        for (Error error : errors)
            validationResponse.addError(error);
        return validationResponse;
    }

    private List<LocationRequest> convertToLocationRequest(List<Object> objects) {
        List<LocationRequest> locationRequests = new ArrayList<LocationRequest>();
        for (Object object : objects) {
            locationRequests.add((LocationRequest) object);
        }
        return locationRequests;
    }

    private boolean addHeader(List<Error> errors) {
        return errors.add(new Error("district,block,panchayat,error"));
    }
}
