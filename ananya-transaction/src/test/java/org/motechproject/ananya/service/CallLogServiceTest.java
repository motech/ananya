package org.motechproject.ananya.service;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllCallLogs;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLogServiceTest {
    private CallLogService callLoggerService;

    @Mock
    private AllCallLogs allCallLogs;

    @Before
    public void setUp() {
        initMocks(this);
        callLoggerService = new CallLogService(allCallLogs);
    }

    @Test
    public void shouldSaveCallLogFromCallDurations() {
        final DateTime start = DateTime.now();

        CallDurationList callDurationList = new CallDurationList("callId", "callerId", "calledNumber");
        List<CallDuration> callDurations = callDurationList.all();

        long callStartTime = start.getMillis();
        long courseStartTime = start.plusSeconds(2).getMillis();
        long courseEndTime = start.plus(10).getMillis();
        long callEndTime = start.plus(12).getMillis();

        callDurations.add(new CallDuration(CallEvent.CALL_START, callStartTime));
        callDurations.add(new CallDuration(CallEvent.CERTIFICATECOURSE_START, courseStartTime));
        callDurations.add(new CallDuration(CallEvent.CERTIFICATECOURSE_END, courseEndTime));
        callDurations.add(new CallDuration(CallEvent.DISCONNECT, callEndTime));

        callLoggerService.saveAll(callDurationList);

        ArgumentCaptor<CallLog> captor = ArgumentCaptor.forClass(CallLog.class);
        verify(allCallLogs).add(captor.capture());
        CallLog callLog = captor.getValue();

        assertEquals("callId", callLog.getCallId());
        assertEquals("callerId", callLog.getCallerId());

        List<CallLogItem> callLogs = callLog.getCallLogItems();
        assertEquals(2, callLogs.size());

        CallLogItem callLogForCallType = callLogs.get(0);
        assertEquals(CallFlowType.CALL, callLogForCallType.getCallFlowType());
        assertEquals(callStartTime, callLogForCallType.getStartTime().getMillis());
        assertEquals(callEndTime, callLogForCallType.getEndTime().getMillis());

        CallLogItem callLogForCourseType = callLogs.get(1);
        assertEquals(CallFlowType.CERTIFICATECOURSE, callLogForCourseType.getCallFlowType());
        assertEquals(courseStartTime, callLogForCourseType.getStartTime().getMillis());
        assertEquals(courseEndTime, callLogForCourseType.getEndTime().getMillis());

    }

    @Test
    public void shouldCallAllLogsRepoToDelete(){
        CallLog callLog = new CallLog("123456","123", "321");
        callLoggerService.delete(callLog);
        verify(allCallLogs).remove(callLog);
    }

    @Test
    public void shouldFetchCallLogFromRepo(){
        String callId = "123456";
        String callerId = "123";
        CallLog callLog = new CallLog(callId, callerId, "321");
        when(allCallLogs.findByCallId(callId)).thenReturn(callLog);

        CallLog callLogFromDB = callLoggerService.getCallLogFor(callId);
        assertEquals(callLog,callLogFromDB);
    }


}
