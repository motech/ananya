package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

public class AudioTrackerLogItem {
    @JsonProperty
    private String contentId;
    @JsonProperty
    private String language;
    @JsonProperty
    private DateTime time;
    @JsonProperty
    private Integer duration;

    public AudioTrackerLogItem() {
    }

    public AudioTrackerLogItem(String contentId, String language, DateTime time, Integer duration) {
        this.contentId = contentId;
        this.language=language;
        this.time = time;
        this.duration = duration;
    }

    public String getContentId() {
        return contentId;
    }

    public DateTime getTime() {
        return time;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public int getPercentage(Integer totalDuration) {
        return (int) Math.round((double) duration * 100 / totalDuration);
    }

	public String getLanguage() {
		return language;
	}
    
}
