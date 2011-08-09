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
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class ChapterEndAnswerActionTest extends BaseActionTest {

    private ChapterEndAnswerAction chapterEndAnswerAction;

    @Mock
    private ChaptersRespository chaptersRespository;
    @Mock
    private MilestonesRepository milestonesRepository;

    private String callerId;
    private HealthWorker healthWorker;
    private Chapter chapter;
    private Lesson lesson;
    private Milestone lastMilestone;

    @Before
    public void setUp() {
        callerId = "9989989980";
        healthWorker = new HealthWorker(callerId);
        chapter = new Chapter(1);
        chapter.setId("chapter-1-unique-id");
        lesson = new Lesson(1, "http://somewhere/lesson/1");
        chapter.addLesson(lesson);

        lastMilestone = new Milestone(healthWorker.getId(), chapter.getId(), lesson.getId(), null, new Date());
        chapterEndAnswerAction = new ChapterEndAnswerAction(chaptersRespository, milestonesRepository, messages);

        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(callerId);
        when(milestonesRepository.markLastMilestoneFinish(callerId)).thenReturn(lastMilestone);
        when(chaptersRespository.get(lastMilestone.getChapterId())).thenReturn(chapter);
    }

    @Test
    public void shouldNavigateUserToLastLessonHeardIfOptionChosenIs1() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "1");

        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals("Should navigate to the last lesson heard from the current chapter", nextAction,
                     "forward:/chapter/"+chapter.getNumber()+"/lesson/"+lesson.getNumber());
    }

    @Test
    public void shouldMarkLastMilestoneAsCompleted() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "1");

        chapterEndAnswerAction.handle(ivrRequest, request, response);

        verify(milestonesRepository).markLastMilestoneFinish(callerId);
    }

    @Test
    public void shouldNavigateToStartQuizIfOptionChosenIs2(){
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "2");

        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals("Should navigate to the the first question after end of chapter.", nextAction,
                     "forward:/startQuiz");
    }

}