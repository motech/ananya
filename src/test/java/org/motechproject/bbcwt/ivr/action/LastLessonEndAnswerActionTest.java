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
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);

        IVRRequest ivrRequest = new IVRRequest(null, null, null, "1");

        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals("Should navigate to the last lesson heard from the current chapter", nextAction,
                "forward:/chapter/" + chapter.getNumber() + "/lesson/" + lesson.getNumber());
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldNavigateToStartQuizIfOptionChosenIs2(){
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);

        IVRRequest ivrRequest = new IVRRequest(null, null, null, "2");

        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals("Should navigate to the the first question after end of chapter.", nextAction,
                     "forward:/startQuiz");
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldPlayHelpIfUserResponseIsAsterisk() {
        final String IVR_HELP_AUDIO = "ivr_help_audio.wav";
        when(messages.get(IVRMessage.IVR_HELP)).thenReturn(IVR_HELP_AUDIO);
        when(messages.absoluteFileLocation(IVR_HELP_AUDIO)).thenReturn(CONTENT_LOCATION + IVR_HELP_AUDIO);

        IVRRequest ivrRequest = new IVRRequest(null, null, null, "%");
        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + IVR_HELP_AUDIO);
    }

    @Test
    public void afterHelpShouldForwardToLessonEndMenu() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "%");
        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);
        assertEquals(nextAction, "forward:/lessonEndMenu");
    }

    @Test
    public void shouldPlayInvalidInputMessageAndNavigateToLessonEndMenuIfOptionChosenIsInvalid() {
        int invalidInputCountBeforeThisInput = 1;
        setInvalidInputCountBeforeThisInputAs(invalidInputCountBeforeThisInput);
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "3");

        final String INVALID_INPUT_WAV = "invalid_input.wav";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(INVALID_INPUT_WAV);
        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + INVALID_INPUT_WAV);

        assertEquals("Should navigate to the the lesson end menu.", nextAction,
                     "forward:/lessonEndMenu");
        verifyInvalidInputCountHasBeenIncremented(invalidInputCountBeforeThisInput);
    }

    @Test
    public void shouldForwardToNextChapterIfInvalidInputIsGivenMoreThanPermissibleNumberOfTimes() {
        setInvalidInputCountBeforeThisInputAs(Integer.parseInt(ALLOWED_NUMBER_OF_INVALID_INPUT));
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "3");

        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals("Should navigate to the next chapter.", nextAction,
                     "forward:/startNextChapter");
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldNavigateToLessonEndMenuIfNoInputIsGiven() {
        int noInputCountBeforeThisInput = 1;
        setNoInputCountBeforeThisInputAs(noInputCountBeforeThisInput);

        IVRRequest ivrRequest = new IVRRequest(null, null, null, "");

        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals("Should navigate to the the lesson end menu.", nextAction,
                     "forward:/lessonEndMenu");
        verifyNoInputCountHasBeenIncremented(noInputCountBeforeThisInput);
    }

    @Test
    public void shouldForwardToNextChapterIfNoInputIsGivenMoreThanPermissibleNumberOfTimes() {
        setNoInputCountBeforeThisInputAs(Integer.parseInt(ALLOWED_NUMBER_OF_NO_INPUT));

        IVRRequest ivrRequest = new IVRRequest(null, null, null, "");

        String nextAction = chapterEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals("Should navigate to the next chapter.", nextAction,
                     "forward:/startNextChapter");
        verifyInvalidAndNoInputCountsAreReset();
    }

    private void setInvalidInputCountBeforeThisInputAs(int invalidInputCountBeforeThisInput) {
        when(session.getAttribute(IVR.Attributes.INVALID_INPUT_COUNT)).thenReturn(invalidInputCountBeforeThisInput);
    }

    private void setNoInputCountBeforeThisInputAs(int noInputCountBeforeThisInput) {
        when(session.getAttribute(IVR.Attributes.NO_INPUT_COUNT)).thenReturn(noInputCountBeforeThisInput);
    }

    private void verifyInvalidAndNoInputCountsAreReset() {
        verify(session).setAttribute(IVR.Attributes.INVALID_INPUT_COUNT, 0);
        verify(session).setAttribute(IVR.Attributes.NO_INPUT_COUNT, 0);
    }

    private void verifyInvalidInputCountHasBeenIncremented(int invalidInputCountBeforeThisInput) {
        verify(session).setAttribute(IVR.Attributes.INVALID_INPUT_COUNT, invalidInputCountBeforeThisInput+1);
    }

    private void verifyNoInputCountHasBeenIncremented(int noInputCountBeforeThisInput) {
        verify(session).setAttribute(IVR.Attributes.NO_INPUT_COUNT, noInputCountBeforeThisInput+1);
    }
}