package org.motechproject.ananya.response;

public class LocationRegistrationResponse {

    private String message;

    public LocationRegistrationResponse withIncompleteDetails() {
        message = "One or more of District, Block, Panchayat details are missing";
        return this;
    }

    public LocationRegistrationResponse withAlreadyPresent() {
        message = "The location is already present";
        return this;
    }

    public LocationRegistrationResponse withSuccessfulRegistration() {
        message = "Successfully registered location";
        return this;
    }

    public String getMessage() {
        return message;
    }
}
