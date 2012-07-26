package org.motechproject.ananya.framework.domain;

public class CertificateCourseRequest {
    private String callerId;
    private String operator;
    private String jsonData;
    private String callId;
    private String circle;
    private String calledNumber;

    public CertificateCourseRequest(String callerId, String operator, String circle, String callId, String calledNumber) {
        this.callerId = callerId;
        this.operator = operator;
        this.circle = circle;
        this.callId = callId;
        this.calledNumber = calledNumber;
    }

    public String getCallerId() {
        return callerId;
    }

    public String getOperator() {
        return operator;
    }

    public void setJsonPostData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getCallId() {
        return callId;
    }

    public String getJsonPostData() {
        return this.jsonData;
    }

    public String getCircle() {
        return circle;
    }

    public String getCalledNumber() {
        return calledNumber;
    }
}
