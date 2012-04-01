package org.motechproject.ananya.request;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class CertificationCourseStateRequestList {
    private List<CertificationCourseStateRequest> list = new ArrayList<CertificationCourseStateRequest>();

    public void add(String callId, String callerId, String json, String token) {
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(list);
    }

    public CertificationCourseStateRequest firstRequest() {
        return list.isEmpty() ? null : list.get(0);
    }
}
