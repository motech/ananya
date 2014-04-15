package org.motechproject.ananya.contract;

import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.TransferDataList;

public abstract class BaseServiceRequest extends BaseRequest {

    protected TransferDataList transferDataList;
    protected String circle;
    protected String operator;
    protected String language;

    public BaseServiceRequest(String callId, String callerId, String calledNumber) {
        super(callId, callerId, calledNumber);
    }

    public BaseServiceRequest(String callId, String callerId) {
        super(callId,callerId);
    }

    public AudioTrackerRequestList getAudioTrackerRequestList() {
        AudioTrackerRequestList audioTrackerList = new AudioTrackerRequestList(callId, callerId);
        for (TransferData transferData : transferDataList.all())
            if (transferData.isAudioTrackerState())
                audioTrackerList.add(transferData.getData(), transferData.getToken(), this.language);
        return audioTrackerList;
    }

    public CallDurationList getCallDurationList() {
        CallDurationList callDurationList = new CallDurationList(callId, callerId, calledNumber);
        for (TransferData transferData : transferDataList.all())
            if (transferData.isCallDuration())
                callDurationList.add(transferData.getData());
        return callDurationList;
    }

    public String getOperator() {
        return operator;
    }

    public String getCircle() {
        return circle;
    }

	public String getLanguage() {
		return language;
	}
}
