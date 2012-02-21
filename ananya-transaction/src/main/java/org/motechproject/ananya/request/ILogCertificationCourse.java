package org.motechproject.ananya.request;

/**
 * Created by IntelliJ IDEA.
 * User: imdadah
 * Date: 21/02/12
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ILogCertificationCourse extends ILogBase {
    public Integer lessonOrQuestionIndex();

    public String questionResponse();

    public Boolean result();

    public String interactionKey();

    public Integer chapterIndex();
}
