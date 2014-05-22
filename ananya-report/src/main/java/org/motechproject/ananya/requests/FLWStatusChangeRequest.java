package org.motechproject.ananya.requests;

public class FLWStatusChangeRequest {
    private Long msisdn;
    private String registrationStatus;

    public FLWStatusChangeRequest(Long msisdn, String registrationStatus) {
        this.msisdn = msisdn;
        this.registrationStatus = registrationStatus;
    }

    public Long getMsisdn() {
        return msisdn;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }
}
