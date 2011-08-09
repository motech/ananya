package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EndOfQuizMenuActionTest extends BaseActionTest {

    private EndOfQuizMenuAction endOfQuizMenuAction;

    @Before
    public void setup() {
        endOfQuizMenuAction = new EndOfQuizMenuAction(messages);
    }

    @Test
    public void shouldRenderTheEndOfQuizMenu() {
        final String IVR_TO_BE_PLAYED_AFTER_QUIZ = "IVR to be played after quiz.";
        when(messages.get(IVRMessage.END_OF_QUIZ_PTIONS)).thenReturn(IVR_TO_BE_PLAYED_AFTER_QUIZ);
        endOfQuizMenuAction.handle(new IVRRequest(), request, response);
        verify(ivrResponseBuilder).addPlayText(IVR_TO_BE_PLAYED_AFTER_QUIZ);
    }

    @Test
    public void shouldSetTheNextInteractionToBeEndOfQuizMenuAnswerAction() {
        endOfQuizMenuAction.handle(new IVRRequest(), request, response);
        verify(session).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/endOfQuizMenuAnswer");
    }
}