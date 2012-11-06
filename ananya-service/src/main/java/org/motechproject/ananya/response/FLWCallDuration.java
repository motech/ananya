package org.motechproject.ananya.response;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FLWCallDuration {
    @XmlElement
    private String startTime;

    @XmlElement
    private String endTime;

    @XmlElement(name = "minutes")
    @JsonProperty("minutes")
    private Integer pulse;

    public FLWCallDuration() {
    }

    public FLWCallDuration(String startTime, String endTime, Integer pulse) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.pulse = pulse;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Integer getPulse() {
        return pulse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FLWCallDuration)) return false;

        FLWCallDuration that = (FLWCallDuration) o;

        return new EqualsBuilder()
                .append(this.startTime, that.startTime)
                .append(this.endTime, that.endTime)
                .append(this.pulse, that.pulse)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.startTime)
                .append(this.endTime)
                .append(this.pulse)
                .toHashCode();
    }
}
