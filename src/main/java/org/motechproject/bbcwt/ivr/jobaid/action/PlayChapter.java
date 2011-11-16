package org.motechproject.bbcwt.ivr.jobaid.action;

import org.apache.log4j.Logger;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.JobAidCourse;
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
public class PlayChapter implements IVRAction {
    private static final Logger LOGGER = Logger.getLogger(PlayChapter.class);

    @Autowired
    private JobAidContentService jobAidContentService;
    @Autowired
    private LessonSelection lessonSelection;
    @Autowired
    private ChapterSelection chapterSelection;
    @Autowired
    private IVRMessage messages;

    public PlayChapter() {

    }

    public PlayChapter(JobAidContentService jobAidContentService, ChapterSelection chapterSelection, LessonSelection lessonSelection, IVRMessage messages) {
        this.jobAidContentService = jobAidContentService;
        this.lessonSelection = lessonSelection;
        this.chapterSelection = chapterSelection;
        this.messages = messages;
    }

    @Override
    public void processRequest(IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder) {
        Chapter chapter = currentChapter(context);

        if(chapter.title()!=null) {
            String chapterTitle = messages.absoluteFileLocation("jobAid/" + chapter.title());
            LOGGER.info(String.format("Playing title for chapter %d in level %d : %s.", currentChapterNumber(context), currentLevelNumber(context), chapterTitle));
            responseBuilder.addPlayAudio(chapterTitle);
        }
        else {
            LOGGER.info(String.format("There is no title for chapter %%d in level %d.", currentChapterNumber(context), currentLevelNumber(context)));
        }
    }

    private Chapter currentChapter(IVRContext context) {
        JobAidCourse course = jobAidContentService.getCourse("JobAidCourse");
        return course.levels().get(currentLevelNumber(context)).chapters().get(currentChapterNumber(context));
    }

    private int currentLevelNumber(IVRContext context) {
        return ((JobAidFlowState)context.flowSpecificState()).level();
    }

    private int currentChapterNumber(IVRContext context) {
        return ((JobAidFlowState)context.flowSpecificState()).chapter();
    }

    @Override
    public void playPrompt(IVRContext context, IVRRequest request, IVRDtmfBuilder dtmfBuilder) {
        //Do nothing.
    }

    @Override
    public CallFlowExecutor.ProcessStatus validateInput(IVRContext context, IVRRequest request) {
        return CallFlowExecutor.ProcessStatus.OK;
    }

    @Override
    public IVRAction processAndForwardToNextState(IVRContext context, IVRRequest request) {
        Chapter chapter = currentChapter(context);
        if(chapter.getLessons().size() == 0) {
            LOGGER.info(String.format("There are no lessons in chapter: %d in the level: %d, hence going back to the chapter menu.", currentChapterNumber(context), currentLevelNumber(context)));
            return chapterSelection;
        }
        return lessonSelection;
    }
}