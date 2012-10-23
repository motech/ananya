package org.motechproject.ananya.response;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FLWCallDetail {
    @XmlElement
    private CallType callType;
    @XmlElement
    private String startTime;
    @XmlElement
    private String endTime;
    @XmlElement
    private Integer minutes;

    public FLWCallDetail() {
    }

    public FLWCallDetail(CallType callType, String startTime, String endTime, Integer minutes) {
        this.callType = callType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.minutes = minutes;
    }

    public CallType getCallType() {
        return callType;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Integer getMinutes() {
        return minutes;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
