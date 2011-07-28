package org.motechproject.bbcwt.ivr.action;


import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class HelpMenuActionTest extends BaseActionTest {

    private HelpMenuAction action;

    @Mock
    private IVRDtmfBuilder ivrDtmfBuilder;

    @Mock
    private IVRResponseBuilder ivrResponseBuilder;

    @Mock
    private CollectDtmf collectDtmf;

    @Mock
    private Response kookooResponse;

    @Before
    public void setUp(){
        initMocks(this);
        action = new HelpMenuAction(messages);
    }

    @Test
    public void shouldBuildHelpMenu(){
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, null, null);

        String HELP_OPTION_TEXT ="Please press 1 for Help, 2 for starting Chapters.";

        when(request.getAttribute(IVR.Attributes.DTMF_BUILDER)).thenReturn(ivrDtmfBuilder);
        when(request.getAttribute(IVR.Attributes.RESPONSE_BUILDER)).thenReturn(ivrResponseBuilder);
        when(request.getSession()).thenReturn(session);

        when(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS)).thenReturn(HELP_OPTION_TEXT);

        when(ivrDtmfBuilder.withPlayText(Matchers.<String>any())).thenReturn(ivrDtmfBuilder);
        when(ivrDtmfBuilder.create()).thenReturn(collectDtmf);
        when(ivrResponseBuilder.withCollectDtmf(Matchers.<CollectDtmf>any())).thenReturn(ivrResponseBuilder);
        when(ivrResponseBuilder.create()).thenReturn(kookooResponse);

        String endAction = action.handle(ivrRequest,request,response);

        verify(messages, atMost(1)).get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS);
        verify(ivrDtmfBuilder, atMost(1)).withPlayText(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS);
        verify(ivrResponseBuilder, atMost(1)).withCollectDtmf(collectDtmf);
        verify(session, atLeastOnce()).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/helpMenuAnswer");
    }
}