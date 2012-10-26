package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.exceptions.FLWDoesNotExistException;
import org.motechproject.ananya.mapper.FrontLineWorkerUsageResponseMapper;
import org.motechproject.ananya.response.FrontLineWorkerUsageResponse;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.response.CallDetailsResponse;

import java.util.ArrayList;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FLWDetailsServiceTest {
    private FLWDetailsService flwDetailsService;
    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private LocationService locationService;
    @Mock
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private SMSReferenceService smsReferenceService;
    @Mock
    private FrontLineWorkerUsageResponseMapper flwResponseMapper;

    private UUID flwGuid = UUID.randomUUID();

    @Before
    public void setup(){
        initMocks(this);
        flwDetailsService = new FLWDetailsService(frontLineWorkerService, callDurationMeasureService, locationService, smsReferenceService, flwResponseMapper);
    }

    @Test
    public void shouldRaiseExceptionIfGuidIsInvalid() {
        expectedException.expect(FLWDoesNotExistException.class);
        expectedException.expectMessage("Unknown flw id: " + flwGuid);
        when(frontLineWorkerService.findByFlwGuid(flwGuid)).thenReturn(null);

        flwDetailsService.getUsageData(flwGuid.toString());
    }

    @Test
    public void shouldReturnUsageDetailsForAValidFlwGuid() {
        Location defaultLocation = Location.getDefaultLocation();
        String msisdn = "911234567890";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "Name", Designation.ANM, defaultLocation, DateTime.now(), flwGuid);
        SMSReference smsReference = new SMSReference();
        CallDetailsResponse callDetailsResponse = new CallDetailsResponse(new ArrayList<CallUsageDetails>(), new ArrayList<CallDurationMeasure>(), new ArrayList<CallDurationMeasure>());
        FrontLineWorkerUsageResponse expectedFLWResponse = new FrontLineWorkerUsageResponse();
        when(frontLineWorkerService.findByFlwGuid(flwGuid)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(defaultLocation);
        when(callDurationMeasureService.getCallDetails(msisdn)).thenReturn(callDetailsResponse);
        when(smsReferenceService.getSMSReferenceNumber(msisdn)).thenReturn(smsReference);
        when(flwResponseMapper.mapFrom(frontLineWorker,defaultLocation,callDetailsResponse,smsReference)).thenReturn(expectedFLWResponse);

        FrontLineWorkerUsageResponse actualResponse = flwDetailsService.getUsageData(flwGuid.toString());

        assertEquals(expectedFLWResponse,actualResponse);
    }
}
