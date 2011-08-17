package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.MilestonesRepository;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class LessonEndAnswerActionTest extends BaseActionTest {

    private LessonEndAnswerAction lessonEndAnswerAction;

    @Mock
    private ChaptersRespository chaptersRespository;
    @Mock
    private MilestonesRepository milestonesRepository;

    private String callerId;
    private Milestone currentMilestone;
    private HealthWorker healthWorker;
    private Chapter chapter;
    private Lesson currentLesson;

    @Before
    public void setUp() {
        callerId = "9999988888";

        chapter = new Chapter(1);
        chapter.setId("ChapterId1");
        Lesson lesson1 = new Lesson(1, "Lesson 1", "Lesson 1 End Menu");
        currentLesson = new Lesson(2, "Lesson 2", "Lesson 2 End Menu");
        Lesson lesson3 = new Lesson(3, "Lesson 3", "Lesson 3 End Menu");
        chapter.addLesson(lesson1);
        chapter.addLesson(currentLesson);
        chapter.addLesson(lesson3);

        currentMilestone = new Milestone("unique-id-for-" + callerId, chapter.getId(), currentLesson.getId(), null, new Date());

        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(callerId);
        when(milestonesRepository.markLastMilestoneFinish(callerId)).thenReturn(currentMilestone);
        when(chaptersRespository.get(currentMilestone.getChapterId())).thenReturn(chapter);

        lessonEndAnswerAction = new LessonEndAnswerAction(chaptersRespository, milestonesRepository, messages);
    }

    @Test
    public void shouldSetTheEndDateInTheMilestone() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "1");

        lessonEndAnswerAction.handle(ivrRequest, request, response);

        verify(milestonesRepository, times(1)).markLastMilestoneFinish(callerId);
    }

    @Test
    public void shouldNavigateToTheLastChapterIfUserAnswers1() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "1");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);
        assertEquals(nextAction, "forward:/chapter/"+chapter.getNumber()+"/lesson/"+currentLesson.getNumber());
    }

    @Test
    public void shouldNavigateToTheNextChapterIfUserAnswers2() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "2");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);
        assertEquals(nextAction, "forward:/chapter/"+chapter.getNumber()+"/lesson/"+ (currentLesson.getNumber()+1) );
    }

    @Test
    public void shouldNavigateToTheEndLessonMenuIfUserAnswerIsInvalid() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "4");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);
        assertEquals(nextAction, "forward:/lessonEndMenu");
    }
}