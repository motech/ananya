package org.motechproject.ananya.framework.domain;

public class JobAidRequest {
    private String callerId;
    private String operator;

    public JobAidRequest(String callerId, String operator) {
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
