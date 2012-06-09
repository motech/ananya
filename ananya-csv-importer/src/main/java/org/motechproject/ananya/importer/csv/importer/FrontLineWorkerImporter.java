package org.motechproject.ananya.importer.csv.importer;

import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.LocationList;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.FLWValidationResponse;
import org.motechproject.ananya.service.LocationService;
import org.motechproject.ananya.service.RegistrationService;
import org.motechproject.ananya.validators.FrontLineWorkerValidator;
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

@CSVImporter(entity = "FrontLineWorker", bean = FrontLineWorkerRequest.class)
@Component
public class FrontLineWorkerImporter {

    private RegistrationService registrationService;
    private LocationService locationService;
    private Logger logger = LoggerFactory.getLogger(FrontLineWorkerImporter.class);

    @Autowired
    public FrontLineWorkerImporter(RegistrationService registrationService, LocationService locationService) {
        this.registrationService = registrationService;
        this.locationService = locationService;
    }

    @Validate
    public ValidationResponse validate(List<Object> objects) {
        boolean isValid = true;
        int recordCounter = 0;
        List<Location> locations = locationService.getAll();
        LocationList locationList = new LocationList(locations);
        List<Error> errors = new ArrayList<Error>();

        List<FrontLineWorkerRequest> frontLineWorkerRequests = convertToFLWRequest(objects);
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();
        logger.info("Started validating FLW csv records");
        addHeader(errors);
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            Location location = getLocationFor(frontLineWorkerRequest.getLocation(), locationList);
            FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validateWithBulkValidation(frontLineWorkerRequest, location, frontLineWorkerRequests);
            if (flwValidationResponse.isInValid()) {
                isValid = false;
            }
            logger.info("Validated FLW record number : " + recordCounter++ + " with validation status : " + isValid);
            errors.add(new Error(frontLineWorkerRequest.toCSV() + ",\"" + flwValidationResponse.getMessage() + "\""));
        }
        logger.info("Completed validating FLW csv records");
        return constructValidationResponse(isValid, errors);
    }

    @Post
    public void postData(List<Object> objects) {
        logger.info("Started posting FLW data");
        List<FrontLineWorkerRequest> frontLineWorkerRequests = convertToFLWRequest(objects);
        registrationService.registerAllFLWs(frontLineWorkerRequests);
        logger.info("Finished posting FLW data");
    }

    private ValidationResponse constructValidationResponse(boolean isValid, List<Error> errors) {
        ValidationResponse validationResponse = new ValidationResponse(isValid);
        for (Error error : errors)
            validationResponse.addError(error);
        return validationResponse;
    }

    private Location getLocationFor(LocationRequest locationRequest, LocationList locationList) {
        return locationList.findFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());
    }

    private List<FrontLineWorkerRequest> convertToFLWRequest(List<Object> objects) {
        List<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        for (Object object : objects) {
            frontLineWorkerRequests.add((FrontLineWorkerRequest) object);
        }
        return frontLineWorkerRequests;
    }

    private boolean addHeader(List<Error> errors) {
        return errors.add(new Error("msisdn,name,designation,district,block,panchayat,error"));
    }
}
