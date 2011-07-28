package org.motechproject.bbcwt.ivr.action;


import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
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

        String HELP_OPTION_TEXT ="Please press 1 for Help, 2 for starting Chapters.";

        when(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS)).thenReturn(HELP_OPTION_TEXT);

        String endAction = action.handle(ivrRequest,request,response);

        verify(messages, atMost(1)).get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS);
        verify(ivrDtmfBuilder, atMost(1)).withPlayText(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS);
        verify(ivrResponseBuilder, atMost(1)).withCollectDtmf(collectDtmf);
        verify(session, atLeastOnce()).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/helpMenuAnswer");
    }
}