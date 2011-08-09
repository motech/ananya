package org.motechproject.bbcwt.ivr.action;

import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EndOfQuizMenuActionTest extends BaseActionTest {

    private EndOfQuizMenuAction endOfQuizMenuAction;

    @Test
    public void shouldRenderTheEndOfQuizMenu() {
        final String IVR_TO_BE_PLAYED_AFTER_QUIZ = "IVR to be played after quiz.";
        when(messages.get(IVRMessage.END_OF_QUIZ_PTIONS)).thenReturn(IVR_TO_BE_PLAYED_AFTER_QUIZ);
        endOfQuizMenuAction = new EndOfQuizMenuAction(messages);
        endOfQuizMenuAction.handle(new IVRRequest(), request, response);
        verify(ivrResponseBuilder).addPlayText(IVR_TO_BE_PLAYED_AFTER_QUIZ);
    }
}