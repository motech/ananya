package org.motechproject.ananya.support.admin.domain;

public class CallDetail {
    private String callId;
    private String startTime;
    private String endTime;
    private String duration;
    private String calledNumber;
    private String type;

    public CallDetail(String callId, String startTime, String endTime, String duration, String calledNumber, String type) {
        this.callId = callId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.calledNumber = calledNumber;
        this.type = type;
    }

    public String getCallId() {
        return callId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDuration() {
        return duration;
    }

    public String getCalledNumber() {
        return calledNumber;
    }

    public String getType() {
        return type;
    }
}