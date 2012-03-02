package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;

@TypeDiscriminator("doc.type == 'CallLog'")
public class CallLog extends BaseLog{

    @JsonProperty
    private IvrFlow ivrFlow;

    public CallLog() {
    }

    public CallLog(String callId, String callerId, IvrFlow ivrFlow, DateTime startTime, DateTime endTime) {
        super(callerId, "", startTime, endTime, "", callId);
        this.ivrFlow = ivrFlow;
    }

    public IvrFlow getIvrFlow() {
        return ivrFlow;
    }

}
