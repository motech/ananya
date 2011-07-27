package org.motechproject.bbcwt.ivr.action.event;

import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.action.NewCallEventAction;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class NewCallEventActionTest extends BaseActionTest {
    private NewCallEventAction action;
    @Mock
    private HealthWorkersRepository healthWorkers;
    @Mock
    private HealthWorker healthWorker;

    @Before
    public void setUp() {
        initMocks(this);
        action = new NewCallEventAction(messages, healthWorkers);
    }

    @Test
    public void shouldWelcomeNewUserAndRegisterCallerId() {
        String callerId = "9898982323";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", callerId, IVR.Event.NEW_CALL.key(), "Data");

        when(request.getSession()).thenReturn(session);
        when(healthWorkers.findByCallerId(ivrRequest.getCid())).thenReturn(null);
        when(messages.get("wc.msg.new.user")).thenReturn("Welcome. This is the first time you are accessing FLW Training course.");
        when(messages.get("msg.new.user.options")).thenReturn("Please press 1 for Help, 2 for starting Chapters.");

        String ivrResponse = action.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVR.Attributes.CALLER_ID, ivrRequest.getCid());
        verify(healthWorkers).findByCallerId(ivrRequest.getCid());
        verify(healthWorkers).add(argThat(new HealthWorkerCallerIdMatcher(callerId)));

        assertTrue("IVR Response should contain welcome message for new user.", ivrResponse.contains("Welcome. This is the first time you are accessing FLW Training course."));
        assertTrue("Collect Dtmf should be present", ivrResponse.contains("collectdtmf"));
        assertTrue("Collect Dtmf prompt should be present", ivrResponse.contains("Please press 1 for Help, 2 for starting Chapters."));
    }

    @Test
    public void shouldWelcomeExistingUser() {
        String callerId = "9898982323";
        IVRRequest ivrRequest = new IVRRequest("unique-call-id", callerId, IVR.Event.NEW_CALL.key(), "Data");

        when(request.getSession()).thenReturn(session);
        when(healthWorkers.findByCallerId(ivrRequest.getCid())).thenReturn(healthWorker);
        when(messages.get("wc.msg.existing.user")).thenReturn("Welcome. Continue your FLW Training course.");


        String ivrResponse = action.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVR.Attributes.CALLER_ID, ivrRequest.getCid());
        verify(healthWorkers).findByCallerId(ivrRequest.getCid());

        assertTrue("IVR Response should contain welcome message for existing user.", ivrResponse.contains("Welcome. Continue your FLW Training course."));
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
