package org.motechproject.ananya.importer.csv.importer;

import org.motechproject.ananya.domain.Location;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ValidationResponse validate(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        boolean isValid = true;
        int recordCounter = 0;
        List<Error> errors = new ArrayList<Error>();

        FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();
        logger.info("Started validating FLW csv records");
        addHeader(errors);
        Map<String, Integer> msisdnOccurrenceMap = getMsisdnOccurrenceMap(frontLineWorkerRequests);

        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            LocationRequest locationRequest = frontLineWorkerRequest.getLocation();
            Location location = locationService.findFor(locationRequest.getDistrict(), locationRequest.getBlock(), locationRequest.getPanchayat());

            FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validateWithBulkValidation(frontLineWorkerRequest, location, msisdnOccurrenceMap);

            if (flwValidationResponse.isInValid()) {
                isValid = false;
            }
            logger.info("Validated FLW record number : " + recordCounter++ + " with validation status : " + isValid);
            errors.add(new Error(frontLineWorkerRequest.toCSV() + ",\"" + flwValidationResponse.getMessage() + "\""));
        }
        logger.info("Completed validating FLW csv records");
        return constructValidationResponse(isValid, errors);
    }

    private Map<String, Integer> getMsisdnOccurrenceMap(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        Map<String, Integer> numberOfOccurrencesOfMsisdn = new HashMap<String, Integer>();
        for (FrontLineWorkerRequest frontLineWorkerRequest : frontLineWorkerRequests) {
            Integer count = numberOfOccurrencesOfMsisdn.containsKey(frontLineWorkerRequest.getMsisdn()) ?
                    numberOfOccurrencesOfMsisdn.get(frontLineWorkerRequest.getMsisdn()) + 1 : 1;
            numberOfOccurrencesOfMsisdn.put(frontLineWorkerRequest.getMsisdn(), count);
        }
        return numberOfOccurrencesOfMsisdn;
    }

    @Post
    public void postData(List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        logger.info("Started posting FLW data");
        registrationService.registerAllFLWs(frontLineWorkerRequests);
        logger.info("Finished posting FLW data");
    }

    private ValidationResponse constructValidationResponse(boolean isValid, List<Error> errors) {
        ValidationResponse validationResponse = new ValidationResponse(isValid);
        for (Error error : errors)
            validationResponse.addError(error);
        return validationResponse;
    }

    private boolean addHeader(List<Error> errors) {
        return errors.add(new Error("msisdn,name,designation,district,block,panchayat,error"));
    }
}
