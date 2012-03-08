package org.motechproject.ananya.request;

public class RegistrationRequest extends BaseRequest {
    private String designation;
    private String panchayat;
    private String operator;

    public static final String LOG_TYPE = "RegistrationLog";

    public RegistrationRequest(String callerId, String calledNumber, String designation, String panchayat, String operator) {
        super(callerId, calledNumber);
        this.designation = designation;
        this.panchayat = panchayat;
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public String designation() {
        return this.designation;
    }

    public String panchayat() {
        return this.panchayat;
    }

}
