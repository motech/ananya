package org.motechproject.ananya.response;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FLWCallDetail extends  FLWCallDuration {
    @XmlElement
    private CallType callType;

    public FLWCallDetail() {
        super();
    }

    public FLWCallDetail(CallType callType, String startTime, String endTime, Integer minutes) {
        super(startTime, endTime, minutes);
        this.callType = callType;
    }

    public CallType getCallType() {
        return callType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FLWCallDetail)) return false;

        FLWCallDetail that = (FLWCallDetail) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(this.callType, that.callType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.callType)
                .toHashCode();
    }
}
