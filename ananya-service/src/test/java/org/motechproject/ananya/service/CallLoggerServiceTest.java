package org.motechproject.ananya.service;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllCallLogs;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLoggerServiceTest {
    private CallLoggerService callLoggerService;

    @Mock
    private AllCallLogs allCallLogs;

    @Mock
    private ReportPublisherService reportDataPublisher;

    @Before
    public void setUp() {
        initMocks(this);
        callLoggerService = new CallLoggerService(allCallLogs, reportDataPublisher);
    }

    @Test
    public void shouldSaveCallLogForCallStartEvent() {
        final DateTime start = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.CALL_START, start.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(start, IvrFlow.CALL, null);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
        verifyNoMoreInteractions(reportDataPublisher);
    }

    @Test
    public void shouldSaveCallLogForRegStartEvent() {
        final DateTime start = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.REGISTRATION_START, start.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(start, IvrFlow.REGISTRATION, null);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
        verifyNoMoreInteractions(reportDataPublisher);
    }

    @Test
    public void shouldSaveCallLogForRegEndEvent() {
        final DateTime end = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.REGISTRATION_END, end.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(null, IvrFlow.REGISTRATION, end);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
        verifyNoMoreInteractions(reportDataPublisher);
    }

    @Test
    public void shouldSaveCallLogForCCStartEvent() {
        final DateTime start = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.CERTIFICATECOURSE_START, start.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(start, IvrFlow.CERTIFICATECOURSE, null);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
        verifyNoMoreInteractions(reportDataPublisher);
    }

    @Test
    public void shouldSaveCallLogForCCEndEvent() {
        final DateTime end = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.CERTIFICATECOURSE_END, end.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(null, IvrFlow.CERTIFICATECOURSE, end);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
        verifyNoMoreInteractions(reportDataPublisher);
    }

    @Test
    public void shouldSaveCallLogForJobAidStartEvent() {
        final DateTime start = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.JOBAID_START, start.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(start, IvrFlow.JOBAID, null);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
        verifyNoMoreInteractions(reportDataPublisher);
    }

    @Test
    public void shouldSaveCallLogForJobAidEndEvent() {
        final DateTime end = DateTime.now();
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.JOBAID_END, end.getMillis()));

        Matcher<CallLog> callLogMatcher = callLogMatcher(null, IvrFlow.JOBAID, end);
        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher));
        verifyNoMoreInteractions(reportDataPublisher);
    }

    @Test
    public void shouldSaveAllCallLogWithUpdatedEndTimeForDisconnectEvent() {
        final DateTime end = new DateTime(2011,1,1,1,10);
        DateTime callStart = new DateTime(2011, 1, 1, 1, 1);
        DateTime regStart = new DateTime(2011, 1, 1, 1, 5);
        DateTime regEnd = new DateTime(2011, 1, 1, 1, 6);

        ArrayList<CallLog> callLogs = new ArrayList<CallLog>() {};
        callLogs.add(new CallLog("callId", "callerId", IvrFlow.CALL, callStart,null));
        callLogs.add(new CallLog("callId", "callerId", IvrFlow.REGISTRATION, regStart, regEnd));

        stub(allCallLogs.findByCallId("callId")).toReturn(callLogs);

        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.DISCONNECT, end.getMillis()));

        verify(allCallLogs).addOrUpdate(argThat(callLogMatcher(callStart, IvrFlow.CALL, end)));
    }

    @Test
    public void shouldPublishCallDurationEventForDisconnectEvent(){
        stub(allCallLogs.findByCallId("callId")).toReturn(new ArrayList<CallLog>() {});
        callLoggerService.save(new CallDuration("callId", "callerId", CallEvent.DISCONNECT, DateTime.now().getMillis()));

        ArgumentCaptor<LogData> captor = ArgumentCaptor.forClass(LogData.class);
        verify(reportDataPublisher).publishCallDuration(captor.capture());

        LogData logData = captor.getValue();
        assertEquals("callId", logData.getDataId());
    }

    @Test
    public void shouldGetAllCallLogsForAGivenCallId(){
        String callid = "callid";
        Collection<CallLog> callLogs = new ArrayList<CallLog>();
        CallLog mockCallLog = new CallLog("callId", "callerId", IvrFlow.CALL, DateTime.now(), DateTime.now());
        callLogs.add(mockCallLog);
        when(allCallLogs.findByCallId(callid)).thenReturn(callLogs);

        Collection<CallLog> allCallLogsForCallId = callLoggerService.getAllCallLogs(callid);

        assertEquals(1, allCallLogsForCallId.size());
        assertTrue(allCallLogsForCallId.contains(mockCallLog));
    }

    @Test
    public void shouldDeleteAllGivenCallLogs(){
        Collection<CallLog> callLogs = new ArrayList<CallLog>();
        CallLog mockCallLog = new CallLog("callId", "callerId", IvrFlow.CALL, DateTime.now(), DateTime.now());
        callLogs.add(mockCallLog);

        callLoggerService.delete(callLogs);

        verify(allCallLogs).delete(callLogs);
    }

    private Matcher<CallLog> callLogMatcher(final DateTime startTime, final IvrFlow ivrFlowall, final DateTime endTime) {
        return new BaseMatcher<CallLog>() {
                @Override
                public boolean matches(Object o) {
                    CallLog o1 = (CallLog) o;
                    return ((o1.getStartTime() == null && startTime == null) ||o1.getStartTime().equals(startTime))
                        && o1.getIvrFlow() == ivrFlowall
                        && ((o1.getEndTime() == null && endTime == null) || o1.getEndTime().equals(endTime))
                        && o1.getCallId() == "callId";
                }

                @Override
                public void describeTo(Description description) {
                }
            };
    }

}
