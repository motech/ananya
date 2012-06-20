package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type == 'RegistrationLog'")
public class RegistrationLog extends BaseLog {

    @JsonProperty
    String circle;
    
    public RegistrationLog() {
    }

    public RegistrationLog(String callId, String callerId, String operator, String circle) {
        super(callerId, "", operator, callId);
        this.circle = circle;
    }
}