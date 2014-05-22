package org.motechproject.ananya.framework.domain;

public class JobAidDisconnectRequest {
    private String callerId;
    private String operator;
    private String jsonData;
    private String callId;
    private String calledNumber;
    private String callDuration;
    private String promptList;
    private String circle;
    private String language;

    public JobAidDisconnectRequest(String callerId, String callId, String operator, String circle,
                                   String calledNumber, String callDuration, String promptList, String jsonData, String language) {
        this.callerId = callerId;
        this.operator = operator;
        this.callId = callId;
        this.circle = circle;
        this.calledNumber = calledNumber;
        this.callDuration = callDuration;
        this.promptList = promptList;
        this.jsonData = jsonData;
        this.language = language;
    }

    public String getCallerId() {
        return callerId;
    }

    public String getOperator() {
        return operator;
    }

    public String getCallId() {
        return callId;
    }

    public String getJsonPostData() {
        return this.jsonData;
    }

    public String getCalledNumber() {
        return calledNumber;
    }

    public String getPromptList() {
        return promptList;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public String getCircle() {
        return circle;
    }

	public String getLanguage() {
		return language;
	}
    
}
