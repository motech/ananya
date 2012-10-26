package org.motechproject.ananya.domain;

import org.motechproject.ananya.ValidationResponse;
import org.motechproject.ananya.utils.ValidationUtils;

public class WebRequestValidator {

    public static ValidationResponse validate(String flwId, String channel) {
        ValidationResponse validationResponse = new ValidationResponse();
        validateChannel(channel, validationResponse);
        validateFlwId(flwId, validationResponse);
        return validationResponse;
    }

    private static void validateChannel(String channel, ValidationResponse validationResponse) {
        if (Channel.isInvalid(channel))
            validationResponse.addError(String.format("Invalid channel: %s", channel));
    }

    private static void validateFlwId(String flwId, ValidationResponse validationResponse) {
        if (!ValidationUtils.isValidUUID(flwId))
            validationResponse.addError(String.format("Invalid flwId: %s", flwId));
    }
}
