package org.motechproject.ananya.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CallDurationData {
    private CallEvent event;
    private String time;

    public CallDurationData(CallEvent event, String time) {
        this.event = event;
        this.time = time;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public CallEvent getEvent() {
        return event;
    }

    public String getTime() {
        return time;
    }
}
