package org.motechproject.ananya.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "flw")
public class FLWNighttimeCallsResponse {

    public FLWNighttimeCallsResponse() {

    }

    @XmlElementWrapper(name = "callDetails")
    @XmlElement(name = "callDetail")
    @JsonProperty(value = "callDetails")
    private List<FLWCallDuration> callDurations;

    public FLWNighttimeCallsResponse(List<FLWCallDuration> callDurations) {
        this.callDurations = callDurations;
    }

    public List<FLWCallDuration> getCallDurations() {
        return callDurations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FLWNighttimeCallsResponse that = (FLWNighttimeCallsResponse) o;

        return new EqualsBuilder()
                .append(this.callDurations, that.callDurations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.callDurations)
                .hashCode();
    }
}
