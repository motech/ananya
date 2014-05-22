package org.motechproject.ananya.response;

public class FrontLineWorkerResponse {
    private String msisdn;
    private String name;
    private String status;
    private String designation;
    private String operator;
    private String circle;
    private String verificationStatus;

    public FrontLineWorkerResponse(String msisdn, String name, String status, String designation, String operator, String circle, String verificationStatus) {
        this.msisdn = msisdn;
        this.name = name;
        this.status = status;
        this.designation = designation;
        this.operator = operator;
        this.circle = circle;
        this.verificationStatus = verificationStatus;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getDesignation() {
        return designation;
    }

    public String getOperator() {
        return operator;
    }

    public String getCircle() {
        return circle;
    }

    public String   getVerificationStatus() {
        return verificationStatus;
    }
}
