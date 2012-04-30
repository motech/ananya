package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

public class AudioTrackerLogItem {
    @JsonProperty
    private String contentId;
    @JsonProperty
    private DateTime timeStamp;
    @JsonProperty
    private  Integer duration;

    public AudioTrackerLogItem(String contentId, DateTime timeStamp, Integer duration) {
        this.contentId = contentId;
        this.timeStamp = timeStamp;
        this.duration = duration;
    }

    public String getContentId() {
        return contentId;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public Integer getDuration() {
        return duration;
    }
}
