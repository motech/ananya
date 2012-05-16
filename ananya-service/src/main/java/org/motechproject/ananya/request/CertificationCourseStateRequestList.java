package org.motechproject.ananya.request;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.motechproject.ananya.domain.InteractionKeys;

import java.util.ArrayList;
import java.util.List;

public class CertificationCourseStateRequestList {
    private List<CertificationCourseStateRequest> list = new ArrayList<CertificationCourseStateRequest>();
    private String callId;
    private String callerId;

    public CertificationCourseStateRequestList(String callId, String callerId) {
        this.callId = callId;
        this.callerId = callerId;
    }

    public void add(String json, String token) {
        list.add(CertificationCourseStateRequest.createFrom(callerId, callId, token, json));
    }

    public List<CertificationCourseStateRequest> all() {
        return list;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public CertificationCourseStateRequest lastRequest() {
        return list.get(list.size() - 1);
    }

    public String getCallerId() {
        return callerId;
    }

    public String getCallId() {
        return callId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(list);
    }

    public CertificationCourseStateRequest firstRequest() {
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean hasCourseCompletionInteraction() {
        for (CertificationCourseStateRequest stateRequest : list)
            if (stateRequest.getInteractionKey().equalsIgnoreCase(InteractionKeys.PlayCourseResultInteraction))
                return true;
        return false;
    }
}
