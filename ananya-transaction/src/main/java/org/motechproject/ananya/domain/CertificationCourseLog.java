package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.type == 'CertificationCourseLog'")
public class CertificationCourseLog extends BaseLog {

    @JsonProperty
    private String certificateCourseId;

    @JsonProperty
    private List<CertificationCourseLogItem> courseLogItems = new ArrayList<CertificationCourseLogItem>();

    public CertificationCourseLog() {
    }

    public CertificationCourseLog(String callerId, String calledNumber, DateTime startTime, DateTime endTime,
                                  String operator, String callId, String certificateCourseId) {
        super(callerId, calledNumber, operator, callId);
        this.certificateCourseId = certificateCourseId;
    }

    public void addCourseLogItem(CertificationCourseLogItem courseLogItem) {
        this.courseLogItems.add(courseLogItem);
    }

    public List<CertificationCourseLogItem> getCourseLogItems() {
        return courseLogItems;
    }

    public String getCertificateCourseId() {
        return certificateCourseId;
    }
}