package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'CallDetailLog'")
public class CallDetailLog extends MotechBaseDataObject{

    @JsonProperty
    private String callId;

    @JsonProperty
    private String callerId;

    @JsonProperty
    private DateTime time;

    @JsonProperty
    private String operator;

    @JsonProperty
    private CallEvent callEvent;

    public CallDetailLog() {
    }

    public CallDetailLog(String callId, String callerId, CallEvent callEvent, DateTime time, String operator) {
        this.callId = callId;
        this.callerId = callerId;
        this.time = time;
        this.operator = operator;
        this.callEvent = callEvent;
    }

    public DateTime getTime() {
        return time;
    }

    public CallEvent getCallEvent() {
        return callEvent;
    }

    public String getCallId() {
        return callId;
    }
}
