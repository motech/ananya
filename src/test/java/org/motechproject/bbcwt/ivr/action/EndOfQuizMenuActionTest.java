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
        final String IVR_TO_BE_PLAYED_AFTER_QUIZ = "options.wav";
        when(messages.get(IVRMessage.END_OF_QUIZ_OPTIONS)).thenReturn(IVR_TO_BE_PLAYED_AFTER_QUIZ);
        endOfQuizMenuAction.handle(new IVRRequest(), request, response);
        verify(ivrDtmfBuilder).withPlayAudio(CONTENT_LOCATION + IVR_TO_BE_PLAYED_AFTER_QUIZ);
        verify(ivrDtmfBuilder).create();
        verify(ivrResponseBuilder).withCollectDtmf(collectDtmf);
    }

    @Test
    public void shouldSetTheNextInteractionToBeEndOfQuizMenuAnswerAction() {
        endOfQuizMenuAction.handle(new IVRRequest(), request, response);
        verify(session).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/endOfQuizMenuAnswer");
    }
}