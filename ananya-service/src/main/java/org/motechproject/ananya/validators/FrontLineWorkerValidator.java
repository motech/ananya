package org.motechproject.ananya.validators;

import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.response.FLWValidationResponse;
import org.motechproject.ananya.utils.ValidationUtils;

import java.util.Map;

public class FrontLineWorkerValidator {

    public FLWValidationResponse validate(FrontLineWorker frontLineWorker, Location locationOfFrontLineWorker) {
        FLWValidationResponse flwValidationResponse = new FLWValidationResponse();
        validateMsisdn(frontLineWorker, flwValidationResponse);
        validateName(frontLineWorker, flwValidationResponse);
        validateLocation(locationOfFrontLineWorker, flwValidationResponse);
        validateFLWGuid(frontLineWorker, flwValidationResponse);
        return flwValidationResponse;
    }

    private void validateLocation(Location locationOfFrontLineWorker, FLWValidationResponse flwValidationResponse) {
        if (locationOfFrontLineWorker == null)
            flwValidationResponse.forInvalidLocation();
    }

    private void validateFLWGuid(FrontLineWorker frontLineWorker, FLWValidationResponse flwValidationResponse) {
        if (frontLineWorker.getFlwGuid() == null || !ValidationUtils.isValidUUID(frontLineWorker.getFlwGuid().toString()))
            flwValidationResponse.forInvalidFlwGuid();
    }

    private void validateName(FrontLineWorker frontLineWorker, FLWValidationResponse flwValidationResponse) {
        if (frontLineWorker.isInvalidName())
            flwValidationResponse.forInvalidName();
    }

    private void validateMsisdn(FrontLineWorker frontLineWorker, FLWValidationResponse flwValidationResponse) {
        if(frontLineWorker.isInvalidMsisdn())
            flwValidationResponse.forInvalidMsisdn();
    }

    public FLWValidationResponse validateWithBulkValidation(FrontLineWorkerRequest frontLineWorkerRequest,
                                                            Location location,
                                                            Map<String, Integer> msisdnOccurrenceMap) {
        FLWValidationResponse validationResponse = validate(FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest), location);
        if (msisdnOccurrenceMap.get(frontLineWorkerRequest.getMsisdn()) != 1)
            validationResponse.forDuplicates();
        return validationResponse;
    }
}
