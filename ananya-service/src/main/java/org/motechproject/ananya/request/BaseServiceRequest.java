package org.motechproject.ananya.request;

import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.TransferDataList;

public class BaseServiceRequest extends BaseRequest {

    protected TransferDataList transferDataList;

    public BaseServiceRequest(String callId, String callerId, String calledNumber, String json) {
        super(callId, callerId, calledNumber);
        this.transferDataList = new TransferDataList(json);
    }

    public AudioTrackerRequestList getAudioTrackerRequestList() {
        AudioTrackerRequestList audioTrackerList = new AudioTrackerRequestList(callId, callerId);
        for (TransferData transferData : transferDataList.all())
            if (transferData.isAudioTrackerState())
                audioTrackerList.add(transferData.getData(), transferData.getToken());
        return audioTrackerList;
    }

    public CallDurationList getCallDurationList() {
        CallDurationList callDurationList = new CallDurationList(callId, callerId, calledNumber);
        for (TransferData transferData : transferDataList.all())
            if (transferData.isCallDuration())
                callDurationList.add(transferData.getData());
        return callDurationList;
    }

}
