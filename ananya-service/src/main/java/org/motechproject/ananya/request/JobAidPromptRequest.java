package org.motechproject.ananya.request;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class JobAidPromptRequest {
    
    private String callId;
   
    private String callerId;
    
    private List<String> promptList;


    public JobAidPromptRequest(String callId, String callerId, String promptIds) {
        this.callId = callId;
        this.callerId = callerId;
        this.promptList = new Gson().fromJson(promptIds, new TypeToken<List<String>>(){}.getType());
    }
    
    public String getCallId() {
        return callId;
    }

    public String getCallerId() {
        return callerId;
    }

    public List<String> getPromptList() {
        return promptList;
    }
}
