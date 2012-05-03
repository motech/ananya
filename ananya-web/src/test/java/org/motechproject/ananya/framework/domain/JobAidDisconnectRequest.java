package org.motechproject.ananya.framework.domain;

public class JobAidDisconnectRequest {
    private String callerId;
    private String operator;
    private String jsonData;
    private String callId;
    private String calledNumber;

    public JobAidDisconnectRequest(String callerId, String operator, String callId, String calledNumber) {
        this.callerId = callerId;
        this.operator = operator;
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

    public String getCalledNumber() {
        return calledNumber;
    }

    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }
}
