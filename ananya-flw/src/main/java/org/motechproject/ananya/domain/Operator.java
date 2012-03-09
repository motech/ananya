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

    public Operator(String name, Integer allowedUsagePerMonth) {
        this.name = name;
        this.allowedUsagePerMonth = allowedUsagePerMonth;
    }

    public Operator() {
    }

    public String getName() {
        return name;
    }

    public Integer getAllowedUsagePerMonth() {
        return allowedUsagePerMonth;
    }
}
