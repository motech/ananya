package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallFlowType;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.CallLogList;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallDurationMeasureServiceTest {
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private ReportDB reportDB;
    @Mock
    private CallLoggerService callLoggerService;
    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Before
    public void setup() {
        initMocks(this);
        callDurationMeasureService = new CallDurationMeasureService(callLoggerService, reportDB, allFrontLineWorkerDimensions);
    }

    @Test
    public void shouldSaveCallDurationsForACallId() {
        String callId = "callId";
        Long callerId = 123456789L;

        CallLogList callLogList = new CallLogList(callId, callerId.toString());
        DateTime now = DateTime.now();
        callLogList.add(new CallLog(CallFlowType.CERTIFICATECOURSE, now, now.plusSeconds(10)));
        when(callLoggerService.getCallLogList(callId)).thenReturn(callLogList);

        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(callerId, "", "anganwadi-worker", "Registered");
        when(allFrontLineWorkerDimensions.fetchFor(callerId)).thenReturn(frontLineWorkerDimension);

        callDurationMeasureService.createCallDurationMeasure(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(reportDB).add(captor.capture());

        CallDurationMeasure callDurationMeasure = captor.getValue();
        assertEquals(frontLineWorkerDimension, callDurationMeasure.getFrontLineWorkerDimension());
        assertEquals(callId, callDurationMeasure.getCallId());
        assertEquals(10, callDurationMeasure.getDuration());
        assertEquals(CallFlowType.CERTIFICATECOURSE.toString(), callDurationMeasure.getType());
        verify(callLoggerService).delete(callLogList);
    }

    @Test
    public void shouldNotSaveCallDurationMeasureWhenDurationDataISIncorrect() {
        String callId = "callId";
        Long callerId = 123456789L;
        CallLogList callLogList = new CallLogList(callId, callerId.toString());
        when(callLoggerService.getCallLogList(callId)).thenReturn(callLogList);

        callDurationMeasureService.createCallDurationMeasure(callId);

        verify(reportDB, never()).add(any());
        verify(allFrontLineWorkerDimensions, never()).fetchFor(anyLong());
        verify(callLoggerService).delete(callLogList);
    }

    @Test
    public void shouldSaveCallDurationsForMultipleCallFlows() {
        String callId = "callId";
        Long callerId = 123456789L;
        DateTime now = DateTime.now();
        DateTime callStartTime = now;
        DateTime callEndTime = now.plusSeconds(20);
        DateTime certificateCourseEndTime = now.plusSeconds(15);
        DateTime certificateCourseStartTime = now.plusSeconds(5);

        CallLogList callLogList = new CallLogList(callId, callerId.toString());

        callLogList.add(new CallLog(CallFlowType.CALL, callStartTime, callEndTime));
        callLogList.add(new CallLog(CallFlowType.CERTIFICATECOURSE, certificateCourseStartTime, certificateCourseEndTime));
        when(callLoggerService.getCallLogList(callId)).thenReturn(callLogList);

        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(callerId, "", "anganwadi-worker", "Registered");
        when(allFrontLineWorkerDimensions.fetchFor(callerId)).thenReturn(frontLineWorkerDimension);

        callDurationMeasureService.createCallDurationMeasure(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(reportDB, times(2)).add(captor.capture());
        List<CallDurationMeasure> callDurationMeasures = captor.getAllValues();
        assertEquals(2,callDurationMeasures.size());

        CallDurationMeasure callDurationMeasureForCall = callDurationMeasures.get(0);
        assertEquals(20, callDurationMeasureForCall.getDuration());
        assertEquals(callId, callDurationMeasureForCall.getCallId());
        assertEquals(CallFlowType.CALL.toString(), callDurationMeasureForCall.getType());
        assertEquals(frontLineWorkerDimension, callDurationMeasureForCall.getFrontLineWorkerDimension());

        CallDurationMeasure callDurationMeasureForCourse = callDurationMeasures.get(1);
        assertEquals(10, callDurationMeasureForCourse.getDuration());
        assertEquals(callId, callDurationMeasureForCourse.getCallId());
        assertEquals(CallFlowType.CERTIFICATECOURSE.toString(), callDurationMeasureForCourse.getType());
        assertEquals(frontLineWorkerDimension, callDurationMeasureForCourse.getFrontLineWorkerDimension());
    }
}
