package org.motechproject.ananya.request;


public class CertificateCourseStateFlwRequest {
    private Integer chapterIndex;
    private Integer lessonOrQuestionIndex;
    private Boolean result;
    private String interactionKey;
    private String callId;
    private String callerId;

    public CertificateCourseStateFlwRequest(Integer chapterIndex, Integer lessonOrQuestionIndex, Boolean result, String interactionKey, String callId, String callerId) {
        this.chapterIndex = chapterIndex;
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
        this.result = result;
        this.interactionKey = interactionKey;
        this.callId = callId;
        this.callerId = callerId;
    }

    public String getCallerId() {
        return callerId;
    }

    public String getCallId() {
        return callId;
    }

    public Integer getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(Integer chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public Integer getLessonOrQuestionIndex() {
        return lessonOrQuestionIndex;
    }

    public void setLessonOrQuestionIndex(Integer lessonOrQuestionIndex) {
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
    }

    public Boolean getResult() {
        return result;
    }

    public String getInteractionKey() {
        return interactionKey;
    }

}
