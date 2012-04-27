package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;

public class AudioTrackerLogItem {
    @JsonProperty
    private String contentId;
    @JsonProperty
    private String timeStamp;
    @JsonProperty
    private  Integer duration;

    public AudioTrackerLogItem(String contentId, String timeStamp, Integer duration) {
        this.contentId = contentId;
        this.timeStamp = timeStamp;
        this.duration = duration;
    }

    public String getContentId() {
        return contentId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Integer getDuration() {
        return duration;
    }
}
