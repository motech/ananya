package org.motechproject.ananya.domain;

public class CallDuration {

    private long time;
    private CallEvent callEvent;

    public CallDuration() {
    }

    public CallDuration(CallEvent callEvent, long time) {
        this.time = time;
        this.callEvent = callEvent;
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

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (time ^ (time >>> 32));
        result = 31 * result + (callEvent != null ? callEvent.hashCode() : 0);
        return result;
    }

    public boolean isDisconnect() {
        return callEvent == CallEvent.DISCONNECT;
    }
}
