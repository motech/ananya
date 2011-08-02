package org.motechproject.bbcwt.ivr.action;

import org.hamcrest.text.pattern.internal.ast.Exactly;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.when;

public class LessonEndMenuActionTest extends BaseActionTest {
    private LessonEndMenuAction lessonEndMenuAction;

    @Before
    public void setUp()
    {
        lessonEndMenuAction = new LessonEndMenuAction(messages);
    }

    @Test
    public void shouldBuildEndOfLessonMenu(){
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", null, null, null);

        String END_LESSON_OPTION_TEXT ="Please press 1 for repeating last lesson, 2 for next lesson.";

        when(messages.get(IVRMessage.END_OF_LESSON_MENU)).thenReturn(END_LESSON_OPTION_TEXT);

        String endAction = lessonEndMenuAction.handle(ivrRequest,request,response);

        verify(messages).get(IVRMessage.END_OF_LESSON_MENU);
        verify(ivrDtmfBuilder, times(1)).withPlayText(END_LESSON_OPTION_TEXT);
        verify(ivrResponseBuilder, times(1)).withCollectDtmf(collectDtmf);
        verify(session, times(1)).setAttribute(IVR.Attributes.NEXT_INTERACTION, "/lessonEndAnswer");
    }
}