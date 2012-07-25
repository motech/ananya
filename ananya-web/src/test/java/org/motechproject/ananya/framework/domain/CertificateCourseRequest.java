package org.motechproject.ananya.framework.domain;

public class CertificateCourseRequest {
    private String callerId;
    private String operator;
    private String jsonData;
    private String callId;
    private String circle;

    public CertificateCourseRequest(String callerId, String operator, String circle, String callId) {
        this.callerId = callerId;
        this.operator = operator;
        this.circle = circle;
        this.callId = callId;
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
}
