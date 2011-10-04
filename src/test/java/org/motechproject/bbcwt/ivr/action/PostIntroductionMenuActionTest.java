package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class PostIntroductionMenuActionTest extends BaseActionTest {
    private PostIntroductionMenuAction action;

    @Before
    public void setUp(){
        action = new PostIntroductionMenuAction(messages);
    }

    @Test
    public void shouldPlayThePostIntroductionMenu() {
        final String POST_INTRO_NAVIGATION_OPTIONS ="1_to_repeat_intro_2_to_go_to_chapters.wav";
        when(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS)).thenReturn(POST_INTRO_NAVIGATION_OPTIONS);

        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, null, null);
        action.handle(ivrRequest,request,response);

        verify(messages, times(1)).get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS);
        verify(ivrDtmfBuilder, times(1)).withPlayAudio(CONTENT_LOCATION + POST_INTRO_NAVIGATION_OPTIONS);
        verify(ivrResponseBuilder, times(1)).withCollectDtmf(collectDtmf);
    }

    @Test
    public void shouldSetTheNextActionToBePostIntroductionMenuAnswerAction() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, null, null);
        action.handle(ivrRequest,request,response);
        verify(session).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/postIntroductionMenuAnswerAction");
    }
}