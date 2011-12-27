package org.motechproject.bbcwt.ivr.action;


import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class IntroductionActionTest extends BaseActionTest {

    private IntroductionAction action;

    @Before
    public void setUp(){
        action = new IntroductionAction(messages);
    }

    @Test
    public void shouldBuildIntroductionMenu(){
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, null, null);

        final String BLANK_AUDIO_FILE = "blank_audio_file.wav";
        final String COURSE_INTRO_AUDIO = "course_introduction.wav";
        when(messages.get(IVRMessage.BLANK_AUDIO_FILE)).thenReturn(BLANK_AUDIO_FILE);
        when(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_WC_MESSAGE)).thenReturn(COURSE_INTRO_AUDIO);

        String chainedAction = action.handle(ivrRequest,request,response);

        verify(ivrResponseBuilder, times(1)).addPlayAudio(CONTENT_LOCATION + BLANK_AUDIO_FILE);
        verify(ivrResponseBuilder, times(1)).addPlayAudio(CONTENT_LOCATION + COURSE_INTRO_AUDIO);
        assertEquals("The next action chained should be PostIntroductionMenuAction", chainedAction, "forward:/postIntroductionMenu");
    }

}