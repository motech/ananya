package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallFlowType;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.CallLogItem;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;

import java.sql.Timestamp;
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
    @Mock
    private AllTimeDimensions allTimeDimensions;

    @Mock
    private AllRegistrationMeasures allRegistrationMeasures;

    private String callId;
    private long callerId;
    private int flwId;
    private TimeDimension timeDimension;
    private LocationDimension locationDimension;
    private FrontLineWorkerDimension frontLineWorkerDimension;
    private RegistrationMeasure registrationMeasure;

    @Before
    public void setup() {
        initMocks(this);
        callDurationMeasureService = new CallDurationMeasureService(callLoggerService, reportDB, allFrontLineWorkerDimensions, allRegistrationMeasures, allTimeDimensions);
        callId = "callId";
        callerId = 123456789L;
        flwId = 1;
        timeDimension = new TimeDimension(DateTime.now());
        locationDimension = new LocationDimension("", "", "", "");
        frontLineWorkerDimension = new FrontLineWorkerDimension(callerId, "","", "anganwadi-worker", "ANGANWADI", "Registered");
        frontLineWorkerDimension.setId(flwId);
        registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, callId);
    }

    @Test
    public void shouldSaveCallDurationsForACallId() {
        CallLog callLog = new CallLog(callId, String.valueOf(callerId), "321");
        DateTime now = DateTime.now();
        callLog.addItem(new CallLogItem(CallFlowType.CERTIFICATECOURSE, now, now.plusSeconds(10)));

        when(callLoggerService.getCallLogFor(callId)).thenReturn(callLog);
        when(allFrontLineWorkerDimensions.fetchFor(callerId)).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flwId)).thenReturn(registrationMeasure);
        when(allTimeDimensions.getFor(now)).thenReturn(timeDimension);

        callDurationMeasureService.createFor(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(reportDB).add(captor.capture());

        CallDurationMeasure callDurationMeasure = captor.getValue();
        assertEquals(frontLineWorkerDimension, callDurationMeasure.getFrontLineWorkerDimension());
        assertEquals(timeDimension, callDurationMeasure.getTimeDimension());
        assertEquals(callId, callDurationMeasure.getCallId());
        assertEquals(10, callDurationMeasure.getDuration());
        assertEquals(CallFlowType.CERTIFICATECOURSE.toString(), callDurationMeasure.getType());
        verify(callLoggerService).delete(callLog);
    }

    @Test
    public void shouldNotSaveCallDurationMeasureWhenDurationDataISIncorrect() {
        String callId = "callId";
        Long callerId = 123456789L;
        CallLog callLog = new CallLog(callId, callerId.toString(), "321");
        when(callLoggerService.getCallLogFor(callId)).thenReturn(callLog);

        callDurationMeasureService.createFor(callId);

        verify(reportDB, never()).add(any());
        verify(allFrontLineWorkerDimensions, never()).fetchFor(anyLong());
        verify(callLoggerService).delete(callLog);
    }

    @Test
    public void shouldSaveCallDurationsForMultipleCallFlows() {
        DateTime now = DateTime.now();
        DateTime callStartTime = now;
        DateTime callEndTime = now.plusSeconds(20);
        DateTime certificateCourseEndTime = now.plusSeconds(15);
        DateTime certificateCourseStartTime = now.plusSeconds(5);
        String calledNumber = "321";
        LocationDimension locationDimension = new LocationDimension("","","","");
        TimeDimension timeDimension = new TimeDimension(DateTime.now());
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(callerId, "","", "anganwadi-worker", "ANGANWADI", "Registered");
        frontLineWorkerDimension.setId(flwId);
        RegistrationMeasure registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, callId);

        CallLog callLog = new CallLog(callId, String.valueOf(callerId), calledNumber);

        callLog.addItem(new CallLogItem(CallFlowType.CALL, callStartTime, callEndTime));
        callLog.addItem(new CallLogItem(CallFlowType.CERTIFICATECOURSE, certificateCourseStartTime, certificateCourseEndTime));
        when(callLoggerService.getCallLogFor(callId)).thenReturn(callLog);
        when(allFrontLineWorkerDimensions.fetchFor(callerId)).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flwId)).thenReturn(registrationMeasure);


        callDurationMeasureService.createFor(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(reportDB, times(2)).add(captor.capture());
        List<CallDurationMeasure> callDurationMeasures = captor.getAllValues();
        assertEquals(2,callDurationMeasures.size());

        CallDurationMeasure callDurationMeasureForCall = callDurationMeasures.get(0);
        assertEquals(20, callDurationMeasureForCall.getDuration());
        assertEquals(callId, callDurationMeasureForCall.getCallId());
        assertEquals(Long.valueOf(calledNumber), callDurationMeasureForCall.getCalledNumber());
        assertEquals(CallFlowType.CALL.toString(), callDurationMeasureForCall.getType());
        assertEquals(frontLineWorkerDimension, callDurationMeasureForCall.getFrontLineWorkerDimension());

        CallDurationMeasure callDurationMeasureForCourse = callDurationMeasures.get(1);
        assertEquals(10, callDurationMeasureForCourse.getDuration());
        assertEquals(callId, callDurationMeasureForCourse.getCallId());
        assertEquals(CallFlowType.CERTIFICATECOURSE.toString(), callDurationMeasureForCourse.getType());
        assertEquals(frontLineWorkerDimension, callDurationMeasureForCourse.getFrontLineWorkerDimension());
    }

    @Test
    public void shouldSaveCallStartAndEndTime() {
        final DateTime startTime = DateTime.now();
        final DateTime endTime = DateTime.now().plusMinutes(4);
        CallLog callLog = new CallLog(callId, String.valueOf(callerId), "321");
        callLog.addItem(new CallLogItem(CallFlowType.CALL, startTime, endTime));

        when(callLoggerService.getCallLogFor(callId)).thenReturn(callLog);
        when(allFrontLineWorkerDimensions.fetchFor(callerId)).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flwId)).thenReturn(registrationMeasure);

        callDurationMeasureService.createFor(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(reportDB).add(captor.capture());
        CallDurationMeasure callDurationMeasure = captor.getValue();

        assertEquals(new Timestamp(startTime.getMillis()), callDurationMeasure.getStartTime());
        assertEquals(new Timestamp(endTime.getMillis()), callDurationMeasure.getEndTime());
    }

    @Test
    public void shouldNotCreateCallDurationMeasureIfCallLogIsNull() {
        String callId = "callId";
        when(callLoggerService.getCallLogFor(callId)).thenReturn(null);

        callDurationMeasureService.createFor(callId);

        verify(reportDB, never()).add(any());
        verify(allFrontLineWorkerDimensions, never()).fetchFor(anyLong());
        verify(callLoggerService, never()).delete(any(CallLog.class));
    }
}
