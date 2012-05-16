package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type == 'SMSLog'")
public class SMSLog extends BaseLog {

    @JsonProperty
    private String locationId;

    @JsonProperty
    private Integer courseAttempts;

    public SMSLog(){
    }

    public SMSLog(String callerId) {
        super(callerId, "", "", "");
    }

    public SMSLog(String callId, String callerId, String locationId, Integer courseAttempts) {
        super(callId, callerId);
        this.locationId = locationId;
        this.courseAttempts = courseAttempts;
    }

    public String getLocationId() {
        return locationId;
    }

    public Integer getCourseAttempts() {
        return courseAttempts;
    }
}
