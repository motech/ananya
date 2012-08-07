package org.motechproject.ananya.domain.grid;

public class CallDetailGridUnit {
    private String name;
    private String msisdn;
    private String callId;
    private String startTime;
    private String endTime;
    private String duration;
    private String calledNumber;
    private String type;

    public CallDetailGridUnit(String name, String msisdn, String callId, String startTime, String endTime, String duration, String calledNumber, String type) {
        this.name = name;
        this.msisdn = msisdn;
        this.callId = callId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.calledNumber = calledNumber;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getMsisdn() {
        return msisdn;
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