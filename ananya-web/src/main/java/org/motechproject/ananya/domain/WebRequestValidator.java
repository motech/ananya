package org.motechproject.ananya.domain;

import org.motechproject.ananya.ValidationResponse;

public class WebRequestValidator {

    public ValidationResponse validateChannel(String channel) {
        ValidationResponse validationResponse = new ValidationResponse();
        if (Channel.isInvalid(channel)) {
            validationResponse.addError(String.format("Invalid channel: %s", channel));
        }
        return validationResponse;
    }
}
