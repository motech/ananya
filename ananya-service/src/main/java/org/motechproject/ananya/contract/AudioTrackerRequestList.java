package org.motechproject.ananya.contract;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class AudioTrackerRequestList {

    private String callId;
    private String callerId;
    private List<AudioTrackerRequest> audioTrackerRequestList;

    public AudioTrackerRequestList(String callId, String callerId) {
        this.callId = callId;
        this.callerId = callerId;
        audioTrackerRequestList = new ArrayList<>();
    }

    public void add(String json, String token, String language) {
        audioTrackerRequestList.add(AudioTrackerRequest.createFrom(callId, callerId, json, token, language));
    }

    public List<AudioTrackerRequest> all() {
        return audioTrackerRequestList;
    }

    public String getCallId() {
        return callId;
    }

    public String getCallerId() {
        return callerId;
    }

    public boolean isEmpty() {
        return audioTrackerRequestList.isEmpty();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(audioTrackerRequestList);
    }
}