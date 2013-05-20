package org.motechproject.ananya.response;

public class LocationValidationResponse {
    private String message = "";
    private boolean isValid = true;

    public boolean isValid() {
        return isValid;
    }

    public boolean isInValid() {
        return !isValid;
    }

    public String getMessage() {
        return message;
    }

    public LocationValidationResponse withIncompleteDetails() {
        isValid = false;
        message = "[One or more of State, District, Block, Panchayat details are missing]";
        return this;
    }

    public LocationValidationResponse withAlreadyPresent() {
        isValid = false;
        message = "[The location is already present]";
        return this;
    }
}
