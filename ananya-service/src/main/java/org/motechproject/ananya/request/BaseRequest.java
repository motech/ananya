package org.motechproject.ananya.request;

import org.motechproject.ananya.domain.ServiceType;

public abstract class BaseRequest {
    protected String callId;
    protected String callerId;
    protected String calledNumber;
    protected String token;

    protected BaseRequest() {
    }

    public BaseRequest(String callId, String callerId, String calledNumber) {
        this.callId = callId;
        this.callerId = callerId;
        this.calledNumber = calledNumber;
    }

    public BaseRequest(String callId, String callerId) {
        this(callId, callerId, null);
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

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }

    public ServiceType getType() {
        return null;
    }

}
