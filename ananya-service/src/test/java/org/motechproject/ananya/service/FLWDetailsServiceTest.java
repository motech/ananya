package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.exception.ValidationException;
import org.motechproject.ananya.mapper.FLWUsageResponseMapper;
import org.motechproject.ananya.request.FLWNighttimeCallsRequest;
import org.motechproject.ananya.response.FLWNighttimeCallsResponse;
import org.motechproject.ananya.response.FLWUsageResponse;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.response.CallDetailsResponse;
import org.motechproject.ananya.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
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
    private FLWUsageResponseMapper flwResponseMapper;
    @Mock
    private Properties ananyaProperties;

    private String nightStartTime = "19:00:00";
    private String nightEndTime = "06:59:59";

    @Before
    public void setup() {
        initMocks(this);
        when(ananyaProperties.getProperty("nighttime.calls.start.time")).thenReturn(nightStartTime);
        when(ananyaProperties.getProperty("nighttime.calls.end.time")).thenReturn(nightEndTime);
        flwDetailsService = new FLWDetailsService(frontLineWorkerService, callDurationMeasureService, locationService, smsReferenceService, flwResponseMapper);
    }

    @Test
    public void shouldReturnUsageDetailsForAValidMsisdn() {
        UUID flwId = UUID.randomUUID();

        Location defaultLocation = Location.getDefaultLocation();
        String msisdn = "911234567890";
        String language = "language";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "Name", Designation.ANM, defaultLocation, language, DateTime.now(), flwId);
        SMSReference smsReference = new SMSReference();
        CallDetailsResponse callDetailsResponse = new CallDetailsResponse(new ArrayList<CallUsageDetails>(), new ArrayList<CallDurationMeasure>(), new ArrayList<CallDurationMeasure>());
        FLWUsageResponse expectedFLWResponse = new FLWUsageResponse();
        when(frontLineWorkerService.findByCallerId(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(defaultLocation);
        when(callDurationMeasureService.getCallDetails(msisdn)).thenReturn(callDetailsResponse);
        when(smsReferenceService.getSMSReferenceNumber(msisdn)).thenReturn(smsReference);
        when(flwResponseMapper.mapUsageResponse(frontLineWorker, defaultLocation, callDetailsResponse, smsReference)).thenReturn(expectedFLWResponse);

        FLWUsageResponse actualResponse = flwDetailsService.getUsage(msisdn);

        assertEquals(expectedFLWResponse, actualResponse);
    }

    @Test
    public void shouldRaiseExceptionIfMsisdnIsInvalidForNighttimeCalls() {
        String msisdn = "msisdn";

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("unknown msisdn : " + msisdn);
        when(frontLineWorkerService.findByCallerId(msisdn)).thenReturn(null);

        FLWNighttimeCallsRequest nighttimeCallsRequest = new FLWNighttimeCallsRequest(msisdn, Channel.CONTACT_CENTER, DateUtils.parseLocalDate("14-12-2009"), DateUtils.parseLocalDate("15-12-2009"));
        flwDetailsService.getNighttimeCalls(nighttimeCallsRequest);
    }

    @Test
    public void shouldFetchNighttimeCalls() {
        String msisdn = "919900501221";
        String startDate = "14-12-2009";
        String endDate = "15-12-2009";
        String language = "language";
        final LocalDate startLocalDate = DateUtils.parseLocalDate(startDate);
        final LocalDate endLocalDate = DateUtils.parseLocalDate(endDate);
        when(frontLineWorkerService.findByCallerId(msisdn)).thenReturn(new FrontLineWorker(msisdn, "AIRTEL", null, language));
        FLWNighttimeCallsRequest nighttimeCallsRequest = new FLWNighttimeCallsRequest(msisdn, Channel.CONTACT_CENTER, DateUtils.parseLocalDate(startDate), DateUtils.parseLocalDate(endDate));

        List<JobAidCallDetails> jobAidCallDetailsList = new ArrayList<JobAidCallDetails>() {{
            add(new JobAidCallDetails(startLocalDate.toDateTime(LocalTime.now()), endLocalDate.toDateTime(LocalTime.now()), 1));
            add(new JobAidCallDetails(startLocalDate.toDateTime(LocalTime.now()), endLocalDate.toDateTime(LocalTime.now()), 1));
        }};
        FLWNighttimeCallsResponse nighttimeCallsResponse = mock(FLWNighttimeCallsResponse.class);

        when(frontLineWorkerService.findByCallerId(msisdn)).thenReturn(new FrontLineWorker(msisdn, "AIRTEL", null, language));
        when(callDurationMeasureService.getJobAidCallDurations(msisdn, startLocalDate, endLocalDate)).thenReturn(jobAidCallDetailsList);
        when(flwResponseMapper.mapNighttimeCallsResponse(jobAidCallDetailsList)).thenReturn(nighttimeCallsResponse);

        assertEquals(nighttimeCallsResponse, flwDetailsService.getNighttimeCalls(nighttimeCallsRequest));
    }
}
