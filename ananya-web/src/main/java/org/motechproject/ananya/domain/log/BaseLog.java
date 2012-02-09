package org.motechproject.ananya.domain.log;


import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

public class BaseLog extends MotechBaseDataObject {
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

    public BaseLog() {
    }

    public BaseLog(String callerId, String calledNumber, DateTime startTime, DateTime endTime, String operator) {
        this.callerId = callerId;
        this.calledNumber = calledNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.operator = operator;
    }
}
