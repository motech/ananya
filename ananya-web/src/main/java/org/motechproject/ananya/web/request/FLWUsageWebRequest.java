package org.motechproject.ananya.web.request;

import org.motechproject.ananya.domain.WebRequestValidator;
import org.motechproject.ananya.response.ValidationResponse;

public class FLWUsageWebRequest {
    private final String msisdn;
    private final String channel;

    public FLWUsageWebRequest(String msisdn, String channel) {
        this.msisdn = msisdn;
        this.channel = channel;
    }

    public ValidationResponse validate() {
        ValidationResponse validationResponse = new ValidationResponse();
        WebRequestValidator webRequestValidator = new WebRequestValidator();
        webRequestValidator.validateChannel(channel, validationResponse);
        webRequestValidator.validateMsisdn(msisdn, validationResponse);
        return validationResponse;
    }
}
