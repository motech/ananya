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
    private String calledNumber;

    public CallDurationList(String callId, String callerId, String calledNumber) {
        this.callId = callId;
        this.callerId = callerId;
        this.calledNumber = calledNumber;
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

    public String getCalledNumber() {
        return calledNumber;
    }

    public void add(String data) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<CallDuration>() {
        }.getType();
        CallDuration callDuration = gson.fromJson(data, collectionType);
        list.add(callDuration);
    }
}
