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
    protected String language;
    
    @JsonProperty
    private List<CertificationCourseLogItem> courseLogItems = new ArrayList<CertificationCourseLogItem>();

    public CertificationCourseLog() {
    }

    public CertificationCourseLog(String callerId, String calledNumber, String operator, String callId, String certificateCourseId, String language) {
        super(callerId, calledNumber, operator, callId);
        this.language= language;
        this.certificateCourseId = certificateCourseId;
    }

    public void addCourseLogItem(CertificationCourseLogItem courseLogItem) {
        this.courseLogItems.add(courseLogItem);
    }

    public List<CertificationCourseLogItem> items() {
        return courseLogItems;
    }

    public String getCertificateCourseId() {
        return certificateCourseId;
    }

	public String getLanguage() {
		return language;
	}
	
    public boolean hasNoItems() {
        return courseLogItems == null || courseLogItems.isEmpty();
    }

    public DateTime time() {
        return hasNoItems() ? null : courseLogItems.get(0).getTime();
    }
}