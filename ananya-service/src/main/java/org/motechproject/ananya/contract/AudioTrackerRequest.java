package org.motechproject.ananya.contract;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

public class AudioTrackerRequest extends BaseRequest {
    private String contentId;
    private String language;
    private String time;
    private Integer duration;

    public AudioTrackerRequest() {
    }

    public static AudioTrackerRequest createFrom(String callId, String callerId, String json, String token, String language) {
        Gson gson = new Gson();
        Type type = new TypeToken<AudioTrackerRequest>() {
        }.getType();
        AudioTrackerRequest audioTrackerRequest = gson.fromJson(json, type);
        audioTrackerRequest.callerId = callerId;
        audioTrackerRequest.callId = callId;
        audioTrackerRequest.token = token;
        audioTrackerRequest.language=language;
        return audioTrackerRequest;
    }

    public String getContentId() {
        return contentId;
    }

    public String getTime() {
        return time;
    }

    public Integer getDuration() {
        return duration;
    }

    public DateTime getTimeAsDateTime() {
        if (StringUtils.isBlank(time)) return null;
        return new DateTime(Long.valueOf(time));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

	public String getLanguage() {
		return language;
	}
}
