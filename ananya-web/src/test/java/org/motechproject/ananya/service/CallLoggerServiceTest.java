package org.motechproject.ananya.service;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallDuration;
import org.motechproject.ananya.domain.CallEvent;
import org.motechproject.ananya.domain.CallFlow;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.repository.AllCallLogs;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@Ignore
public class CallLoggerServiceTest {
    private CallLoggerService callLoggerService;
    @Mock
    private AllCallLogs allCallLogs;

    @Before
    public void setUp() {
        initMocks(this);
        callLoggerService = new CallLoggerService(allCallLogs);
    }

    @Test
    public void shouldSaveCallLogForCallStartEvent() {
        final DateTime start = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.CALL_START, start.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(start, CallFlow.CALL, null);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
    }

    @Test
    public void shouldSaveCallLogForCallEndEvent() {
        final DateTime end = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.CALL_END, end.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(null, CallFlow.CALL, end);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
    }

    @Test
    public void shouldSaveCallLogForRegStartEvent() {
        final DateTime start = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.REGISTRATION_START, start.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(start, CallFlow.REGISTRATION, null);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
    }

    @Test
    public void shouldSaveCallLogForRegEndEvent() {
        final DateTime end = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.REGISTRATION_END, end.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(null, CallFlow.REGISTRATION, end);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
    }

    @Test
    public void shouldSaveCallLogForCCStartEvent() {
        final DateTime start = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.CERTIFICATECOURSE_START, start.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(start, CallFlow.CERTIFICATECOURSE, null);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
    }

    @Test
    public void shouldSaveCallLogForCCEndEvent() {
        final DateTime end = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.CERTIFICATECOURSE_END, end.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(null, CallFlow.CERTIFICATECOURSE, end);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
    }

    private Matcher<CallLog> callLogMatcher(final DateTime startTime, final CallFlow callFlowall, final DateTime endTime) {
        return new BaseMatcher<CallLog>() {
                @Override
                public boolean matches(Object o) {
                    CallLog o1 = (CallLog) o;
                    return o1.getStartTime()== startTime
                        && o1.getCallFlow() == callFlowall
                        && o1.getEndTime() == endTime
                        && o1.getCallId() == "callId";
                }

                @Override
                public void describeTo(Description description) {
                }
            };
    }

}
