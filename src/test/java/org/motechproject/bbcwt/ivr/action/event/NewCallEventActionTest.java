package org.motechproject.bbcwt.ivr.action.event;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NewCallEventActionTest extends BaseActionTest {
    private NewCallEventAction action;

    @Before
    public void setUp() {
        initMocks(this);
        action = new NewCallEventAction(messages);
    }

    @Test
    public void shouldInitiateASessionWithRequiredAttributesForANewCall() {
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", "9898982323", IVR.Event.NEW_CALL.key(), "Data");
        when(request.getSession()).thenReturn(session);

        action.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.COLLECT_PIN);
        verify(session).setAttribute(IVR.Attributes.CALL_ID, ivrRequest.getSid());
        verify(session).setAttribute(IVR.Attributes.CALLER_ID, ivrRequest.getCid());
    }

}
