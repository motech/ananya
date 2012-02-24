package org.motechproject.ananya.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type == 'CertificationCourseLog'")
public class CertificationCourseLog extends BaseLog {
    @JsonProperty
    private Integer chapterIndex;
    @JsonProperty
    private Integer lessonOrQuestionIndex;
    @JsonProperty
    private String questionResponse;
    @JsonProperty
    private Boolean result;
    @JsonProperty
    private String interactionKey;

    public CertificationCourseLog() {

    }

    public CertificationCourseLog(String callerId, String callId, String token, Integer chapterIndex, Integer lessonOrQuestionIndex, String questionResponse, Boolean result, String interactionKey) {
        super(callerId, null, null, null, null, callId);
        this.chapterIndex = chapterIndex;
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
        this.questionResponse = questionResponse;
        this.result = result;
        this.interactionKey = interactionKey;
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

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }
}