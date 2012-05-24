package org.motechproject.ananya.requests;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CallMessage implements Serializable {

    private static String CALL_ID = "callId";
    private static String CALLER_ID = "callerId";

    private CallMessageType type;
    private Map<String, String> payload;


    public CallMessage(CallMessageType type, String callId, String callerId) {
        this.type = type;
        this.payload = new HashMap<String, String>();
        payload.put(CALL_ID, callId);
        payload.put(CALLER_ID, callerId);
    }

    public CallMessage(CallMessageType type, String callerId) {
        this.type = type;
        this.payload = new HashMap<String, String>();
        payload.put(CALLER_ID, callerId);
    }


    public CallMessageType getType() {
        return type;
    }

    public String getCallId() {
        return payload.get(CALL_ID);
    }

    public String getCallerId() {
        return payload.get(CALLER_ID);
    }

    @Override
    public String toString() {
        return "CallMessage{type=" + type + "|payload=" + payload + "}";
    }
}
