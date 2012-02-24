package org.motechproject.ananya.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

public class BaseLog extends MotechBaseDataObject {

    @JsonProperty
    private String callId;
    @JsonProperty
    protected String callerId;
    @JsonProperty
    protected String calledNumber;

    @JsonProperty
    protected DateTime startTime;

    @JsonProperty
    protected DateTime endTime;

    @JsonProperty
    protected String operator;

    public BaseLog() {
    }

    public BaseLog(String callerId, String calledNumber, DateTime startTime, DateTime endTime, String operator, String callId) {
        this.callerId = callerId;
        this.calledNumber = calledNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.operator = operator;
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }


    public String getCalledNumber() {
        return calledNumber;
    }


    public DateTime getEndTime() {
        return endTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public String getOperator() {

        return operator;
    }

    public String getCallerId() {
        return this.callerId;
    }

    public DateTime getStartTime() {
        return this.startTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }
}
