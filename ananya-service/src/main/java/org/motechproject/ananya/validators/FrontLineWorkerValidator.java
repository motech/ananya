package org.motechproject.ananya.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.response.ValidationResponse;

public class FrontLineWorkerValidator {

    public ValidationResponse validate(FrontLineWorker frontLineWorker, Location locationOfFrontLineWorker) {
        ValidationResponse validationResponse = new ValidationResponse();
        if (StringUtils.length(frontLineWorker.getMsisdn()) < 10 || !StringUtils.isNumeric(frontLineWorker.getMsisdn()))
            return validationResponse.forInvalidMsisdn();
        if (frontLineWorker != null && !StringUtils.isAlphanumericSpace(frontLineWorker.getName()))
            return validationResponse.forInvalidName();
        if (locationOfFrontLineWorker == null)
            return validationResponse.forInvalidLocation();

        return validationResponse;
    }
}
