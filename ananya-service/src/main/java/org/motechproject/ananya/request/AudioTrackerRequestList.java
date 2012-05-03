package org.motechproject.ananya.request;

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
        audioTrackerRequestList = new ArrayList<AudioTrackerRequest>();
    }

    public void add(String json, String token) {
        audioTrackerRequestList.add(AudioTrackerRequest.createFrom(callId, callerId, json, token));
    }

    public List<AudioTrackerRequest> getAll() {
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