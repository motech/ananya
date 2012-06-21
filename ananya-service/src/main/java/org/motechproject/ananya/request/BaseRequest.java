package org.motechproject.ananya.request;

public abstract class BaseRequest {
    protected String callId;
    protected String callerId;
    protected String calledNumber;
    protected String token;

    protected BaseRequest(){
    }

    public BaseRequest(String callId, String callerId, String calledNumber) {
        this.callId = callId;
        this.callerId = callerId;
        this.calledNumber = calledNumber;
    }

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

}
