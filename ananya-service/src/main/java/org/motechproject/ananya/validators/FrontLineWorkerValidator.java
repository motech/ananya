package org.motechproject.ananya.validators;

import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.response.FLWValidationResponse;
import org.motechproject.ananya.utils.ValidationUtils;

import java.util.Map;

public class FrontLineWorkerValidator {

    public static FLWValidationResponse validate(FrontLineWorkerRequest frontLineWorkerRequest, Location locationOfFrontLineWorker) {
        FLWValidationResponse flwValidationResponse = new FLWValidationResponse();
        validateMsisdn(frontLineWorkerRequest, flwValidationResponse);
        validateName(frontLineWorkerRequest, flwValidationResponse);
        validateLocation(locationOfFrontLineWorker, flwValidationResponse);
        validateFLWId(frontLineWorkerRequest, flwValidationResponse);
        return flwValidationResponse;
    }

    private static void validateLocation(Location locationOfFrontLineWorker, FLWValidationResponse flwValidationResponse) {
        if (locationOfFrontLineWorker == null)
            flwValidationResponse.forInvalidLocation();
    }

    private static void validateFLWId(FrontLineWorkerRequest frontLineWorker, FLWValidationResponse flwValidationResponse) {
        if (frontLineWorker.getFlwId() == null || !ValidationUtils.isValidUUID(frontLineWorker.getFlwId()))
            flwValidationResponse.forInvalidFlwId();
    }

    private static void validateName(FrontLineWorkerRequest frontLineWorkerRequest, FLWValidationResponse flwValidationResponse) {
        if (frontLineWorkerRequest.isInvalidName())
            flwValidationResponse.forInvalidName();
    }

    private static void validateMsisdn(FrontLineWorkerRequest frontLineWorkerRequest, FLWValidationResponse flwValidationResponse) {
        if (frontLineWorkerRequest.isInvalidMsisdn())
            flwValidationResponse.forInvalidMsisdn();
    }

    public static FLWValidationResponse validateWithBulkValidation(FrontLineWorkerRequest frontLineWorkerRequest,
                                                            Location location,
                                                            Map<String, Integer> msisdnOccurrenceMap) {
        FLWValidationResponse validationResponse = validate(frontLineWorkerRequest, location);
        if (msisdnOccurrenceMap.get(frontLineWorkerRequest.getMsisdn()) != 1)
            validationResponse.forDuplicates();
        return validationResponse;
    }
}