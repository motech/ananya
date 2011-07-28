package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class HelpMenuAnswerActionTest extends BaseActionTest {
    private HelpMenuAnswerAction action;
    @Mock
    private IVRResponseBuilder responseBuilder;

    @Before
    public void setUp() {
        initMocks(this);
        action = new HelpMenuAnswerAction(messages);
    }

    @Test
    public void shouldPlayHelpWhenOptionChosenIs1() {
        String userInput = "1";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, IVR.Event.GOT_DTMF.key(), userInput);

        String HELP_KEY = IVRMessage.IVR_HELP;
        String HELP_TEXT = "Some Help.";

        when(messages.get(HELP_KEY)).thenReturn(HELP_TEXT);
        when(request.getAttribute(IVR.Attributes.RESPONSE_BUILDER)).thenReturn(responseBuilder);

        String chainedAction = action.handle(ivrRequest, request, response);

        verify(messages, atLeastOnce()).get(HELP_KEY);
        verify(responseBuilder, atMost(1)).addPlayText(HELP_TEXT);

        assertEquals("The help menu action should be chained to produce the help menu again.", "forward:/helpMenu", chainedAction);
    }

    @Test
    public void shouldPlayHelpWhenOptionChosenIs2() {
        String userInput = "2";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", "9999988888", IVR.Event.GOT_DTMF.key(), userInput);

        String chainedAction = action.handle(ivrRequest, request, response);

        assertEquals("The help menu answer action should be chained to chapter to play the chapter.", "forward:/chapter", chainedAction);

    }
}