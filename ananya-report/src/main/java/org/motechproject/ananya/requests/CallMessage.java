package org.motechproject.ananya.requests;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CallMessage implements Serializable {

    private static String CALL_ID = "callId";
    private CallMessageType type;
    private Map<String, String> payload;

    public CallMessage(CallMessageType type, String callId) {
        this.type = type;
        this.payload = new HashMap<String, String>();
        payload.put(CALL_ID, callId);
    }


    public CallMessageType getType() {
        return type;
    }

    public String getCallId() {
        return payload.get(CALL_ID);
    }

    @Override
    public String toString() {
        return "CallMessage{type=" + type + "|payload=" + payload + "}";
    }
}
