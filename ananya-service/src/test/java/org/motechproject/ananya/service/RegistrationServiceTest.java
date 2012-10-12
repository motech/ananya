package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.FrontLineWorkerResponse;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.JobAidContentMeasureService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.motechproject.ananya.service.measure.SMSSentMeasureService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegistrationServiceTest {

    private RegistrationService registrationService;

    @Mock
    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private RegistrationMeasureService registrationMeasureService;
    @Mock
    private JobAidContentMeasureService jobAidContentMeasureService;
    @Mock
    private LocationService locationService;
    @Mock
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    @Mock
    private CourseItemMeasureService courseItemMeasureService;
    @Mock
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private SMSSentMeasureService smsSentMeasureService;

    @Before
    public void setUp() {
        initMocks(this);
        registrationService = new RegistrationService(
                frontLineWorkerService, courseItemMeasureService, frontLineWorkerDimensionService,
                registrationMeasureService, locationService, jobAidContentMeasureService,
                callDurationMeasureService, smsSentMeasureService);
    }

    @Test
    public void shouldSaveNewFLWAndPublishForReport() {
        String callerId = "919986574410";
        String name = "name";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        String flwGuid = "flwGuid";
        Designation designation = Designation.AWW;
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), new LocationRequest("district ", " block", "village"), null, flwGuid);
        when(locationService.findFor("district", "block", "village")).thenReturn(location);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, name, designation, location, null, flwGuid), location)).thenReturn(new FrontLineWorker(callerId, "operator", "bihar"));

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Created/Updated FLW record"));
        verify(frontLineWorkerService).createOrUpdate(new FrontLineWorker(callerId, name, designation, location, null, flwGuid), location);
        verify(registrationMeasureService).createOrUpdateFor(callerId);
    }

    @Test
    public void shouldUpdateAllMeasuresForExistingFLW() {
        String callerId = "919986574410";
        String name = "name";
        String flwGuid = "flwGuid";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        Designation designation = Designation.AWW;
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), new LocationRequest("district ", " block", "village"), null, flwGuid);
        when(locationService.findFor("district", "block", "village")).thenReturn(location);
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, "operator", Designation.ANM, location, null, flwGuid);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, name, designation, location, null, flwGuid), location)).thenReturn(frontLineWorker);

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Created/Updated FLW record"));
        verify(frontLineWorkerService).createOrUpdate(new FrontLineWorker(callerId, name, designation, location, null, flwGuid), location);
        verify(registrationMeasureService).createOrUpdateFor(callerId);
        verify(courseItemMeasureService).updateLocation(Long.parseLong(callerId), location.getExternalId());
        verify(jobAidContentMeasureService).updateLocation(Long.parseLong(callerId), location.getExternalId());
        verify(callDurationMeasureService).updateLocation(Long.parseLong(callerId), location.getExternalId());
        verify(smsSentMeasureService).updateLocation(Long.parseLong(callerId), location.getExternalId());
    }

    @Test
    public void shouldNotSaveFLWForInvalidLocation() {
        String callerId = "919986574410";
        String name = "name";
        Designation designation = Designation.AWW;
        when(locationService.findFor(anyString(), anyString(), anyString())).thenReturn(null);
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), new LocationRequest("district", "block", "village"), null, "flwGuid");

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Invalid location"));
        verify(frontLineWorkerService, never()).createOrUpdate(any(FrontLineWorker.class), any(Location.class));
        verify(registrationMeasureService, never()).createOrUpdateFor(callerId);
    }

    @Test
    public void shouldNotSaveFLWForInvalidCallerId() {
        String callerId = "";
        String name = "name";
        Designation designation = Designation.AWW;
        when(locationService.findFor(anyString(), anyString(), anyString())).thenReturn(null);

        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), new LocationRequest("district", "block", "village"), null, "flwGuid");

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Invalid msisdn"));
        verify(frontLineWorkerService, never()).createOrUpdate(any(FrontLineWorker.class), any(Location.class));
        verify(registrationMeasureService, never()).createOrUpdateFor(callerId);

        callerId = "abcdef";
        frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), new LocationRequest("district", "block", "village"), null, "flwGuid");
        registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Invalid msisdn"));
        verify(frontLineWorkerService, never()).createOrUpdate(any(FrontLineWorker.class), any(Location.class));
        verify(registrationMeasureService, never()).createOrUpdateFor(callerId);
    }

    @Test
    public void shouldSaveFLWWithInvalidDesignationAsPartiallyRegistered() {
        String callerId = "919986574410";
        String name = "name";
        String designation = "invalid_designation";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        registrationService = new RegistrationService(frontLineWorkerService, courseItemMeasureService, frontLineWorkerDimensionService, registrationMeasureService, locationService, jobAidContentMeasureService, callDurationMeasureService, smsSentMeasureService);
        when(locationService.findFor("district", "block", "village")).thenReturn(location);
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation, new LocationRequest("district", "block", "village"), null, "flwGuid");
        when(frontLineWorkerService.createOrUpdate(any(FrontLineWorker.class), any(Location.class))).thenReturn(new FrontLineWorker(callerId, "operator", "bihar"));

        registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontLineWorkerService).createOrUpdate(captor.capture(), any(Location.class));
        FrontLineWorker value = captor.getValue();
        assertEquals(callerId, value.getMsisdn());
        assertNull(value.getDesignation());
    }

    @Test
    public void shouldRegisterMultipleFLWs() {
        String callerId = "1234532532523 ";
        String callerId1 = "123434255434";
        String name = "name";
        String name1 = " name1";
        String designation = Designation.AWW.name();
        Location location = new Location("district", "block", "village", 1, 1, 1);
        when(locationService.findFor("district", "block", "village")).thenReturn(location);
        List<FrontLineWorkerRequest> frontLineWorkerRequestList = new ArrayList<FrontLineWorkerRequest>();
        frontLineWorkerRequestList.add(new FrontLineWorkerRequest(callerId, name, designation, new LocationRequest("district", "block", "village"), null, "flwGuid1"));
        frontLineWorkerRequestList.add(new FrontLineWorkerRequest(callerId1, name1, designation, new LocationRequest("district", "block", "village"), null, "flwGuid2"));
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId.trim(), name, Designation.valueOf(designation), location, null, "flwGuid1"), location)).thenReturn(new FrontLineWorker(callerId.trim(), "airtel", "bihar"));
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId1, name1, Designation.valueOf(designation), location, null, "flwGuid2"), location)).thenReturn(new FrontLineWorker(callerId1, "airtel", "bihar"));

        List<RegistrationResponse> registrationResponses = registrationService.registerAllFLWs(frontLineWorkerRequestList);

        assertTrue(StringUtils.contains(registrationResponses.get(0).getMessage(), "Created/Updated FLW record"));
        verify(registrationMeasureService).createOrUpdateFor(callerId.trim());
        assertTrue(StringUtils.contains(registrationResponses.get(1).getMessage(), "Created/Updated FLW record"));
        verify(registrationMeasureService).createOrUpdateFor(callerId1);
    }

    @Test
    public void shouldGetFilteredFLWs() {
        Long msisdn = 123456L;
        String name = "name";
        String status = RegistrationStatus.REGISTERED.name();
        String designation = Designation.AWW.name();
        String operator = "airtel";
        String circle = "bihar";
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status, "flwGuid"));
        when(frontLineWorkerDimensionService.getFilteredFLW(new ArrayList<Long>(), msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerResponse> filteredFLW = registrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, null, null);

        assertEquals(1, filteredFLW.size());
        assertEquals(msisdn.toString(), filteredFLW.get(0).getMsisdn());
    }

    @Test
    public void shouldGetFilteredFLWsBetweenStartDateAndEndDate() {
        Long msisdn = 123456L;
        String name = "name";
        String status = RegistrationStatus.REGISTERED.name();
        String designation = Designation.AWW.name();
        String operator = "airtel";
        String circle = "bihar";
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        DateTime activityStartDate = DateTime.now();
        DateTime activityEndDate = DateTime.now().plusDays(1);
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status, "flwGuid"));
        ArrayList<Long> msisdnList = new ArrayList<Long>();
        msisdnList.add(msisdn);
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(msisdnList);
        when(frontLineWorkerDimensionService.getFilteredFLW(msisdnList, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerResponse> filteredFLW = registrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, activityStartDate.toDate(), activityEndDate.toDate());

        assertEquals(1, filteredFLW.size());
        assertEquals(msisdn.toString(), filteredFLW.get(0).getMsisdn());
    }

    @Test
    public void shouldNotAddMsisdnFilterGetFilteredFLWsBetweenStartDateAndEndDate() {
        Long msisdn = 123456L;
        Long msisdn1 = 123457L;
        String name = "name";
        String status = RegistrationStatus.REGISTERED.name();
        String designation = Designation.AWW.name();
        String operator = "airtel";
        String circle = "bihar";
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        DateTime activityStartDate = DateTime.now();
        DateTime activityEndDate = DateTime.now().plusDays(1);
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status, "flwGuid"));
        ArrayList<Long> msisdnListForCC = new ArrayList<Long>();
        msisdnListForCC.add(msisdn);
        ArrayList<Long> msisdnListForJobAid = new ArrayList<Long>();
        msisdnListForCC.add(msisdn1);
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(msisdnListForCC);
        when(jobAidContentMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(msisdnListForJobAid);
        when(frontLineWorkerDimensionService.getFilteredFLW(msisdnListForCC, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerResponse> filteredFLW = registrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, activityStartDate.toDate(), activityEndDate.toDate());

        assertEquals(1, filteredFLW.size());
        assertEquals(msisdn.toString(), filteredFLW.get(0).getMsisdn());
    }

    @Test
    public void shouldNotApplyGeneralFiltersIfDateFilterReturnsNothing() {
        Long msisdn = 123456L;
        String name = "name";
        String status = RegistrationStatus.REGISTERED.name();
        String designation = Designation.AWW.name();
        String operator = "airtel";
        String circle = "bihar";
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        DateTime activityStartDate = DateTime.now();
        DateTime activityEndDate = DateTime.now().plusDays(1);
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status, "flwGuid"));
        ArrayList<Long> msisdnList = new ArrayList<Long>();
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(msisdnList);

        List<FrontLineWorkerResponse> filteredFLW = registrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, activityStartDate.toDate(), activityEndDate.toDate());

        verifyZeroInteractions(frontLineWorkerDimensionService);
        assertEquals(0, filteredFLW.size());
    }

    @Test
    public void shouldNotApplyDateFiltersIfEitherOfTheDateFilterIsNotGiven() {
        Long msisdn = 123456L;
        String name = "name";
        String status = RegistrationStatus.REGISTERED.name();
        String designation = Designation.AWW.name();
        String operator = "airtel";
        String circle = "bihar";
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        DateTime activityEndDate = DateTime.now().plusDays(1);
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status, "flwGuid"));
        when(frontLineWorkerDimensionService.getFilteredFLW(Collections.EMPTY_LIST, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerResponse> filteredFLW = registrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, null, activityEndDate.toDate());

        verifyZeroInteractions(courseItemMeasureService);
        assertEquals(1, filteredFLW.size());
    }
}
