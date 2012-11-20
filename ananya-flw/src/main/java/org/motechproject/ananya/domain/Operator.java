package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
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
    private Integer startOfPulseInMilliSec;

    @JsonProperty
    private Integer endOfPulseInMilliSec;

    public Operator(String name, Integer allowedUsagePerMonth, Integer startOfPulseInMilliSec, Integer endOfPulseInMilliSec) {
        this.name = name;
        this.allowedUsagePerMonth = allowedUsagePerMonth;
        this.startOfPulseInMilliSec = startOfPulseInMilliSec;
        this.endOfPulseInMilliSec = endOfPulseInMilliSec;
    }

    public Operator() {
    }

    public String getName() {
        return name;
    }

    public Integer getAllowedUsagePerMonth() {
        return allowedUsagePerMonth;
    }

    @JsonIgnore
    public Integer getPulseToMilliSec() {
        return endOfPulseInMilliSec - startOfPulseInMilliSec;
    }

    public Integer getStartOfPulseInMilliSec() {
        return startOfPulseInMilliSec;
    }

    public Integer getEndOfPulseInMilliSec() {
        return endOfPulseInMilliSec;
    }

    public void setStartOfPulseInMilliSec(Integer startOfPulseInMilliSec) {
        this.startOfPulseInMilliSec = startOfPulseInMilliSec;
    }

    public void setEndOfPulseInMilliSec(Integer endOfPulseInMilliSec) {
        this.endOfPulseInMilliSec = endOfPulseInMilliSec;
    }
}
