package org.motechproject.ananya.response;

import org.motechproject.ananya.domain.RegistrationStatus;


public class RegistrationResponse {

    private String message;
    private RegistrationStatus status;

    public RegistrationResponse withInvalidLocationStatus() {
        status = RegistrationStatus.NOT_REGISTERED;
        message = "Invalid Location";
        return this;
    }

    public RegistrationResponse withInvalidCallerId() {
        status = RegistrationStatus.NOT_REGISTERED;
        message = "Invalid CallerId";
        return this;
    }

    public RegistrationResponse withInvalidName() {
        status = RegistrationStatus.NOT_REGISTERED;
        message = "Invalid Name";
        return this;
    }

    public RegistrationResponse withLocationUpdated() {
        status = RegistrationStatus.REGISTERED;
        message = "Location details updated";
        return this;
    }

    public RegistrationResponse withNewRegistrationDone() {
        status = RegistrationStatus.REGISTERED;
        message = "New FrontlineWorker added";
        return this;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRegistered() {
        return status.isRegistered();
    }
}
   