package org.motechproject.ananya.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class JobAidServiceRequest extends BaseServiceRequest {

    private String promptList;
    private Integer callDuration;

    public JobAidServiceRequest(String callId, String callerId, String calledNumber,
                                String jsonData, String promptList, Integer callDuration) {
        super(callId, callerId, calledNumber,jsonData);
        this.promptList = promptList;
        this.callDuration = callDuration;
    }

    public Integer getCallDuration() {
        return callDuration;
    }

    public List<String> getPrompts() {
        return new Gson().fromJson(promptList, new TypeToken<List<String>>() {
        }.getType());
    }

}
