package org.motechproject.ananya.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;

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


    public String getCallerId() {
        return this.callerId;
    }

    public String getOperator() {
        return operator;
    }

    public DateTime getStartTime() {
        return DateUtil.setTimeZone(this.startTime);
    }

    public DateTime getEndTime() {
        return DateUtil.setTimeZone(this.endTime);
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

}
