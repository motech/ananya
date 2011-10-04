package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public class PostIntroductionMenuAnswerActionTest extends BaseActionTest {
    private PostIntroductionMenuAnswerAction action;

    @Before
    public void setUp() {
        action = new PostIntroductionMenuAnswerAction(messages);
    }

    @Test
    public void shouldPlayLessonWhenOptionChosenIs2() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);

        String userInput = "2";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", "9999988888", IVR.Event.GOT_DTMF.key(), userInput);

        String chainedAction = action.handle(ivrRequest, request, response);

        assertEquals("The action should be chained to chapter to play the chapter.", "forward:/chapter/1/lesson/1", chainedAction);
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldPlayIntroductionBackWhenOptionChosenIs1() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);

        String userInput = "1";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, IVR.Event.GOT_DTMF.key(), userInput);

        String chainedAction = action.handle(ivrRequest, request, response);

        assertEquals("The action should be chained to produce the help menu again.", "forward:/introduction", chainedAction);

        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldPlayInvalidInputWhenInvalidOptionIsChosen() {
        final int invalidInputCountBeforeThisInput = 1;
        setInvalidInputCountBeforeThisInputAs(1);

        String userInput = "3";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, IVR.Event.GOT_DTMF.key(), userInput);

        final String INVALID_IP_MSG = "Invalid Input";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(INVALID_IP_MSG);

        String chainedAction = action.handle(ivrRequest, request, response);

        verify(messages, atLeastOnce()).get(IVRMessage.INVALID_INPUT);
        verify(ivrResponseBuilder, atLeastOnce()).addPlayAudio(CONTENT_LOCATION + INVALID_IP_MSG);

        assertEquals("The introduction should be played again in case of an invalid input.", "forward:/postIntroductionMenu", chainedAction);
        verifyInvalidInputCountHasBeenIncremented(invalidInputCountBeforeThisInput);
    }

    @Test
    public void shouldForwardToPlayLessonWhenMoreThanAllowedInvalidOptionsAreChosen() {
        setInvalidInputCountBeforeThisInputAs(Integer.parseInt(ALLOWED_NUMBER_OF_INVALID_INPUT));
        String invalidUserInput = "3";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, IVR.Event.GOT_DTMF.key(), invalidUserInput);

        String chainedAction = action.handle(ivrRequest, request, response);

        assertEquals("If Invalid input is chosen for more than allowed times, then forward to lesson", "forward:/chapter/1/lesson/1", chainedAction);
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldForwardToHelpMenuWhenNoOptionIsChosen() {
        final int noInputCountBeforeThisInput = 1;
        setNoInputCountBeforeThisInputAs(noInputCountBeforeThisInput);

        String noInput = "";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, IVR.Event.GOT_DTMF.key(), noInput);

        String chainedAction = action.handle(ivrRequest, request, response);

        assertEquals("The introduction should be played again in case of an invalid input.", "forward:/postIntroductionMenu", chainedAction);
        verifyNoInputCountHasBeenIncremented(noInputCountBeforeThisInput);
    }

    @Test
    public void shouldForwardToPlayLessonWhenNoOptionIsChosenForMoreThanAllowedTimes() {
        setNoInputCountBeforeThisInputAs(Integer.parseInt(ALLOWED_NUMBER_OF_NO_INPUT));
        String invalidUserInput = "";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, IVR.Event.GOT_DTMF.key(), invalidUserInput);

        String chainedAction = action.handle(ivrRequest, request, response);

        assertEquals("If no input is chosen for more than allowed times, then forward to lesson", "forward:/chapter/1/lesson/1", chainedAction);
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