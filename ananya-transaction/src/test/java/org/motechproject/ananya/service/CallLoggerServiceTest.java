package org.motechproject.ananya.service;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllCallLogList;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLoggerServiceTest {
    private CallLoggerService callLoggerService;

    @Mock
    private AllCallLogList allCallLogs;

    @Before
    public void setUp() {
        initMocks(this);
        callLoggerService = new CallLoggerService(allCallLogs);
    }

    @Test
    public void shouldSaveCallLogFromCallDurations() {
        final DateTime start = DateTime.now();

        CallDurationList callDurationList = new CallDurationList("callId", "callerId");
        List<CallDuration> callDurations = callDurationList.all();

        long callStartTime = start.getMillis();
        long courseStartTime = start.plusSeconds(2).getMillis();
        long courseEndTime = start.plus(10).getMillis();
        long callEndTime = start.plus(12).getMillis();

        callDurations.add(new CallDuration("callId", "callerId", CallEvent.CALL_START, callStartTime));
        callDurations.add(new CallDuration("callId", "callerId", CallEvent.CERTIFICATECOURSE_START, courseStartTime));
        callDurations.add(new CallDuration("callId", "callerId", CallEvent.CERTIFICATECOURSE_END, courseEndTime));
        callDurations.add(new CallDuration("callId", "callerId", CallEvent.DISCONNECT, callEndTime));

        callLoggerService.saveAll(callDurationList);

        ArgumentCaptor<CallLogList> captor = ArgumentCaptor.forClass(CallLogList.class);
        verify(allCallLogs).add(captor.capture());
        CallLogList callLogList = captor.getValue();

        assertEquals("callId", callLogList.getCallId());
        assertEquals("callerId", callLogList.getCallerId());

        List<CallLog> callLogs = callLogList.getCallLogs();
        assertEquals(2, callLogs.size());

        CallLog callLogForCallType = callLogs.get(0);
        assertEquals(CallFlowType.CALL, callLogForCallType.getCallFlowType());
        assertEquals(callStartTime, callLogForCallType.getStartTime().getMillis());
        assertEquals(callEndTime, callLogForCallType.getEndTime().getMillis());

        CallLog callLogForCourseType = callLogs.get(1);
        assertEquals(CallFlowType.CERTIFICATECOURSE, callLogForCourseType.getCallFlowType());
        assertEquals(courseStartTime, callLogForCourseType.getStartTime().getMillis());
        assertEquals(courseEndTime, callLogForCourseType.getEndTime().getMillis());

    }

    @Test
    public void shouldCallAllLogsRepoToDelete(){
        CallLogList callLogList = new CallLogList("123456","123");
        callLoggerService.delete(callLogList);
        verify(allCallLogs).remove(callLogList);
    }

    @Test
    public void shouldFetchCallLogListFromRepo(){
        String callId = "123456";
        String callerId = "123";
        CallLogList callLogList = new CallLogList(callId, callerId);
        when(allCallLogs.findByCallId(callId)).thenReturn(callLogList);

        CallLogList callLogListFromDB = callLoggerService.getCallLogList(callId);
        assertEquals(callLogList,callLogListFromDB);
    }


}
