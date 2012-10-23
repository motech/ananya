package org.motechproject.ananya.response;

public class FLWCallDetail {
    private CallType callType;
    private String startTime;
    private String endTime;
    private Integer minutes;

    public FLWCallDetail(CallType callType, String startTime, String endTime, Integer minutes) {
        this.callType = callType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minutes = minutes;
    }

    public CallType getCallType() {
        return callType;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Integer getMinutes() {
        return minutes;
    }
}
