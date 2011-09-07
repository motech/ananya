package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.MilestonesRepository;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class LastLessonEndAnswerActionTest extends BaseActionTest {

    private LastLessonEndAnswerAction chapterEndAnswerAction;

    @Mock
    private MilestonesRepository milestonesRepository;

    private String callerId;
    private HealthWorker healthWorker;
    private Chapter chapter;
    private Lesson lesson;
    private Milestone lastMilestoneWithLinkedReferences;

    @Before
    public void setUp() {
        callerId = "9989989980";
        healthWorker = new HealthWorker(callerId);
        chapter = new Chapter(1);
        chapter.setId("chapter-1-unique-id");
        lesson = new Lesson(1, "http://somewhere/lesson/1", "http://somwhere/lesson/1/endMenu");
        chapter.addLesson(lesson);

        lastMilestoneWithLinkedReferences = new Milestone(healthWorker.getId(), chapter.getId(), lesson.getId(), null, new Date());
        lastMilestoneWithLinkedReferences.setChapter(chapter);
        lastMilestoneWithLinkedReferences.setHealthWorker(healthWorker);

        chapterEndAnswerAction = new LastLessonEndAnswerAction(milestonesRepository, messages);

        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(callerId);
        when(milestonesRepository.currentMilestoneWithLinkedReferences(callerId)).thenReturn(lastMilestoneWithLinkedReferences);
    }

    @Test
    public void shouldNavigateUserToLastLessonHeardIfOptionChosenIs1() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "1");

        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals("Should navigate to the last lesson heard from the current chapter", nextAction,
                "forward:/chapter/" + chapter.getNumber() + "/lesson/" + lesson.getNumber());
    }

    @Test
    public void shouldNavigateToStartQuizIfOptionChosenIs2(){
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "2");

        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals("Should navigate to the the first question after end of chapter.", nextAction,
                     "forward:/startQuiz");
    }

    @Test
    public void shouldPlayInvalidInputMessageAndNavigateToLessonEndMenuIfOptionChosenIsInvalid() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "3");

        final String INVALID_INPUT_WAV = "invalid_input.wav";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(INVALID_INPUT_WAV);
        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + INVALID_INPUT_WAV);

        assertEquals("Should navigate to the the lesson end menu.", nextAction,
                     "forward:/lessonEndMenu");
    }

}