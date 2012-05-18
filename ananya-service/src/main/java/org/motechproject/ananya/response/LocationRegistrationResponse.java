package org.motechproject.ananya.response;

public class LocationRegistrationResponse {

    private String message;
    private String locationDetails;

    public LocationRegistrationResponse withIncompleteDetails() {
        message = "One or more of District, Block details are missing";
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

    public void setLocationDetails(String district, String block, String panchayat) {
        locationDetails = district+","+block+","+panchayat;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public String getMessage() {
        return message;
    }
}
