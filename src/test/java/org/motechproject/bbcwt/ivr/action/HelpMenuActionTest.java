package org.motechproject.bbcwt.ivr.action;


import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class HelpMenuActionTest extends BaseActionTest {

    private HelpMenuAction action;

    @Before
    public void setUp(){
        action = new HelpMenuAction(messages);
    }

    @Test
    public void shouldBuildHelpMenu(){
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, null, null);

        String HELP_OPTION_TEXT ="start_course_option_prompt.wav";

        when(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS)).thenReturn(HELP_OPTION_TEXT);

        String endAction = action.handle(ivrRequest,request,response);

        verify(messages, times(1)).get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS);
        verify(ivrDtmfBuilder, times(1)).withPlayAudio(CONTENT_LOCATION + HELP_OPTION_TEXT);
        verify(ivrResponseBuilder, times(1)).withCollectDtmf(collectDtmf);
        verify(session).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/helpMenuAnswer");
    }

}