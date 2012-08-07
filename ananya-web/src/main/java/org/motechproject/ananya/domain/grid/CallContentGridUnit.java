package org.motechproject.ananya.domain.grid;

public class CallContentGridUnit {
    private String name;
    private String msisdn;
    private String callId;
    private String timeStamp;
    private String contentName;
    private String contentFileName;

    public CallContentGridUnit(String name, String msisdn, String callId, String timeStamp, String contentName, String contentFileName) {
        this.name = name;
        this.msisdn = msisdn;
        this.callId = callId;
        this.timeStamp = timeStamp;
        this.contentName = contentName;
        this.contentFileName = contentFileName;
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getContentName() {
        return contentName;
    }

    public String getContentFileName() {
        return contentFileName;
    }
}
