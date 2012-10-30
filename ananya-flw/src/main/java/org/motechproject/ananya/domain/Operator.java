package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Operator'")
public class Operator extends MotechBaseDataObject {
    @JsonProperty
    private String name;
    @JsonProperty
    private Integer allowedUsagePerMonth;
    @JsonProperty
    private Integer pulseToMilliSec;

    public Operator(String name, Integer allowedUsagePerMonth, Integer pulseToMilliSec) {
        this.name = name;
        this.allowedUsagePerMonth = allowedUsagePerMonth;
        this.pulseToMilliSec = pulseToMilliSec;
    }

    public Operator() {
    }

    public String getName() {
        return name;
    }

    public Integer getAllowedUsagePerMonth() {
        return allowedUsagePerMonth;
    }

    public Integer getPulseToMilliSec() {
        return pulseToMilliSec;
    }

    public void setPulseToMilliSec(Integer pulseToMilliSec) {
        this.pulseToMilliSec = pulseToMilliSec;
    }
}
