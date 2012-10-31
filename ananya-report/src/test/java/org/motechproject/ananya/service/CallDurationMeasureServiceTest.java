package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.CallFlowType;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.CallLogItem;
import org.motechproject.ananya.domain.CallUsageDetails;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllCallDurationMeasures;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.response.CallDetailsResponse;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallDurationMeasureServiceTest {
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private AllCallDurationMeasures allCallDurationMeasures;
    @Mock
    private CallLogService callLoggerService;
    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Mock
    private AllTimeDimensions allTimeDimensions;
    @Mock
    private AllRegistrationMeasures allRegistrationMeasures;
    @Mock
    private LocationDimensionService locationDimensionService;
    @Mock
    private OperatorService operatorService;

    @Captor
    private ArgumentCaptor<List<CallDurationMeasure>> captor;
    private String callId = "callId";
    private long callerId = 123456789L;
    private int flwId = 1;
    private TimeDimension timeDimension = new TimeDimension(DateTime.now());
    private LocationDimension locationDimension = new LocationDimension();
    private FrontLineWorkerDimension frontLineWorkerDimension;
    private RegistrationMeasure registrationMeasure;

    @Before
    public void setup() {
        initMocks(this);
        frontLineWorkerDimension = new FrontLineWorkerDimension(callerId, "airtel", "", "anganwadi-worker", "ANGANWADI", "Registered", UUID.randomUUID());
        frontLineWorkerDimension.setId(flwId);
        registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, callId);
        callDurationMeasureService = new CallDurationMeasureService(callLoggerService, allCallDurationMeasures, allFrontLineWorkerDimensions, allRegistrationMeasures, allTimeDimensions, locationDimensionService, operatorService);
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
        when(operatorService.usageInPulse(frontLineWorkerDimension.getOperator(), 10000)).thenReturn(1);

        callDurationMeasureService.createFor(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(allCallDurationMeasures).add(captor.capture());

        CallDurationMeasure callDurationMeasure = captor.getValue();
        assertEquals(frontLineWorkerDimension, callDurationMeasure.getFrontLineWorkerDimension());
        assertEquals(timeDimension, callDurationMeasure.getTimeDimension());
        assertEquals(callId, callDurationMeasure.getCallId());
        assertEquals(10, callDurationMeasure.getDuration());
        assertEquals(1, (int) callDurationMeasure.getDurationInPulse());
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

        verify(allCallDurationMeasures, never()).add(any(CallDurationMeasure.class));
        verify(allFrontLineWorkerDimensions, never()).fetchFor(anyLong());
        verify(operatorService, never()).usageInPulse(anyString(), anyInt());
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
        LocationDimension locationDimension = new LocationDimension("", "", "", "", "VALID");
        TimeDimension timeDimension = new TimeDimension(DateTime.now());
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension(callerId, "airtel", "", "anganwadi-worker", "ANGANWADI", "Registered", UUID.randomUUID());
        frontLineWorkerDimension.setId(flwId);
        RegistrationMeasure registrationMeasure = new RegistrationMeasure(frontLineWorkerDimension, locationDimension, timeDimension, callId);

        CallLog callLog = new CallLog(callId, String.valueOf(callerId), calledNumber);

        callLog.addItem(new CallLogItem(CallFlowType.CALL, callStartTime, callEndTime));
        callLog.addItem(new CallLogItem(CallFlowType.CERTIFICATECOURSE, certificateCourseStartTime, certificateCourseEndTime));
        when(callLoggerService.getCallLogFor(callId)).thenReturn(callLog);
        when(allFrontLineWorkerDimensions.fetchFor(callerId)).thenReturn(frontLineWorkerDimension);
        when(allRegistrationMeasures.fetchFor(flwId)).thenReturn(registrationMeasure);
        when(operatorService.usageInPulse(frontLineWorkerDimension.getOperator(), 10000)).thenReturn(1);
        when(operatorService.usageInPulse(frontLineWorkerDimension.getOperator(), 20000)).thenReturn(1);

        callDurationMeasureService.createFor(callId);

        ArgumentCaptor<CallDurationMeasure> captor = ArgumentCaptor.forClass(CallDurationMeasure.class);
        verify(allCallDurationMeasures, times(2)).add(captor.capture());
        List<CallDurationMeasure> callDurationMeasures = captor.getAllValues();
        assertEquals(2, callDurationMeasures.size());

        CallDurationMeasure callDurationMeasureForCall = callDurationMeasures.get(0);
        assertEquals(20, callDurationMeasureForCall.getDuration());
        assertEquals(1, (int) callDurationMeasureForCall.getDurationInPulse());
        assertEquals(callId, callDurationMeasureForCall.getCallId());
        assertEquals(Long.valueOf(calledNumber), callDurationMeasureForCall.getCalledNumber());
        assertEquals(CallFlowType.CALL.toString(), callDurationMeasureForCall.getType());
        assertEquals(frontLineWorkerDimension, callDurationMeasureForCall.getFrontLineWorkerDimension());

        CallDurationMeasure callDurationMeasureForCourse = callDurationMeasures.get(1);
        assertEquals(10, callDurationMeasureForCourse.getDuration());
        assertEquals(1, (int) callDurationMeasureForCourse.getDurationInPulse());
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
        verify(allCallDurationMeasures).add(captor.capture());
        CallDurationMeasure callDurationMeasure = captor.getValue();

        assertEquals(new Timestamp(startTime.getMillis()), callDurationMeasure.getStartTime());
        assertEquals(new Timestamp(endTime.getMillis()), callDurationMeasure.getEndTime());
    }

    @Test
    public void shouldNotCreateCallDurationMeasureIfCallLogIsNull() {
        String callId = "callId";
        when(callLoggerService.getCallLogFor(callId)).thenReturn(null);

        callDurationMeasureService.createFor(callId);

        verify(allCallDurationMeasures, never()).add(any(CallDurationMeasure.class));
        verify(allFrontLineWorkerDimensions, never()).fetchFor(anyLong());
        verify(callLoggerService, never()).delete(any(CallLog.class));
    }

    @Test
    public void shouldUpdateLocationForAllMeasuresForCallerId() {
        String locationId = "location_id";
        final CallDurationMeasure expectedCallDurationMeasure = new CallDurationMeasure();
        when(allCallDurationMeasures.findByCallerId(callerId)).thenReturn(new ArrayList<CallDurationMeasure>() {{
            add(expectedCallDurationMeasure);
        }});
        LocationDimension expectedLocationDimension = new LocationDimension();
        when(locationDimensionService.getFor(locationId)).thenReturn(expectedLocationDimension);

        callDurationMeasureService.updateLocation(callerId, locationId);

        verify(allCallDurationMeasures).updateAll(captor.capture());
        List<CallDurationMeasure> actualCallDurationMeasures = captor.getValue();
        assertEquals(1, actualCallDurationMeasures.size());
        assertEquals(expectedLocationDimension, actualCallDurationMeasures.get(0).getLocationDimension());
    }

    @Test
    public void shouldGetFlwUsageDetails() {
        String msisdn = "2134567890";
        ArrayList<CallUsageDetails> jobAidCallDetails = new ArrayList<CallUsageDetails>() {{
            add(new CallUsageDetails(123L, 213L, 2012, 1));
        }};
        when(allCallDurationMeasures.getCallUsageDetailsByMonthAndYear(Long.parseLong(msisdn))).thenReturn(jobAidCallDetails);

        CallDetailsResponse callDetails = callDurationMeasureService.getCallDetails(msisdn);

        assertEquals(jobAidCallDetails, callDetails.getCallUsageDetailsList());
    }

    @Test
    public void shouldGetRecentJobAidCallDetails() {
        String msisdn = "2134567890";
        ArrayList<CallDurationMeasure> callDurationMeasures = new ArrayList<>();
        when(allCallDurationMeasures.getRecentJobAidCallDetails(Long.parseLong(msisdn))).thenReturn(callDurationMeasures);

        CallDetailsResponse callDetails = callDurationMeasureService.getCallDetails(msisdn);

        assertEquals(callDurationMeasures, callDetails.getRecentJobAidCallDetailsList());
    }

    @Test
    public void shouldGetRecentCertificateCourseCallDetails() {
        String msisdn = "2134567890";
        ArrayList<CallDurationMeasure> callDurationMeasures = new ArrayList<>();
        when(allCallDurationMeasures.getRecentCertificateCourseCallDetails(Long.parseLong(msisdn))).thenReturn(callDurationMeasures);

        CallDetailsResponse callDetails = callDurationMeasureService.getCallDetails(msisdn);

        assertEquals(callDurationMeasures, callDetails.getRecentCertificateCourseCallDetailsList());
    }

    @Test
    public void shouldUpdateCallDurationMeasuresWithNewLocation() {
        String newLocationId = "newLocationId";
        String oldLocationId = "oldLocationId";
        ArrayList<CallDurationMeasure> callDurationMeasures = new ArrayList<>();
        callDurationMeasures.add(new CallDurationMeasure(null, new LocationDimension(oldLocationId, null, null, null, "VALID"), null, null, null, 10, DateTime.now(), DateTime.now(), null, 0));
        when(locationDimensionService.getFor(newLocationId)).thenReturn(new LocationDimension(newLocationId, null, null, null, "VALID"));
        when(allCallDurationMeasures.findByLocationId(oldLocationId)).thenReturn(callDurationMeasures);

        callDurationMeasureService.updateLocation(oldLocationId, newLocationId);

        verify(allCallDurationMeasures).updateAll(captor.capture());
        List<CallDurationMeasure> callDurationMeasureList = captor.getValue();
        assertEquals(1, callDurationMeasureList.size());
        assertEquals(newLocationId, callDurationMeasureList.get(0).getLocationDimension().getLocationId());
    }
}
