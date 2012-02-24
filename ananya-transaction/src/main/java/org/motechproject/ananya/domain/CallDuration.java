package org.motechproject.ananya.domain;

public class CallDuration {

    private String callId;
    private String callerId;
    private long time;
    private CallEvent callEvent;
    public CallDuration() {
    }

    public CallDuration(String callId, String callerId, CallEvent callEvent, long time) {
        this.callId = callId;
        this.callerId = callerId;
        this.time = time;
        this.callEvent = callEvent;
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

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public CallEvent getCallEvent() {
        return callEvent;
    }

    public void setCallEvent(CallEvent callEvent) {
        this.callEvent = callEvent;
    }
}
