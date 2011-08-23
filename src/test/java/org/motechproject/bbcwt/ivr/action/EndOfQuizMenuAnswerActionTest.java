package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

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
        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, "1"), request, response);
        assertThat(nextAction, is("forward:/startQuiz"));
    }

    @Test
    public void shouldForwardUserToNextChapterIfOptionPressedIs2() {
        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, "2"), request, response);
        assertThat(nextAction, is("forward:/startNextChapter"));
    }

    @Test
    public void shouldForwardUserToLastChapterIfOptionPressedIs3() {
        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, "3"), request, response);
        assertThat(nextAction, is("forward:/repeatLastChapter"));
    }

    @Test
    public void shouldForwardUserToEndOfQuizMenuIfInvalidInputIsPressed() {
        final String INVALID_INPUT = "invalid input";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(INVALID_INPUT);
        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, "*"), request, response);
        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + INVALID_INPUT);
        assertThat(nextAction, is("forward:/endOfQuizMenu"));
    }

    @Test
    public void shouldForwardUserToEndOfQuizMenuIfNoInputIsPressed() {
        final String INVALID_INPUT = "invalid input";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(INVALID_INPUT);
        String nextAction = endOfQuizMenuAnswerAction.handle(new IVRRequest(null, null, null, ""), request, response);
        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + INVALID_INPUT);
        assertThat(nextAction, is("forward:/endOfQuizMenu"));
    }
}