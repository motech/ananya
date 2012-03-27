package org.motechproject.ananya.framework.domain;

import java.util.ArrayList;
import java.util.List;

public class JobAidRequest {
    private String callerId;
    private String operator;
    private List<String> promptsHeard;
    private Integer callDuration;

    public JobAidRequest(String callerId, String operator) {
        this.callerId = callerId;
        this.operator = operator;
        promptsHeard = new ArrayList<String>();
    }

    public String getCallerId() {
        return callerId;
    }

    public String getOperator() {
        return operator;
    }
    
    public void addPromptHeard(String prompt){
        promptsHeard.add(prompt);
    }

    public List<String> getPromptsHeard() {
        return promptsHeard;
    }

    public void setCallDuration(Integer callDuration) {
        this.callDuration = callDuration;
    }

    public Integer getCallDuration() {
        return callDuration;
    }
}
