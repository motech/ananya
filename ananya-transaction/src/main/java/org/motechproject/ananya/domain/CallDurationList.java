package org.motechproject.ananya.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CallDurationList {

    private List<CallDuration> list = new ArrayList<CallDuration>();
    private String callId;
    private String callerId;

    public CallDurationList(String callId, String callerId) {
        this.callId = callId;
        this.callerId = callerId;
    }

    public List<CallDuration> all() {
        return list;
    }

    public String getCallId() {
        return callId;
    }

    public String getCallerId() {
        return callerId;
    }

    public void add(String data) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<CallDuration>() {
        }.getType();
        CallDuration callDuration = gson.fromJson(data, collectionType);
        callDuration.setCallId(callId);
        callDuration.setCallerId(callerId);
        list.add(callDuration);
    }
}
