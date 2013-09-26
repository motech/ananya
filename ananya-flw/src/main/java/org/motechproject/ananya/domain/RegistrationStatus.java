package org.motechproject.ananya.domain;

public enum RegistrationStatus {
    REGISTERED(30),
    PARTIALLY_REGISTERED(20),
    UNREGISTERED(10),
    NEW_USER(0);

    public final int weight;

    RegistrationStatus(int weight) {
        this.weight = weight;
    }

    public boolean isRegistered() {
        return this.equals(REGISTERED);
    }
}
