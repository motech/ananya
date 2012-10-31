package org.motechproject.ananya.domain;

public enum LocationStatus {
    VALID,
    INVALID,
    NOT_VERIFIED;

    public static LocationStatus getFor(String status) {
        for(LocationStatus locationStatus : values()) {
            if(locationStatus.name().equalsIgnoreCase(status.trim()))
                return locationStatus;
        }
        return null;
    }
}
