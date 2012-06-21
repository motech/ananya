package org.motechproject.ananya.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.TransferDataList;

import java.util.List;

public class JobAidServiceRequest extends BaseRequest {

    private String jsonData;
    private String promptIds;
    private Integer callDuration;
    private TransferDataList transferDataList;

    public JobAidServiceRequest(String callId, String callerId, String calledNumber, String jsonData, String promptIds, Integer callDuration) {
        super(callId, callerId, calledNumber);
        this.jsonData = jsonData;
        this.promptIds = promptIds;
        this.callDuration = callDuration;
        this.transferDataList = new TransferDataList(jsonData);
    }

    public AudioTrackerRequestList getAudioTrackerRequestList() {
        AudioTrackerRequestList audioTrackerList = new AudioTrackerRequestList(callId, callerId);
        for (TransferData transferData : transferDataList.all()) {
            if (transferData.isAudioTrackerState())
                audioTrackerList.add(transferData.getData(), transferData.getToken());
        }
        return audioTrackerList;
    }

    public CallDurationList getCallDurationList(){
        CallDurationList callDurationList = new CallDurationList(callId, callerId, calledNumber);
        for(TransferData transferData :transferDataList.all()){
            if(transferData.isCallDuration())
                callDurationList.add(transferData.getData());
        }
        return callDurationList;
    }

    public Integer getCallDuration() {
        return callDuration;
    }

    public List<String> getPrompts(){
        return new Gson().fromJson(promptIds, new TypeToken<List<String>>(){}.getType());
    }

}
