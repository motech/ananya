package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;

public enum LocationStatus {
    VALID,
    INVALID,
    IN_REVIEW,
    NOT_VERIFIED;

    public static LocationStatus getFor(String status) {
        for(LocationStatus locationStatus : values()) {
            if(locationStatus.name().equalsIgnoreCase(StringUtils.trimToEmpty(status)))
                return locationStatus;
        }
        return null;
    }

    public static boolean isValid(String status) {
        return StringUtils.isNotBlank(status) && getFor(status) != null;
    }
}
