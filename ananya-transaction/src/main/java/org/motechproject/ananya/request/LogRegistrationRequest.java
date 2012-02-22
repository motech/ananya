package org.motechproject.ananya.request;

import java.util.HashMap;
import java.util.Map;

public class LogRegistrationRequest {
    private String callerId;
    private String calledNumber;
    private String designation;
    private String panchayat;
    private String operator;

    public static final String LOG_TYPE = "RegistrationLog";

    public LogRegistrationRequest(String callerId, String calledNumber, String designation, String panchayat, String operator) {
        this.callerId = callerId;
        this.calledNumber = calledNumber;
        this.designation = designation;
        this.panchayat = panchayat;
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public String callerId() {
        return this.callerId;
    }

    public String calledNumber() {
        return this.calledNumber;
    }

    public String designation() {
        return this.designation;
    }

    public String panchayat() {
        return this.panchayat;
    }

}
