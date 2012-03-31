package org.motechproject.ananya.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

public class CertificationCourseStateRequest extends BaseRequest {

    private Integer chapterIndex;
    private Integer lessonOrQuestionIndex;
    private Boolean result;
    private String interactionKey;

    private String contentId;
    private String contentType;
    private String contentName;
    private String courseItemState;
    private String contentData;
    private String certificateCourseId;
    private String time;

    public CertificationCourseStateRequest() {
    }

    public static CertificationCourseStateRequest createFrom(String callerId, String callId, String token, String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<CertificationCourseStateRequest>() {
        }.getType();
        CertificationCourseStateRequest certificationCourseStateRequest = gson.fromJson(json, type);
        certificationCourseStateRequest.callerId = callerId;
        certificationCourseStateRequest.callId = callId;
        certificationCourseStateRequest.token = token;
        return certificationCourseStateRequest;
    }

    public Boolean getResult() {
        return result;
    }

    public String getContentId() {
        return contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public String getCourseItemState() {
        return courseItemState;
    }

    public String getContentData() {
        return contentData;
    }

    public String getCertificateCourseId() {
        return certificateCourseId;
    }

    public Boolean result() {
        return result;
    }

    public Integer getLessonOrQuestionIndex() {
        return lessonOrQuestionIndex;
    }

    public Boolean isResult() {
        return result;
    }

    public String getInteractionKey() {
        return interactionKey;
    }

    public Integer getChapterIndex() {
        return chapterIndex;
    }

    public String getContentName() {
        return contentName;
    }

    public String getTime() {
        return time;
    }

    public DateTime getTimeAsDateTime() {
        if (StringUtils.isBlank(time)) return null;
        return DateTime.parse(time);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean hasContentId(){
        return StringUtils.isNotBlank(getContentId());
    }
}
