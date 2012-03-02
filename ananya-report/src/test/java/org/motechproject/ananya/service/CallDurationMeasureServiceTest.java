package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallFlow;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        CallLog mockCallLog = new CallLog(callId, callerId.toString(), CallFlow.CALL, DateTime.now(), DateTime.now().plusMinutes(2).plusSeconds(10));
        when(callLoggerService.getAllCallLogs(callId)).thenReturn(Arrays.asList(mockCallLog));
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(callerId, "", "anganwadi-worker", "Registered" );
        when(allFrontLineWorkerDimensions.fetchFor(callerId)).thenReturn(frontLineWorkerDimension);

        callDurationMeasureService.createCallDurationMeasure(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(reportDB).add(captor.capture());
        CallDurationMeasure callDurationMeasure = captor.getValue();
        assertEquals(frontLineWorkerDimension, callDurationMeasure.getFrontLineWorkerDimension());
        assertEquals(callId, callDurationMeasure.getCallId());
        assertEquals(130, callDurationMeasure.getDuration());

    }
}
