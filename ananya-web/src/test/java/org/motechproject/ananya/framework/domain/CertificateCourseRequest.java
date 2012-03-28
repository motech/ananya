package org.motechproject.ananya.framework.domain;

public class CertificateCourseRequest {
    private String callerId;
    private String operator;

    public CertificateCourseRequest(String callerId, String operator) {
        this.callerId = callerId;
        this.operator = operator;
    }

    public String getCallerId() {
        return callerId;
    }

    public String getOperator() {
        return operator;
    }
}
