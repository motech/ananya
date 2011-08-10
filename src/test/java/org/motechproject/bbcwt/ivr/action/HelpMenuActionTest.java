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

        final String HELP_OPTION_TEXT ="HelpMenu.wav";
        final String CONTENT_LOCATION = "http://localhost/bbcwt/audio/";

        when(messages.get(IVRMessage.CONTENT_LOCATION)).thenReturn(CONTENT_LOCATION);
        when(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS)).thenReturn(HELP_OPTION_TEXT);

        String endAction = action.handle(ivrRequest,request,response);

        verify(messages, times(1)).get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS);
        verify(ivrDtmfBuilder, times(1)).withPlayAudio(CONTENT_LOCATION.concat(HELP_OPTION_TEXT));
        verify(ivrResponseBuilder, times(1)).withCollectDtmf(collectDtmf);
        verify(session).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/helpMenuAnswer");
    }

}