package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.IvrFlow;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;

import java.util.Arrays;
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
    public void setup(){
        initMocks(this);
        callDurationMeasureService = new CallDurationMeasureService(callLoggerService,reportDB,allFrontLineWorkerDimensions);
    }

    @Test
    public void shouldSaveCallDurationsForACallId(){
        String callId = "callId";
        Long callerId = 123456789L;
        CallLog callCallLog = new CallLog(callId, callerId.toString(), IvrFlow.CALL, DateTime.now(), DateTime.now().plusMinutes(2).plusSeconds(10));
        List<CallLog> callLogs = Arrays.asList(callCallLog);
        when(callLoggerService.getAllCallLogs(callId)).thenReturn(callLogs);
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(callerId, "", "anganwadi-worker", "Registered" );
        when(allFrontLineWorkerDimensions.fetchFor(callerId)).thenReturn(frontLineWorkerDimension);

        callDurationMeasureService.createCallDurationMeasure(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(reportDB).add(captor.capture());
        verify(allFrontLineWorkerDimensions,never()).getOrMakeFor(anyLong(), anyString(), anyString(), anyString());
        CallDurationMeasure callDurationMeasure = captor.getValue();
        assertEquals(frontLineWorkerDimension, callDurationMeasure.getFrontLineWorkerDimension());
        assertEquals(callId, callDurationMeasure.getCallId());
        assertEquals(130, callDurationMeasure.getDuration());
        verify(callLoggerService).delete(callLogs);
    }

    @Test
    public void shouldCreateFLWDimensionAndThenSaveCallDurationMeasureIfFLWDimensionDoesNotExist(){
        String callId = "callId";
        Long callerId = 123456789L;
        List<CallLog> callLogs = Arrays.asList(new CallLog(callId, callerId.toString(), IvrFlow.CALL, DateTime.now(), DateTime.now().plusMinutes(2).plusSeconds(10)));
        when(callLoggerService.getAllCallLogs(callId)).thenReturn(callLogs);
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(callerId, "", "", "" );
        when(allFrontLineWorkerDimensions.fetchFor(callerId)).thenReturn(null);
        when(allFrontLineWorkerDimensions.getOrMakeFor(callerId, "", "", "")).thenReturn(frontLineWorkerDimension);

        callDurationMeasureService.createCallDurationMeasure(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(reportDB).add(captor.capture());
        CallDurationMeasure callDurationMeasure = captor.getValue();
        assertEquals(frontLineWorkerDimension, callDurationMeasure.getFrontLineWorkerDimension());
        assertEquals(callId, callDurationMeasure.getCallId());
        assertEquals(130, callDurationMeasure.getDuration());
        assertEquals(IvrFlow.CALL.name(), callDurationMeasure.getType());
        verify(callLoggerService).delete(callLogs);
    }

    @Test
    public void shouldNotSaveCallDurationMeasureWhenDurationDataISIncorrect(){
        String callId = "callId";
        Long callerId = 123456789L;
        List<CallLog> callLogs = Arrays.asList(new CallLog(callId, callerId.toString(), IvrFlow.CALL, DateTime.now(), null));
        when(callLoggerService.getAllCallLogs(callId)).thenReturn(callLogs);

        callDurationMeasureService.createCallDurationMeasure(callId);

        verify(reportDB, never()).add(any());
        verify(allFrontLineWorkerDimensions, never()).fetchFor(anyLong());
        verify(callLoggerService).delete(callLogs);
    }

    @Test
    public void shouldSaveCallDurationsForMultipleCallFlows(){
        String callId = "callId";
        Long callerId = 123456789L;
        CallLog callLog1 = new CallLog(callId, callerId.toString(), IvrFlow.CALL, DateTime.now(), DateTime.now().plusMinutes(2).plusSeconds(10));
        CallLog callLog2 = new CallLog(callId, callerId.toString(), IvrFlow.JOBAID, DateTime.now(), DateTime.now().plusMinutes(2).plusSeconds(10));
        List<CallLog> callLogs = Arrays.asList(callLog1, callLog2);

        when(callLoggerService.getAllCallLogs(callId)).thenReturn(callLogs);
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(callerId, "", "anganwadi-worker", "Registered" );
        when(allFrontLineWorkerDimensions.fetchFor(callerId)).thenReturn(frontLineWorkerDimension);

        callDurationMeasureService.createCallDurationMeasure(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(reportDB, times(2)).add(captor.capture());
    }
}
