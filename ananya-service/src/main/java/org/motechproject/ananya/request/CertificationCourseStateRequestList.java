package org.motechproject.ananya.request;

import java.util.ArrayList;
import java.util.List;

public class CertificationCourseStateRequestList {
    private List<CertificationCourseStateRequest> list = new ArrayList<CertificationCourseStateRequest>();

    public void add(String callId, String callerId, String json, String token) {
        list.add(CertificationCourseStateRequest.makeObjectFromJson(callerId, callId, token, json));
    }

    public List<CertificationCourseStateRequest> all() {
        return list;
    }

}
