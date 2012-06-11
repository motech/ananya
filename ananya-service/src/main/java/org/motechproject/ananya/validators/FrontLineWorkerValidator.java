package org.motechproject.ananya.validators;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.mapper.FrontLineWorkerMapper;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.response.FLWValidationResponse;

import java.util.List;
import java.util.regex.Pattern;

public class FrontLineWorkerValidator {

    public FLWValidationResponse validate(FrontLineWorker frontLineWorker, Location locationOfFrontLineWorker) {
        FLWValidationResponse flwValidationResponse = new FLWValidationResponse();
        if (StringUtils.length(frontLineWorker.getMsisdn()) < 10 || !StringUtils.isNumeric(frontLineWorker.getMsisdn()))
            flwValidationResponse.forInvalidMsisdn();
        if (StringUtils.isNotBlank(frontLineWorker.name()) && !Pattern.matches("[a-zA-Z0-9\\s\\.]*", frontLineWorker.name()))
            flwValidationResponse.forInvalidName();
        if (locationOfFrontLineWorker == null)
            flwValidationResponse.forInvalidLocation();

        return flwValidationResponse;
    }

    public FLWValidationResponse validateWithBulkValidation(FrontLineWorkerRequest frontLineWorkerRequest,
                                                            Location location,
                                                            List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        FLWValidationResponse validationResponse = validate(FrontLineWorkerMapper.mapFrom(frontLineWorkerRequest), location);
        if(hasDuplicates(frontLineWorkerRequest, frontLineWorkerRequests))
            validationResponse.forDuplicates();
        return validationResponse;
    }

    private boolean hasDuplicates(FrontLineWorkerRequest frontLineWorkerRequest, List<FrontLineWorkerRequest> frontLineWorkerRequests) {
        int count = 0;
        for (FrontLineWorkerRequest flwRequest : frontLineWorkerRequests) {
            if (StringUtils.equals(StringUtils.trimToEmpty(frontLineWorkerRequest.getMsisdn()), StringUtils.trimToEmpty(flwRequest.getMsisdn())))
                count++;
            if(count == 2)
                return true;
        }
        return false;
    }
}
