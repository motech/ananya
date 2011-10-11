package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.powermock.api.mockito.PowerMockito;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EndOfQuizMenuAnswerActionTest extends BaseActionTest {

    private EndOfQuizMenuAnswerAction endOfQuizMenuAnswerAction;

    @Before
    public void setup() {
        endOfQuizMenuAnswerAction = new EndOfQuizMenuAnswerAction(messages);
    }

    @Test
    public void shouldForwardUserToQuizIfOptionPressedIs1() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);
        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, "1"), request, response);
        assertThat(nextAction, is("forward:/startQuiz"));
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldForwardUserToNextChapterIfOptionPressedIs2() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);
        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, "2"), request, response);
        assertThat(nextAction, is("forward:/startNextChapter"));
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldForwardUserToLastChapterIfOptionPressedIs3() {
        setInvalidInputCountBeforeThisInputAs(1);
        setNoInputCountBeforeThisInputAs(1);
        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, "3"), request, response);
        assertThat(nextAction, is("forward:/repeatLastChapter"));
        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldPlayHelpIfRequested() {
        final String IVR_HELP_AUDIO = "ivr_help_audio.wav";
        when(messages.get(IVRMessage.IVR_HELP)).thenReturn(IVR_HELP_AUDIO);
        when(messages.absoluteFileLocation(IVR_HELP_AUDIO)).thenReturn(CONTENT_LOCATION + IVR_HELP_AUDIO);

        IVRRequest ivrRequest = new IVRRequest(null, null, null, "*");

        String nextAction = endOfQuizMenuAnswerAction.handle(ivrRequest, request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + IVR_HELP_AUDIO);
    }

    @Test
    public void shouldFowardToEndOfQuizMenuAfterHelpHasBeenPlayed() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "*");

        String nextAction = endOfQuizMenuAnswerAction.handle(ivrRequest, request, response);

        assertThat(nextAction, is("forward:/endOfQuizMenu"));
    }

    @Test
    public void shouldForwardUserToEndOfQuizMenuIfInvalidInputIsPressed() {
        final int invalidInputCountBeforeThisInput = 1;
        setInvalidInputCountBeforeThisInputAs(invalidInputCountBeforeThisInput);

        final String INVALID_INPUT = "invalid input";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(INVALID_INPUT);

        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, "#"), request, response);

        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + INVALID_INPUT);
        assertThat(nextAction, is("forward:/endOfQuizMenu"));

        verifyInvalidInputCountHasBeenIncremented(invalidInputCountBeforeThisInput);
    }

    @Test
    public void shouldForwardUserToNextChapterIfInvalidInputIsPressedMoreThanPermissibleTimes() {
        setInvalidInputCountBeforeThisInputAs(Integer.parseInt(ALLOWED_NUMBER_OF_INVALID_INPUT));

        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, "#"), request, response);

        assertThat(nextAction, is("forward:/startNextChapter"));

        verifyInvalidAndNoInputCountsAreReset();
    }

    @Test
    public void shouldForwardUserToEndOfQuizMenuIfNoInputIsPressed() {
        final int noInputCountBeforeThisInput = 1;
        setNoInputCountBeforeThisInputAs(noInputCountBeforeThisInput);

        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, ""), request, response);
        assertThat(nextAction, is("forward:/endOfQuizMenu"));

        verifyNoInputCountHasBeenIncremented(noInputCountBeforeThisInput);
    }

    @Test
    public void shouldForwardUserToNextChapterIfNoInputIsGivenForMoreThanPermissibleTimes() {
        setNoInputCountBeforeThisInputAs(Integer.parseInt(ALLOWED_NUMBER_OF_NO_INPUT));

        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, ""), request, response);
        assertThat(nextAction, is("forward:/startNextChapter"));

        verifyInvalidAndNoInputCountsAreReset();
    }


    private void setInvalidInputCountBeforeThisInputAs(int invalidInputCountBeforeThisInput) {
        PowerMockito.when(session.getAttribute(IVR.Attributes.INVALID_INPUT_COUNT)).thenReturn(invalidInputCountBeforeThisInput);
    }

    private void setNoInputCountBeforeThisInputAs(int noInputCountBeforeThisInput) {
        PowerMockito.when(session.getAttribute(IVR.Attributes.NO_INPUT_COUNT)).thenReturn(noInputCountBeforeThisInput);
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