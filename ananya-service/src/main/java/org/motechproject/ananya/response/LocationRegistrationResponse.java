package org.motechproject.ananya.response;

public class LocationRegistrationResponse {

    private String message;
    private String locationDetails;

    public LocationRegistrationResponse(String district, String block, String panchayat) {
        locationDetails = district + "," + block + "," + panchayat;
    }

    public LocationRegistrationResponse withSuccessfulRegistration() {
        message = "Successfully registered location";
        return this;
    }

    public LocationRegistrationResponse withValidationResponse(LocationValidationResponse validationResponse) {
        message = validationResponse.getMessage();
        return this;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "{" +
                "message='" + message + '\'' +
                ", locationDetails='" + locationDetails + '\'' +
                '}';
    }
}
