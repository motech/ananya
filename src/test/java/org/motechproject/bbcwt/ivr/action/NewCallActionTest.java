package org.motechproject.bbcwt.ivr.action;

import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
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
    public void shouldWelcomeNewUserAndRegisterCallerId() {
        String callerId = "9898982323";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", callerId, IVR.Event.NEW_CALL.key(), "Data");

        when(healthWorkers.findByCallerId(ivrRequest.getCid())).thenReturn(null);
        when(messages.get("wc.msg.new.user")).thenReturn("Welcome. This is the first time you are accessing FLW Training course.");

        String nextAction = action.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVR.Attributes.CALLER_ID, callerId);
        verify(healthWorkers).findByCallerId(ivrRequest.getCid());
        verify(healthWorkers).add(argThat(new HealthWorkerCallerIdMatcher(callerId)));
        verify(ivrResponseBuilder).addPlayText("Welcome. This is the first time you are accessing FLW Training course.");

        assertEquals("The next action chained in case of new user should be helpMenu", "forward:/helpMenu", nextAction);
    }

    @Test
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

    public static class HealthWorkerCallerIdMatcher extends ArgumentMatcher<HealthWorker> {
        private String callerIdToMatch;
        private String calledWith;

        public HealthWorkerCallerIdMatcher(String callerIdToMatch) {
            this.callerIdToMatch = callerIdToMatch;
        }

        @Override
        public boolean matches(Object arg) {
            if(arg instanceof HealthWorker) {
                HealthWorker arg1 = (HealthWorker) arg;
                calledWith = arg1.getCallerId();
                return calledWith.matches(callerIdToMatch);
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Expected the health worker to have caller ID: " + callerIdToMatch + " but has been called with: " + calledWith);
        }
    }

}
