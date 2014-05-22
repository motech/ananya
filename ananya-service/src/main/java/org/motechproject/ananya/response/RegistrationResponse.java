package org.motechproject.ananya.response;

public class RegistrationResponse {
    private String message;

    public RegistrationResponse() {
    }

    public RegistrationResponse withValidationResponse(FLWValidationResponse FLWValidationResponse) {
        this.message = FLWValidationResponse.getMessage();
        return this;
    }

    public RegistrationResponse withNewRegistrationDone() {
        this.message = "Created/Updated FLW record";
        return this;
    }

    public String getMessage() {
        return message;
    }
}
   