package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.type === 'CallLog'")
public class CallLog extends MotechBaseDataObject {

    @JsonProperty
    List<CallLogItem> callLogItems = new ArrayList<CallLogItem>();

    @JsonProperty
    private String callId;

    @JsonProperty
    private String callerId;

    public CallLog() {
    }

    public CallLog(String callId, String callerId) {
        this.callerId = callerId;
        this.callId = callId;
    }

    public void addItem(CallLogItem callLogItem) {
        callLogItems.add(callLogItem);
    }

    public List<CallLogItem> getCallLogItems() {
        return callLogItems;
    }

    public void addOrUpdate(CallLogItem callLogItem) {
        for (CallLogItem c : callLogItems) {
            if (c.getCallFlowType().equals(callLogItem.getCallFlowType())) {
                if (callLogItem.getStartTime() != null)
                    c.setStartTime(callLogItem.getStartTime());
                if (callLogItem.getEndTime() != null)
                    c.setEndTime(callLogItem.getEndTime());
                return;
            }
        }
        callLogItems.add(callLogItem);
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
