package org.motechproject.ananya.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

public class CertificationCourseStateRequest extends BaseRequest {

    private Integer chapterIndex;
    private Integer lessonOrQuestionIndex;
    private String questionResponse;
    private Boolean result;
    private String interactionKey;

    private String contentId;
    private String contentType;
    private String contentName;
    private String courseItemState;
    private String contentData;
    private String certificateCourseId;
    private DateTime time;

    public CertificationCourseStateRequest() {
    }

    public static CertificationCourseStateRequest makeObjectFromJson(String callerId, String callId,
                                                                     String dataToken, String certificateCourseJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<CertificationCourseStateRequest>(){}.getType();
        CertificationCourseStateRequest certificationCourseStateRequest = gson.fromJson(certificateCourseJson, type);
        
        certificationCourseStateRequest.callerId = callerId;
        certificationCourseStateRequest.callId = callId;
        certificationCourseStateRequest.token = dataToken;
        
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

    public String getQuestionResponse() {
        return questionResponse;
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

    public DateTime getTime() {
        return time;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
