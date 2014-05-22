package org.motechproject.ananya.validators;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidUUID(String uuid) {
        return Pattern.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", uuid);
    }

    public static boolean isValidCallId(String callId) {
        if (StringUtils.isEmpty(callId)) {
            return false;
        }

        String[] split = callId.split("-");
        if (split.length != 2) {
            return false;
        }

        if (StringUtils.isEmpty(split[0]) || !StringUtils.isNumeric(split[0]) || split[0].length() < 10) {
            return false;
        }

        if (StringUtils.isEmpty(split[1]) || !StringUtils.isNumeric(split[1])) {
            return false;
        }

        return true;
    }

    public static boolean isValidCallerId(String callerId) {
        return !StringUtils.isEmpty(callerId) && StringUtils.isNumeric(callerId) && callerId.length() >= 10 && callerId.length() <= 12;
    }

    public static boolean isValidCalledNumber(String calledNumber) {
        return !StringUtils.isEmpty(calledNumber) && StringUtils.isNumeric(calledNumber);
    }
}
