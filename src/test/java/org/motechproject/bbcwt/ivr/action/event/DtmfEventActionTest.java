package org.motechproject.bbcwt.ivr.action.event;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DtmfEventActionTest extends BaseActionTest {
    private DtmfEventAction action;

    @Before
    public void setUp() {
        initMocks(this);
        action = new DtmfEventAction(messages);
    }

    @Test
    public void shouldPlayHelpWhenOptionChosenIs1() {
        String userInput = "1";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", "9999988888", IVR.Event.GOT_DTMF.key(), userInput);

        when(messages.get("msg.help")).thenReturn("Some Help.");

        String ivrResponse = action.handle(ivrRequest, request, response);

        assertTrue("Help should be played if user selects 1.", ivrResponse.contains("Some Help."));
    }

    @Test
    public void shouldPlayHelpWhenOptionChosenIs2() {
        String userInput = "2";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", "9999988888", IVR.Event.GOT_DTMF.key(), userInput);

        when(messages.get("content.chapter1")).thenReturn("Content of Chapter1.");

        String ivrResponse = action.handle(ivrRequest, request, response);

        assertTrue("Chapter should be played if user selects 2.", ivrResponse.contains("Content of Chapter1."));
    }

}