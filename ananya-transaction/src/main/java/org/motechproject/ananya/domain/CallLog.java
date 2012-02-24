package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;

@TypeDiscriminator("doc.type == 'CallLog'")
public class CallLog extends BaseLog{

    @JsonProperty
    private CallFlow callFlow;

    public CallLog() {
    }

    public CallLog(String callId, String callerId, CallFlow callFlow, DateTime startTime, DateTime endTime) {
        super(callerId, "", startTime, endTime, "", callId);
        this.callFlow = callFlow;
    }

    public CallFlow getCallFlow() {
        return callFlow;
    }
}
