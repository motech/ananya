package org.motechproject.ananya.request;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.motechproject.ananya.domain.InteractionKeys;

import java.util.ArrayList;
import java.util.List;

public class CertificateCourseStateRequestList {
    private List<CertificateCourseStateRequest> list = new ArrayList<CertificateCourseStateRequest>();
    private String callId;
    private String callerId;

    public CertificateCourseStateRequestList(String callId, String callerId) {
        this.callId = callId;
        this.callerId = callerId;
    }

    public void add(String json, String token) {
        list.add(CertificateCourseStateRequest.createFrom(callerId, callId, token, json));
    }

    public List<CertificateCourseStateRequest> all() {
        return list;
    }

    public boolean isNotEmpty() {
        return !list.isEmpty();
    }

    public CertificateCourseStateRequest lastRequest() {
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

    public CertificateCourseStateRequest firstRequest() {
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean hasCourseCompletionInteraction() {
        for (CertificateCourseStateRequest stateRequest : list)
            if (stateRequest.getInteractionKey().equalsIgnoreCase(InteractionKeys.PlayCourseResultInteraction))
                return true;
        return false;
    }
}
