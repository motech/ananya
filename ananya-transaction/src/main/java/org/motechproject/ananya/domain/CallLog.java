package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

@TypeDiscriminator("doc.type == 'CallLog'")
public class CallLog extends BaseLog{

    @JsonProperty
    private CallFlowType callFlowType;

    public CallLog() {
    }

    public CallLog(String callId, String callerId, CallFlowType callFlowType, DateTime startTime, DateTime endTime) {
        super(callerId, "", startTime, endTime, "", callId);
        this.callFlowType = callFlowType;
    }

    public CallFlowType getCallFlowType() {
        return callFlowType;
    }

    public Integer duration(){
        return Seconds.secondsBetween(getStartTime(), getEndTime()).getSeconds();
    }

}
