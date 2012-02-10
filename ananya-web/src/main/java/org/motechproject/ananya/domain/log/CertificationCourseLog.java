package org.motechproject.ananya.domain.log;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

@TypeDiscriminator("doc.type == 'CertificationCourseLog'")
public class CertificationCourseLog extends BaseLog {
    @JsonProperty
    private String chapterIndex;
    @JsonProperty
    private String lessonOrQuestionIndex;
    @JsonProperty
    private String questionResponse;
    @JsonProperty
    private Boolean result;
    private String interactionKey;

    public CertificationCourseLog() {

    }

    public CertificationCourseLog(String callerId, String callId, String token, String chapterIndex, String lessonOrQuestionIndex, String questionResponse, Boolean result, String interactionKey) {
        super(callerId, null, null, null, null, token, callId);
        this.chapterIndex = chapterIndex;
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
        this.questionResponse = questionResponse;
        this.result = result;
        this.interactionKey = interactionKey;
    }

    public String getLessonOrQuestionIndex() {
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

    public String getChapterIndex() {
        return chapterIndex;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }
}