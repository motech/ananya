package org.motechproject.ananya.domain;

public enum RegistrationStatus {
    REGISTERED,
    UNREGISTERED,
    PENDING_REGISTRATION;

    public boolean isRegistered() {
        return this.equals(REGISTERED) || this.equals(PENDING_REGISTRATION);
    }
}
