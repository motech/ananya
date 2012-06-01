package org.motechproject.ananya.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.response.FLWValidationResponse;

public class FrontLineWorkerValidator {

    public FLWValidationResponse validate(FrontLineWorker frontLineWorker, Location locationOfFrontLineWorker) {
        FLWValidationResponse FLWValidationResponse = new FLWValidationResponse();
        if (StringUtils.length(frontLineWorker.getMsisdn()) < 10 || !StringUtils.isNumeric(frontLineWorker.getMsisdn()))
            return FLWValidationResponse.forInvalidMsisdn();
        if (frontLineWorker.name() != null && !StringUtils.isAlphanumericSpace(frontLineWorker.getName()))
            return FLWValidationResponse.forInvalidName();
        if (locationOfFrontLineWorker == null)
            return FLWValidationResponse.forInvalidLocation();

        return FLWValidationResponse;
    }
}
