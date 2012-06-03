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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@CSVImporter(entity = "frontLineWorkerImporter", bean = FrontLineWorkerRequest.class)
@Component
public class FrontLineWorkerImporter {

    private RegistrationService registrationService;
    private LocationService locationService;

    @Autowired
    public FrontLineWorkerImporter(RegistrationService registrationService, LocationService locationService) {
        this.registrationService = registrationService;
        this.locationService = locationService;
    }

    @Validate
    public ValidationResponse validate(List<Object> objects) {
        boolean isValid = true;
        List<Location> locations = locationService.getAll();
        LocationList locationList = new LocationList(locations);
        List<Error> errors = new ArrayList<Error>();

        List<FrontLineWorkerRequest> frontLineWorkerRequests = convertToFLWRequest(objects);
        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();

        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            Location location = getLocationFor(frontLineWorkerRequest.getLocation(), locationList);
            FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validateWithBulkValidation(frontLineWorkerRequest, location, frontLineWorkerRequests);
            if (flwValidationResponse.isInValid()) {
                isValid = false;
                errors.add(new Error(frontLineWorkerRequest.toCSV() + "," + flwValidationResponse.getMessage()));
                continue;
            }
        }
        return constructValidationResponse(isValid, errors);
    }

    @Post
    public void postData(List<Object> objects) {
        List<FrontLineWorkerRequest> frontLineWorkerRequests = convertToFLWRequest(objects);
        registrationService.registerAllFLWs(frontLineWorkerRequests);
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
}
