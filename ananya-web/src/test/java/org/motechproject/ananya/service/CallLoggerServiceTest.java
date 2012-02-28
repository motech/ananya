package org.motechproject.ananya.service;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallDuration;
import org.motechproject.ananya.domain.CallEvent;
import org.motechproject.ananya.domain.CallFlow;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.repository.AllCallLogs;

import java.util.ArrayList;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

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

    @Test
    public void shouldSaveCallLogForJobAidStartEvent() {
        final DateTime start = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.JOBAID_START, start.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(start, CallFlow.JOBAID, null);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
    }

    @Test
    public void shouldSaveCallLogForJobAidEndEvent() {
        final DateTime end = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.JOBAID_END, end.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(null, CallFlow.JOBAID, end);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
    }

    @Test
    public void shouldSaveAllCallLogWithUpdatedEndTimeForDisconnectEvent() {
        final DateTime end = new DateTime(2011,1,1,1,10);
        DateTime callStart = new DateTime(2011, 1, 1, 1, 1);
        DateTime regStart = new DateTime(2011, 1, 1, 1, 5);
        DateTime regEnd = new DateTime(2011, 1, 1, 1, 6);

        ArrayList<CallLog> callLogs = new ArrayList<CallLog>() {};
        callLogs.add(new CallLog("callId", "callerId", CallFlow.CALL, callStart,null));
        callLogs.add(new CallLog("callId", "callerId", CallFlow.REGISTRATION, regStart, regEnd));

        stub(allCallLogs.findByCallId("callId")).toReturn(callLogs);

        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.DISCONNECT, end.getMillis()));

        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher(callStart, CallFlow.CALL, end)));
    }

    private Matcher<CallLog> callLogMatcher(final DateTime startTime, final CallFlow callFlowall, final DateTime endTime) {
        return new BaseMatcher<CallLog>() {
                @Override
                public boolean matches(Object o) {
                    CallLog o1 = (CallLog) o;
                    return ((o1.getStartTime() == null && startTime == null) ||o1.getStartTime().equals(startTime))
                        && o1.getCallFlow() == callFlowall
                        && ((o1.getEndTime() == null && endTime == null) || o1.getEndTime().equals(endTime))
                        && o1.getCallId() == "callId";
                }

                @Override
                public void describeTo(Description description) {
                }
            };
    }

}
