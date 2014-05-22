package org.motechproject.ananya.contract;

import java.util.List;

import org.motechproject.ananya.domain.ServiceType;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.TransferDataList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CertificateCourseServiceRequest extends BaseServiceRequest {
	
	 private String promptList;
	 private Integer ccCallDuration;

    public CertificateCourseServiceRequest(String callId, String callerId, String calledNumber) {
        super(callId, callerId, calledNumber);
    }

    public CertificateCourseServiceRequest(String callId, String callerId) {
        super(callId, callerId);
    }

    public CertificateCourseStateRequestList getCertificateCourseStateRequestList() {
        CertificateCourseStateRequestList certificateCourseStateRequestList = new CertificateCourseStateRequestList(callId, callerId);
        for (TransferData transferData : transferDataList.all())
            if (transferData.isCCState())
                certificateCourseStateRequestList.add(transferData.getData(), transferData.getToken(), this.language);
        return certificateCourseStateRequestList;
    }

    public CertificateCourseServiceRequest withJson(String json) {
        this.transferDataList = new TransferDataList(json);
        return this;
    }

    public CertificateCourseServiceRequest withCircle(String circle) {
        this.circle = circle;
        return this;
    }

    public CertificateCourseServiceRequest withOperator(String operator) {
        this.operator = operator;
        return this;
    }
    
    public CertificateCourseServiceRequest withLanguage(String language) {
        this.language = language;
        return this;
    }
    
    public CertificateCourseServiceRequest withPromptList(String promptList) {
        this.promptList = promptList;
        return this;
    }
    
    public CertificateCourseServiceRequest withCallDuration(Integer ccCallDuration) {
        this.ccCallDuration = ccCallDuration;
        return this;
    }
    
    @Override
    public ServiceType getType() {
        return ServiceType.CERTIFICATE_COURSE;
    }

    public List<String> getPrompts() {
        return new Gson().fromJson(promptList, new TypeToken<List<String>>() {
        }.getType());
    }

	public Integer getCcCallDuration() {
		return ccCallDuration;
	}
    
    
}