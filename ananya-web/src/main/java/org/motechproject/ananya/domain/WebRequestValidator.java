package org.motechproject.ananya.domain;

import org.motechproject.ananya.ValidationResponse;
import org.motechproject.ananya.utils.ValidationUtils;

public class WebRequestValidator {

    public static ValidationResponse validate(String flwGuid, String channel) {
        ValidationResponse validationResponse = new ValidationResponse();
        validateChannel(channel, validationResponse);
        validateFlwGuid(flwGuid, validationResponse);
        return validationResponse;
    }

    private static void validateChannel(String channel, ValidationResponse validationResponse) {
        if (Channel.isInvalid(channel))
            validationResponse.addError(String.format("Invalid channel: %s", channel));
    }

    private static void validateFlwGuid(String flwGuid, ValidationResponse validationResponse) {
        if (!ValidationUtils.isValidUUID(flwGuid))
            validationResponse.addError(String.format("Invalid flwGuid: %s", flwGuid));
    }
}
