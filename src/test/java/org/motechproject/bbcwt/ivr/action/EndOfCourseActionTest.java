package org.motechproject.bbcwt.ivr.action;

import org.junit.Test;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class EndOfCourseActionTest extends BaseActionTest {
    @Test
    public void shouldPlayTheCongratulatoryCourseMessage() {
        EndOfCourseAction endOfCourseAction = new EndOfCourseAction(messages);

        final String MSG_COURSE_COMPLETION = "You have completed the course. Congratulations.";
        when(messages.get(IVRMessage.MSG_COURSE_COMPLETION)).thenReturn(MSG_COURSE_COMPLETION);

        endOfCourseAction.handle(new IVRRequest(), request, response);

        verify(ivrResponseBuilder).addPlayText(MSG_COURSE_COMPLETION);
        verify(ivrResponseBuilder).withHangUp();
    }
}