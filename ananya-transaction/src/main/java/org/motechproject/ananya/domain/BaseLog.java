package org.motechproject.ananya.domain;


import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.model.MotechBaseDataObject;

public class BaseLog extends MotechBaseDataObject {
    @JsonProperty
    protected String callId;
    @JsonProperty
    protected String callerId;
    @JsonProperty
    protected String calledNumber;
    @JsonProperty
    protected String operator;

    public BaseLog() {
    }

    public BaseLog(String callId, String callerId) {
        this.callId = callId;
        this.callerId = callerId;
    }

    public BaseLog(String callerId, String calledNumber, String operator, String callId) {
        this.callerId = callerId;
        this.calledNumber = calledNumber;
        this.operator = operator;
        this.callId = callId;
    }

    public String getCallId() {
        return callId;
    }

    public String getCallerId() {
        return this.callerId;
    }

    public String getOperator() {
        return operator;
    }

    public Long callerIdAsLong() {
        return Long.valueOf(callerId);
    }

    public String getCalledNumber() {
        return calledNumber;
    }
}
