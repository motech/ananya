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

    @Test
    public void shouldForwardNewUserToIntroductionAction() {
        String callerId = "9898982323";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", callerId, IVR.Event.NEW_CALL.key(), "Data");

        when(healthWorkers.findByCallerId(ivrRequest.getCid())).thenReturn(null);

        String nextAction = action.handle(ivrRequest, request, response);

        assertEquals("The next action chained in case of new user should be helpMenu", "forward:/introduction", nextAction);
    }

    @Test
    public void shouldForwardAnExistingUserToTheExistingUserAction() {
        String callerId = "9898982323";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", callerId, IVR.Event.NEW_CALL.key(), "Data");

        when(healthWorkers.findByCallerId(ivrRequest.getCid())).thenReturn(healthWorker);

        String nextAction = action.handle(ivrRequest, request, response);

        assertEquals("The next action chained in case of existing user should be /existingUserMenu.", "forward:/existingUserHandler", nextAction);
    }

}
