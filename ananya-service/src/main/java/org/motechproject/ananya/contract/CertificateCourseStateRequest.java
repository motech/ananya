package org.motechproject.ananya.contract;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

public class CertificateCourseStateRequest extends BaseRequest {

    private Boolean result;
    private Integer chapterIndex;
    private Integer lessonOrQuestionIndex;
    private String time;
    private String interactionKey;
    private String contentId;
    private String contentType;
    private String contentName;
    private String contentData;
    private String courseItemState;
    private String certificateCourseId;

    public CertificateCourseStateRequest() {
    }

    public static CertificateCourseStateRequest createFrom(String callerId, String callId, String token, String json) {
        CertificateCourseStateRequest certificationCourseStateRequest = new Gson().fromJson(json,
                new TypeToken<CertificateCourseStateRequest>() {
        }.getType());
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

    public String getInteractionKey() {
        return interactionKey;
    }

    public Integer getChapterIndex() {
        return chapterIndex;
    }

    public String getContentName() {
        return contentName;
    }

    public DateTime getTimeAsDateTime() {
        return StringUtils.isBlank(time) ? null : new DateTime(Long.valueOf(time));
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean hasContentId() {
        return StringUtils.isNotBlank(getContentId());
    }

}
