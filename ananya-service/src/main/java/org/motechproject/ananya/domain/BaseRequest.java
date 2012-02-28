package org.motechproject.ananya.domain;

public abstract class BaseRequest {
    protected String callId;
    protected String callerId;
    protected String calledNumber;
    protected String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getCallerId() {
        return callerId;
    }

    public String getCalledNumber() {
        return calledNumber;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }

    protected BaseRequest(){
    }

    protected BaseRequest(String callerId, String calledNumber) {
        this.callerId = callerId;
        this.calledNumber = calledNumber;
    }
}
