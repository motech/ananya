package org.motechproject.ananya.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CertificationCourseBookmark extends BaseRequest{
    private Integer chapterIndex;
    private Integer lessonOrQuestionIndex;
    private String questionResponse;
    private Boolean result;
    private String interactionKey;

    public CertificationCourseBookmark(){
    }

    public CertificationCourseBookmark(String callerId, String callId, Integer chapterIndex, Integer lessonOrQuestionIndex, String questionResponse, Boolean result, String interactionKey) {
        super(callerId, callId);
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
}
