package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type == 'SMSLog'")
public class SMSLog extends BaseLog {

    @JsonProperty
    private String locationId;

    @JsonProperty
    private Integer courseAttempts;
    
    @JsonProperty
    private String language;

    public SMSLog(){
    }

    public SMSLog(String callerId) {
        super(callerId, "", "", "");
    }

    public SMSLog(String callId, String callerId, String locationId, Integer courseAttempts, String language) {
        super(callId, callerId);
        this.locationId = locationId;
        this.courseAttempts = courseAttempts;
        this.language=language;
    }

    public String getLocationId() {
        return locationId;
    }

    public Integer getCourseAttempts() {
        return courseAttempts;
    }

	public String getLanguage() {
		return language;
	}
    
}
