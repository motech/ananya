package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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

public class LessonEndAnswerActionTest extends BaseActionTest {

    private LessonEndAnswerAction lessonEndAnswerAction;

    @Mock
    private MilestonesRepository milestonesRepository;

    private String callerId;
    private Milestone currentMilestoneWithLinkedReferences;
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

        currentMilestoneWithLinkedReferences = new Milestone("unique-id-for-" + callerId, chapter.getId(), currentLesson.getId(), null, new Date());
        currentMilestoneWithLinkedReferences.setChapter(chapter);
        currentMilestoneWithLinkedReferences.setHealthWorker(healthWorker);

        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(callerId);
        when(milestonesRepository.currentMilestoneWithLinkedReferences(callerId)).thenReturn(currentMilestoneWithLinkedReferences);

        lessonEndAnswerAction = new LessonEndAnswerAction(milestonesRepository, messages);
    }

    @Test
    public void shouldNavigateToTheLastChapterIfUserAnswers1() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "1");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals(nextAction, "forward:/chapter/"+chapter.getNumber()+"/lesson/"+currentLesson.getNumber());
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldNavigateToTheNextChapterIfUserAnswers2() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "2");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals(nextAction, "forward:/chapter/"+chapter.getNumber()+"/lesson/"+ (currentLesson.getNumber()+1) );
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldPlayHelpIfUserResponseIsAsterisk() {
        final String IVR_HELP_AUDIO = "ivr_help_audio.wav";
        when(messages.get(IVRMessage.IVR_HELP)).thenReturn(IVR_HELP_AUDIO);
        when(messages.absoluteFileLocation(IVR_HELP_AUDIO)).thenReturn(CONTENT_LOCATION + IVR_HELP_AUDIO);

        IVRRequest ivrRequest = new IVRRequest(null, null, null, "%");
        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + IVR_HELP_AUDIO);
    }

    @Test
    public void afterHelpShouldForwardToLessonEndMenu() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "%");
        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);
        assertEquals(nextAction, "forward:/lessonEndMenu");
    }

    @Test
    public void shouldNavigateToTheEndLessonMenuIfUserAnswerIsInvalid() {
        int invalidInputCountBeforeThisInput = 1;
        setInvalidInputCountBeforeThisInputAs(1);
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "4");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals(nextAction, "forward:/lessonEndMenu");
        verifyInvalidInputCountHasBeenIncremented(invalidInputCountBeforeThisInput);
    }

    @Test
    public void shouldNavigateToNextLessonIfUserInputsInvalidKeysForMoreThanPermissibleTimes() {
        setInvalidInputCountBeforeThisInputAs(Integer.parseInt(ALLOWED_NUMBER_OF_INVALID_INPUT));
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "4");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals(nextAction, "forward:/chapter/"+chapter.getNumber()+"/lesson/"+ (currentLesson.getNumber()+1));
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldNavigateToTheEndLessonMenuIfThereIsNoUserInput() {
        int noInputCountBeforeThisInput = 1;
        setNoInputCountBeforeThisInputAs(noInputCountBeforeThisInput);
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals(nextAction, "forward:/lessonEndMenu");
        verifyNoInputCountHasBeenIncremented(noInputCountBeforeThisInput);
    }

    @Test
    public void shoudNavigateToNextLessonIfUserDoesNotGiveInputForMoreThanPermissibleTimes() {
        int noInputCountBeforeThisInput = Integer.parseInt(ALLOWED_NUMBER_OF_NO_INPUT);
        setNoInputCountBeforeThisInputAs(noInputCountBeforeThisInput);

        IVRRequest ivrRequest = new IVRRequest(null, null, null, "");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);

        assertEquals(nextAction, "forward:/chapter/"+chapter.getNumber()+"/lesson/"+ (currentLesson.getNumber()+1));
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