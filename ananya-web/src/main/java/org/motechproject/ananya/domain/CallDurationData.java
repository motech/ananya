package org.motechproject.ananya.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CallDurationData {
    private CallEvent event;
    private long time;

    public CallDurationData(CallEvent event, long time) {
        this.event = event;
        this.time = time;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public CallEvent getEvent() {
        return event;
    }

    public long getTime() {
        return time;
    }
}
