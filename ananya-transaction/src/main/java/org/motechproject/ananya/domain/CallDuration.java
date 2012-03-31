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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallDuration that = (CallDuration) o;

        if (time != that.time) return false;
        if (callEvent != that.callEvent) return false;
        if (callId != null ? !callId.equals(that.callId) : that.callId != null) return false;
        if (callerId != null ? !callerId.equals(that.callerId) : that.callerId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = callId != null ? callId.hashCode() : 0;
        result = 31 * result + (callerId != null ? callerId.hashCode() : 0);
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + (callEvent != null ? callEvent.hashCode() : 0);
        return result;
    }

    public boolean isDisconnect() {
        return callEvent == CallEvent.DISCONNECT;
    }
}
