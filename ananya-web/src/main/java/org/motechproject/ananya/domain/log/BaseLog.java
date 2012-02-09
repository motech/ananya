package org.motechproject.ananya.domain.log;


import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

public class BaseLog extends MotechBaseDataObject {
    @JsonProperty
    private String callId;
    @JsonProperty
    private String callerId;
    @JsonProperty
    private String calledNumber;
    @JsonProperty
    private DateTime startTime;
    @JsonProperty
    private DateTime endTime;
    @JsonProperty
    private String operator;
    @JsonProperty
    private String token;

    public BaseLog() {
    }

    public BaseLog(String callerId, String calledNumber, DateTime startTime, DateTime endTime, String operator, String token, String callId) {
        this.callerId = callerId;
        this.calledNumber = calledNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.operator = operator;
        this.token = token;
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }

    public String getCallerId() {
        return callerId;
    }

    public String getCalledNumber() {
        return calledNumber;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getOperator() {
        return operator;
    }

    public String getToken() {
        return token;
    }
}
