package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.ananya.request.ILogCertificationCourse;

/**
 * Created by IntelliJ IDEA.
 * User: imdadah
 * Date: 21/02/12
 * Time: 9:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class CertificationCourseRequest extends BaseRequest implements ILogCertificationCourse{
    private Integer chapterIndex;
    private Integer lessonOrQuestionIndex;
    private String questionResponse;
    private Boolean result;
    private String interactionKey;

    public CertificationCourseRequest(String callerId, String callerNumber, Integer chapterIndex, Integer lessonOrQuestionIndex, String questionResponse, Boolean result, String interactionKey) {
        super(callerId, callerNumber);
        this.chapterIndex = chapterIndex;
        this.lessonOrQuestionIndex = lessonOrQuestionIndex;
        this.questionResponse = questionResponse;
        this.result = result;
        this.interactionKey = interactionKey;
    }

    @Override
    public Integer lessonOrQuestionIndex() {
        return this.lessonOrQuestionIndex;
    }

    @Override
    public String questionResponse() {
        return this.questionResponse;
    }

    @Override
    public Boolean result() {
        return this.result;
    }

    @Override
    public String interactionKey() {
        return this.interactionKey;
    }

    @Override
    public Integer chapterIndex() {
        return this.chapterIndex;
    }
}
