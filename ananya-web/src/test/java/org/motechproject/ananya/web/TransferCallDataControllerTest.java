package org.motechproject.ananya.web;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallDuration;
import org.motechproject.ananya.domain.CallEvent;
import org.motechproject.ananya.repository.AllCallLogs;
import org.motechproject.ananya.service.CallLoggerService;
import org.motechproject.ananya.service.CertificateCourseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class TransferCallDataControllerTest {

    private TransferCallDataController controller;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AllCallLogs allCallLogs;
    @Mock
    private HttpSession session;
    @Mock
    private CallLoggerService loggerService;
    @Mock
    private CertificateCourseService certificateCourseService;


    @Before
    public void setUp() {
        initMocks(this);
        controller = new TransferCallDataController(loggerService, certificateCourseService);
    }

    @Test
    public void shouldSaveACallLogForCallStartEvent() throws Exception {
        String callId = "123";
        String callerId = "456";

        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("callerId")).thenReturn(callerId);

        when(request.getParameter("dataToPost")).thenReturn("[{\"token\":\"0\",\"type\":\"callDuration\",\"data\":'{\"time\":1330320462000,\"callEvent\":\"CALL_START\"}'}]");

        String s = controller.receiveIVRData(request);

        verify(loggerService).save(argThat(callDurationMatcher("123", "456",CallEvent.CALL_START, 1330320462000L )));
    }

    @Test
    public void shouldSaveACallLogForRegistrationStartAndEndEvent() throws Exception {
        String callId = "123";
        String callerId = "456";

        when(request.getParameter("callId")).thenReturn(callId);
        when(request.getParameter("callerId")).thenReturn(callerId);

        when(request.getParameter("dataToPost")).thenReturn("[{\"token\":\"0\",\"type\":\"callDuration\",\"data\":'{\"time\":1330320462000,\"callEvent\":\"REGISTRATION_START\"}'},{\"token\":\"1\",\"type\":\"callDuration\",\"data\":'{\"time\":1330320480000,\"callEvent\":\"REGISTRATION_END\"}'}]");

        String s = controller.receiveIVRData(request);

        verify(loggerService).save(argThat(callDurationMatcher("123", "456",CallEvent.REGISTRATION_START, 1330320462000L )));
        verify(loggerService).save(argThat(callDurationMatcher("123", "456",CallEvent.REGISTRATION_END, 1330320480000L )));
    }

    private Matcher<CallDuration> callDurationMatcher(final String callId, final String callerId, final CallEvent callEvent, final long time) {
        return new BaseMatcher<CallDuration>() {
            @Override
            public boolean matches(Object o) {
                CallDuration o1 = (CallDuration) o;
                return o1.getCallId() == callId &&
                        o1.getCallerId() == callerId &&
                        o1.getCallEvent() == callEvent &&
                        o1.getTime() == time;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}
