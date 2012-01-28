package org.motechproject.bbcwt.ivr.jobaid.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Level;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.jobaid.IVRAction;
import org.motechproject.bbcwt.ivr.jobaid.JobAidFlowState;
import org.motechproject.bbcwt.service.JobAidContentService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class JobAidAction implements IVRAction {
    @Autowired
    private JobAidContentService jobAidContentService;

    public JobAidAction() {

    }

    public JobAidAction(JobAidContentService jobAidContentService) {
        this.jobAidContentService = jobAidContentService;
    }

    protected Lesson currentLesson(IVRContext context) {
        return currentChapter(context).getLessonByNumber(currentLessonNumber(context));
    }

    protected Chapter currentChapter(IVRContext context) {
        return currentLevel(context).getChapterByNumber(currentChapterNumber(context));
    }

    protected Level currentLevel(IVRContext context) {
        JobAidCourse course = currentCourse();
        return course.getLevelByNumber(currentLevelNumber(context));
    }

    protected JobAidCourse currentCourse() {
        return jobAidContentService.getCourse("JobAidCourse");
    }

    protected int currentLevelNumber(IVRContext context) {
        return jobFlowState(context).level();
    }

    protected JobAidFlowState jobFlowState(IVRContext context) {
        return ((JobAidFlowState)context.flowSpecificState());
    }

    protected int currentChapterNumber(IVRContext context) {
        return jobFlowState(context).chapter();
    }

    protected int currentLessonNumber(IVRContext context) {
        return jobFlowState(context).lesson();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    protected void assembleReturnToStartOption(IVRMessage messages, IVRDtmfBuilder dtmfBuilder) {
        dtmfBuilder.addPlayAudio(messages.absoluteFileLocation("jobaid/" + messages.get(IVRMessage.RETURN_TO_START_IN_JOBAID_OPTION)));
    }
}