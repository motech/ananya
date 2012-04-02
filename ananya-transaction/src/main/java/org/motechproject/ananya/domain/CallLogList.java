package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.type === 'CallLogList'")
public class CallLogList extends MotechBaseDataObject {

    @JsonProperty
    List<CallLog> callLogs = new ArrayList<CallLog>();

    @JsonProperty
    private String callId;

    @JsonProperty
    private String callerId;

    public CallLogList() {
    }

    public CallLogList(String callId, String callerId) {
        this.callerId = callerId;
        this.callId = callId;
    }

    public void add(CallLog callLog) {
        callLogs.add(callLog);
    }

    public List<CallLog> getCallLogs() {
        return callLogs;
    }

    public void addOrUpdate(CallLog callLog) {
        for (CallLog c : callLogs) {
            if (c.getCallFlowType().equals(callLog.getCallFlowType())) {
                if (callLog.getStartTime() != null)
                    c.setStartTime(callLog.getStartTime());
                if (callLog.getEndTime() != null)
                    c.setEndTime(callLog.getEndTime());
                return;
            }
        }
        callLogs.add(callLog);
    }

    public String getCallId() {
        return callId;
    }

    public String getCallerId() {
        return callerId;
    }

    public Long callerIdAsLong() {
        return Long.parseLong(callerId);
    }
}
