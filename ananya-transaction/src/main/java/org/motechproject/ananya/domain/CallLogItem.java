package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

public class CallLogItem {

    @JsonProperty
    private CallFlowType callFlowType;

    @JsonProperty
    private DateTime startTime;

    @JsonProperty
    private DateTime endTime;

    public CallLogItem() {
    }

    public CallLogItem(CallFlowType callFlowType, DateTime startTime, DateTime endTime) {
        this.callFlowType = callFlowType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public CallFlowType getCallFlowType() {
        return callFlowType;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public Integer duration() {
        return Seconds.secondsBetween(startTime, endTime).getSeconds();
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public boolean hasNoTimeLimits() {
        return startTime == null || endTime == null;
    }

    public Integer durationInMilliSec() {
        return duration() * 1000;
    }
}
