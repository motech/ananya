package org.motechproject.ananya.contract;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.domain.TransferDataList;

import java.util.List;

public class JobAidServiceRequest extends BaseServiceRequest {

    private String promptList;
    private Integer callDuration;

    public JobAidServiceRequest(String callId, String callerId, String calledNumber) {
        super(callId, callerId, calledNumber);
    }

    public JobAidServiceRequest(String callId, String callerId) {
        super(callId, callerId);
    }

    public Integer getCallDuration() {
        return callDuration;
    }

    public String getPromptList() {
        return promptList;
    }

    public List<String> getPrompts() {
        return new Gson().fromJson(promptList, new TypeToken<List<String>>() {
        }.getType());
    }

    public JobAidServiceRequest withPromptList(String promptList) {
        this.promptList = promptList;
        return this;
    }

    public JobAidServiceRequest withCallDuration(Integer callDuration) {
        this.callDuration = callDuration;
        return this;
    }

    public JobAidServiceRequest withJson(String json) {
        this.transferDataList = new TransferDataList(json);
        return this;
    }

    public JobAidServiceRequest withCircle(String circle) {
        this.circle = circle;
        return this;
    }

    public JobAidServiceRequest withOperator(String operator) {
        this.operator = operator;
        return this;
    }

    public JobAidServiceRequest withLanguage(String language) {
		this.language=language;
		return this;
	}
    
    @Override
    public ServiceType getType() {
        return ServiceType.JOB_AID;
    }

}
