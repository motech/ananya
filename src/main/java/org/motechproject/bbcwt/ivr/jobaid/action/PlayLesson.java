package org.motechproject.bbcwt.ivr.jobaid.action;

import org.apache.log4j.Logger;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Level;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.ivr.jobaid.CallFlowExecutor;
import org.motechproject.bbcwt.ivr.jobaid.IVRAction;
import org.motechproject.bbcwt.ivr.jobaid.JobAidFlowState;
import org.motechproject.bbcwt.service.JobAidContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayLesson implements IVRAction {
    private static final Logger LOGGER = Logger.getLogger(PlayLesson.class);

    @Autowired
    private JobAidContentService jobAidContentService;
    @Autowired
    private LessonSelection lessonSelection;
    @Autowired
    private IVRMessage messages;

    public PlayLesson() {

    }

    public PlayLesson(JobAidContentService jobAidContentService, LessonSelection lessonSelection, IVRMessage messages) {
        this.jobAidContentService = jobAidContentService;
        this.lessonSelection = lessonSelection;
        this.messages = messages;
    }

    @Override
    public void processRequest(IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder) {
        Lesson lesson = currentLesson(context);
        String lessonLocation = messages.absoluteFileLocation("jobAid/" + lesson.getFileName());
        LOGGER.info(String.format("Playing lesson file: %s for lesson: %d in chapter: %d in level %d.", lessonLocation, currentLessonNumber(context), currentChapterNumber(context), currentLevelNumber(context)));
        responseBuilder.addPlayAudio(lessonLocation);

    }

    private Lesson currentLesson(IVRContext context) {
        return currentChapter(context).getLessonByNumber(currentLessonNumber(context));
    }

    private Chapter currentChapter(IVRContext context) {
        return currentLevel(context).chapters().get(currentChapterNumber(context));
    }

    private Level currentLevel(IVRContext context) {
        JobAidCourse course = jobAidContentService.getCourse("JobAidCourse");
        return course.levels().get(currentLevelNumber(context));
    }

    private int currentLevelNumber(IVRContext context) {
        return jobFlowState(context).level();
    }

    private JobAidFlowState jobFlowState(IVRContext context) {
        return ((JobAidFlowState)context.flowSpecificState());
    }

    private int currentChapterNumber(IVRContext context) {
        return jobFlowState(context).chapter();
    }

    private int currentLessonNumber(IVRContext context) {
        return jobFlowState(context).lesson();
    }

    @Override
    public void playPrompt(IVRContext context, IVRRequest request, IVRDtmfBuilder dtmfBuilder) {
        //Do Nothing
    }

    @Override
    public CallFlowExecutor.ProcessStatus validateInput(IVRContext context, IVRRequest request) {
        return CallFlowExecutor.ProcessStatus.OK;
    }

    @Override
    public IVRAction processAndForwardToNextState(IVRContext context, IVRRequest request) {
        return lessonSelection;
    }
}