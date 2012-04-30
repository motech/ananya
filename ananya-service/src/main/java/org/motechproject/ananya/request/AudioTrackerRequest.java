package org.motechproject.ananya.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

public class AudioTrackerRequest extends BaseRequest {
    private String contentId;
    private String timeStamp;
    private Integer duration;

    public static AudioTrackerRequest createFrom(String callId, String callerId, String json, String token) {
        Gson gson = new Gson();
        Type type = new TypeToken<AudioTrackerRequest>() {
        }.getType();
        AudioTrackerRequest audioTrackerRequest = gson.fromJson(json, type);
        audioTrackerRequest.callerId = callerId;
        audioTrackerRequest.callId = callId;
        audioTrackerRequest.token = token;
        return audioTrackerRequest;
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

    public DateTime getTimeAsDateTime() {
        if (StringUtils.isBlank(timeStamp)) return null;
        return DateTime.parse(timeStamp);
    }
}
