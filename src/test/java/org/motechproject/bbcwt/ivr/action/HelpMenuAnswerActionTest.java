package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

public class HelpMenuAnswerActionTest extends BaseActionTest {
    private HelpMenuAnswerAction action;

    @Before
    public void setUp() {
        action = new HelpMenuAnswerAction(messages);
    }

    @Test
    public void shouldPlayHelpWhenOptionChosenIs1() {
        String userInput = "1";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", "9999988888", IVR.Event.GOT_DTMF.key(), userInput);

        String chainedAction = action.handle(ivrRequest, request, response);

        assertEquals("The help menu answer action should be chained to chapter to play the chapter.", "forward:/chapter/1/lesson/1", chainedAction);
    }

    @Test
    public void shouldPlayHelpWhenOptionChosenIs2() {
        String userInput = "2";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, IVR.Event.GOT_DTMF.key(), userInput);

        String HELP_KEY = IVRMessage.IVR_HELP;
        String HELP_TEXT = "Some Help.";

        when(messages.get(HELP_KEY)).thenReturn(HELP_TEXT);

        String chainedAction = action.handle(ivrRequest, request, response);

        verify(messages, atLeastOnce()).get(HELP_KEY);
        verify(ivrResponseBuilder, atMost(1)).addPlayText(HELP_TEXT);

        assertEquals("The help menu action should be chained to produce the help menu again.", "forward:/helpMenu", chainedAction);
    }

    @Test
    public void shouldPlayInvalidInputWhenInvalidOptionIsChosen() {
        String userInput = "3";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, IVR.Event.GOT_DTMF.key(), userInput);

        final String INVALID_IP_MSG = "Invalid Input";
        when(messages.get(IVRMessage.INVALID_INPUT)).thenReturn(INVALID_IP_MSG);

        String chainedAction = action.handle(ivrRequest, request, response);

        verify(messages, atLeastOnce()).get(IVRMessage.INVALID_INPUT);
        verify(ivrResponseBuilder, atMost(1)).addPlayText(INVALID_IP_MSG);

        assertEquals("The help menu should be played again in case of an invalid input.", "forward:/helpMenu", chainedAction);
    }
}