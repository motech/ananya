package org.motechproject.ananya.domain;

public enum RegistrationStatus {
    REGISTERED,
    PARTIALLY_REGISTERED;

    public boolean isRegistered() {
        return this.equals(REGISTERED);
    }
}
