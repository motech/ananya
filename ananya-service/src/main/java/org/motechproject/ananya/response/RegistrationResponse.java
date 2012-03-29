package org.motechproject.ananya.response;

public class RegistrationResponse {

    private String message;

    public RegistrationResponse withInvalidLocationStatus() {
        message = "Invalid Location";
        return this;
    }

    public RegistrationResponse withInvalidCallerId() {
        message = "Invalid CallerId";
        return this;
    }

    public RegistrationResponse withNewRegistrationDone() {
        message = "New FrontlineWorker added";
        return this;
    }

    public String getMessage() {
        return message;
    }
}
   