package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewCallActionTest extends BaseActionTest {
    private NewCallAction action;
    @Mock
    private HealthWorkersRepository healthWorkers;
    @Mock
    private HealthWorker healthWorker;

    @Before
    public void setUp() {
        action = new NewCallAction(messages, healthWorkers);
    }

    public void shouldWelcomeNewUser() {
        String callerId = "9898982323";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", callerId, IVR.Event.NEW_CALL.key(), "Data");

        when(healthWorkers.findByCallerId(ivrRequest.getCid())).thenReturn(null);
        final String NEW_USR_MSG = "welcome_new_user.wav";
        when(messages.get("wc.msg.new.user")).thenReturn(NEW_USR_MSG);

        String nextAction = action.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVR.Attributes.CALLER_ID, callerId);
        verify(healthWorkers).findByCallerId(ivrRequest.getCid());
        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + NEW_USR_MSG);

        assertEquals("The next action chained in case of new user should be helpMenu", "forward:/helpMenu", nextAction);
    }

    public void shouldWelcomeExistingUser() {
        String callerId = "9898982323";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", callerId, IVR.Event.NEW_CALL.key(), "Data");

        when(healthWorkers.findByCallerId(ivrRequest.getCid())).thenReturn(healthWorker);
        when(messages.get("wc.msg.existing.user")).thenReturn("Welcome. Continue your FLW Training course.");


        String nextAction = action.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVR.Attributes.CALLER_ID, callerId);
        verify(healthWorkers).findByCallerId(ivrRequest.getCid());

        assertEquals("The next action chained in case of existing user should be /existingUserMenu.", "forward:/existingUserMenu", nextAction);
    }

}
