package org.motechproject.bbcwt.ivr.jobaid.action;

import org.apache.log4j.Logger;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Level;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.ivr.jobaid.CallFlowExecutor;
import org.motechproject.bbcwt.ivr.jobaid.IVRAction;
import org.motechproject.bbcwt.service.JobAidContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayLesson extends JobAidAction {
    private static final Logger LOGGER = Logger.getLogger(PlayLesson.class);

    @Autowired
    private LessonSelection lessonSelection;
    @Autowired
    private IVRMessage messages;

    public PlayLesson() {

    }

    public PlayLesson(JobAidContentService jobAidContentService, LessonSelection lessonSelection, IVRMessage messages) {
        super(jobAidContentService);
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
        LOGGER.info("Trying to determine which menu is to be played.");

        Lesson currentLesson = currentLesson(context);
        Chapter chapterWhoseLessonMenuIsToBePlayed = currentChapter(context);
        Level levelContainingTheChapterWhoseLessonMenuIsToBePlayed = currentLevel(context);

        if(chapterWhoseLessonMenuIsToBePlayed.nextLesson(currentLesson) == null) {
            do {
                 chapterWhoseLessonMenuIsToBePlayed = levelContainingTheChapterWhoseLessonMenuIsToBePlayed.nextChapter(chapterWhoseLessonMenuIsToBePlayed);
            } while(chapterWhoseLessonMenuIsToBePlayed!=null && !chapterWhoseLessonMenuIsToBePlayed.hasLessons());
        }

        if(chapterWhoseLessonMenuIsToBePlayed==null) {
            LOGGER.info(String.format("No further chapters with lessons found found in current level: %d", levelContainingTheChapterWhoseLessonMenuIsToBePlayed.number()));

            do {
                levelContainingTheChapterWhoseLessonMenuIsToBePlayed = currentCourse().nextLevel(levelContainingTheChapterWhoseLessonMenuIsToBePlayed);
            } while(levelContainingTheChapterWhoseLessonMenuIsToBePlayed!=null && !levelContainingTheChapterWhoseLessonMenuIsToBePlayed.hasLessons());

            if(levelContainingTheChapterWhoseLessonMenuIsToBePlayed!=null) {
                chapterWhoseLessonMenuIsToBePlayed = levelContainingTheChapterWhoseLessonMenuIsToBePlayed.getChapterByNumber(1);
                while(!chapterWhoseLessonMenuIsToBePlayed.hasLessons()) {
                    chapterWhoseLessonMenuIsToBePlayed = levelContainingTheChapterWhoseLessonMenuIsToBePlayed.nextChapter(chapterWhoseLessonMenuIsToBePlayed);
                }
                LOGGER.info(String.format("Determined that lesson menu of level: %d and chapter: %d has to be played.", levelContainingTheChapterWhoseLessonMenuIsToBePlayed.number(), chapterWhoseLessonMenuIsToBePlayed.getNumber()));
            }
        }


        if(levelContainingTheChapterWhoseLessonMenuIsToBePlayed == null && chapterWhoseLessonMenuIsToBePlayed == null) {
            LOGGER.info("No further levels and chapters with lessons.");
            levelContainingTheChapterWhoseLessonMenuIsToBePlayed = currentLevel(context);
            chapterWhoseLessonMenuIsToBePlayed = currentChapter(context);
        }

        LOGGER.info(String.format("Setting current state to level: %d and chapter: %d, whose lesson menu has to be played.", levelContainingTheChapterWhoseLessonMenuIsToBePlayed.number(), chapterWhoseLessonMenuIsToBePlayed.getNumber()));
        jobFlowState(context).setLevel(levelContainingTheChapterWhoseLessonMenuIsToBePlayed.number());
        jobFlowState(context).setChapter(chapterWhoseLessonMenuIsToBePlayed.getNumber());

        return lessonSelection;
    }
}