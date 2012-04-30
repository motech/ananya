package org.motechproject.ananya.domain;

public enum RegistrationStatus {
    REGISTERED,
    PARTIALLY_REGISTERED,
    UNREGISTERED;

    public boolean isRegistered() {
        return this.equals(REGISTERED);
    }
}
