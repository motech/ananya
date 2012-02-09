package org.motechproject.ananya.domain.log;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;


@TypeDiscriminator("doc.type == 'RegistrationLog'")
public class RegistrationLog extends BaseLog {
    @JsonProperty
    private String designation;
    @JsonProperty
    private String district;
    @JsonProperty
    private String block;
    @JsonProperty
    private String panchayat;

    public RegistrationLog() {
    }

    public RegistrationLog(String callerId, String calledNumber, DateTime startTime, DateTime endTime, String operator) {
        super(callerId, calledNumber, startTime, endTime, operator);
    }

    public RegistrationLog designation(String designation) {
        this.designation = designation;
        return this;
    }

    public RegistrationLog panchayat(String panchayat) {
        this.panchayat = panchayat;
        return this;
    }
}
