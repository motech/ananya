package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.request.LocationSyncRequest;
import org.motechproject.ananya.requests.FLWStatusChangeRequest;
import org.motechproject.ananya.response.FrontLineWorkerResponse;
import org.motechproject.ananya.response.RegistrationResponse;
import org.motechproject.ananya.service.dimension.FrontLineWorkerDimensionService;
import org.motechproject.ananya.service.dimension.FrontLineWorkerHistoryService;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.measure.JobAidContentMeasureService;
import org.motechproject.ananya.service.measure.RegistrationMeasureService;
import org.motechproject.ananya.service.measure.SMSSentMeasureService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FLWRegistrationServiceTest {

    private FLWRegistrationService flwRegistrationService;

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
    private FrontLineWorkerHistoryService frontLineWorkerHistoryService;
    @Mock
    private CourseItemMeasureService courseItemMeasureService;
    @Mock
    private CallDurationMeasureService callDurationMeasureService;
    @Mock
    private SMSSentMeasureService smsSentMeasureService;
    @Mock
    private LocationRegistrationService locationRegistrationService;
    @Mock
    private RegistrationMeasure registrationMeasure;

    @Captor
    private ArgumentCaptor<List<FLWStatusChangeRequest>> flwStatusChangeRequestCaptor;
    private UUID flwId = UUID.randomUUID();
    private String callerId;
    private String name;
    private Designation designation;
    private String language;
    private InOrder order;
    private String newMsisdn;

    @Before
    public void setUp() {
        initMocks(this);
        flwRegistrationService = new FLWRegistrationService(
                frontLineWorkerService, frontLineWorkerHistoryService, courseItemMeasureService, frontLineWorkerDimensionService,
                registrationMeasureService, locationService, jobAidContentMeasureService,
                callDurationMeasureService, smsSentMeasureService, locationRegistrationService);
        order = Mockito.inOrder(registrationMeasureService, frontLineWorkerDimensionService, courseItemMeasureService, callDurationMeasureService, jobAidContentMeasureService, smsSentMeasureService, registrationMeasure);
    }

    @Test
    public void shouldSaveNewFLWAndPublishForReport() {
        String callerId = "919986574410";
        String name = "name";
        Location location = new Location("state", "district", "block", "village", 1, 1, 1, 1, null, null);
        Designation designation = Designation.AWW;
        String language = "language";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, null, name, designation.name(), new LocationRequest("state", "district ", " block", "village"), null, flwId.toString(), VerificationStatus.OTHER.name(), language, null);
        frontLineWorkerRequest.setAlternateContactNumber(callerId);
        when(locationService.findFor("state", "district", "block", "village")).thenReturn(location);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, callerId, name, designation, location, language, null, flwId), location)).thenReturn(new FrontLineWorker(callerId, "operator", "bihar", language));

        RegistrationResponse registrationResponse = flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Created/Updated FLW record"));
        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontLineWorkerService).createOrUpdate(captor.capture(), eq(location));
        FrontLineWorker frontLineWorker = captor.getValue();
        assertEquals(callerId, frontLineWorker.getMsisdn());
        assertEquals(name, frontLineWorker.getName());
        assertEquals(designation, frontLineWorker.getDesignation());
        assertEquals(location.getExternalId(), frontLineWorker.getLocationId());
        assertEquals(VerificationStatus.OTHER, frontLineWorker.getVerificationStatus());
        assertEquals(callerId, frontLineWorker.getAlternateContactNumber());
        verify(registrationMeasureService).createOrUpdateFor(callerId);
    }

    @Test
    public void shouldTransferMeasuresForChangeMsisdnWhenNewMsisdnIsRegistered() {
        FrontLineWorkerRequest frontLineWorkerRequest = flwRequestWithForMsisdnChange();
        Location location = new Location("state", "district", "block", "village", 1, 1, 1, 1, null, null);
        when(locationService.findFor("state", "district", "block", "village")).thenReturn(location);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, callerId, name, designation, location, language, null, flwId), location)).thenReturn(new FrontLineWorker(callerId, "operator", "bihar", language));
        FrontLineWorkerDimension toFlw = new FrontLineWorkerDimension();
        FrontLineWorkerDimension fromFlw = new FrontLineWorkerDimension();
        when(frontLineWorkerDimensionService.getFrontLineWorkerDimension(Long.valueOf(frontLineWorkerRequest.getNewMsisdn()))).thenReturn(fromFlw);
        when(registrationMeasureService.createOrUpdateFor(callerId)).thenReturn(registrationMeasure);
        when(registrationMeasure.getFrontLineWorkerDimension()).thenReturn(toFlw);

        flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        verifyMocksForChangeMsisdnMeasureTransfer(frontLineWorkerRequest, toFlw, fromFlw, order);
    }

    @Test
    public void shouldNotTransferMeasuresForChangeMsisdnWhenNewMsisdnIsNotRegistered() {
        FrontLineWorkerRequest frontLineWorkerRequest = flwRequestWithForMsisdnChange();
        Location location = new Location("state", "district", "block", "village", 1, 1, 1, 1, null, null);
        when(locationService.findFor("state", "district", "block", "village")).thenReturn(location);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, callerId, name, designation, location, language, null, flwId), location)).thenReturn(new FrontLineWorker(callerId, "operator", "bihar", language));
        FrontLineWorkerDimension toFlw = new FrontLineWorkerDimension();
        when(registrationMeasureService.createOrUpdateFor(callerId)).thenReturn(registrationMeasure);
        when(registrationMeasure.getFrontLineWorkerDimension()).thenReturn(toFlw);
        when(frontLineWorkerDimensionService.getFrontLineWorkerDimension(Long.valueOf(frontLineWorkerRequest.getNewMsisdn()))).thenReturn(null);

        flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        verify(registrationMeasure).getFrontLineWorkerDimension();
        verify(frontLineWorkerDimensionService).getFrontLineWorkerDimension(Long.valueOf(frontLineWorkerRequest.getNewMsisdn()));
        verify(courseItemMeasureService, never()).transfer(any(FrontLineWorkerDimension.class), any(FrontLineWorkerDimension.class));
        verify(callDurationMeasureService, never()).transfer(any(FrontLineWorkerDimension.class), any(FrontLineWorkerDimension.class));
        verify(jobAidContentMeasureService, never()).transfer(any(FrontLineWorkerDimension.class), any(FrontLineWorkerDimension.class));
        verify(smsSentMeasureService, never()).transfer(any(FrontLineWorkerDimension.class), any(FrontLineWorkerDimension.class));
        verify(registrationMeasureService, never()).remove(any(Integer.class));
        verify(frontLineWorkerDimensionService, never()).remove(any(FrontLineWorkerDimension.class));
    }

    @Test
    public void shouldChangeMsisdn() {
        FrontLineWorkerRequest frontLineWorkerRequest = flwRequestWithForMsisdnChange();
        Location location = new Location("state", "district", "block", "village", 1, 1, 1, 1, null, null);
        when(locationService.findFor("state", "district", "block", "village")).thenReturn(location);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, callerId, name, designation, location, language, null, flwId), location)).thenReturn(new FrontLineWorker(callerId, "operator", "bihar", language));
        String status = "UNREGISTERED";
        when(frontLineWorkerService.changeMsisdn(callerId, newMsisdn, location)).thenReturn(status);
        FrontLineWorkerDimension toFlw = new FrontLineWorkerDimension();
        when(registrationMeasureService.createOrUpdateFor(callerId)).thenReturn(registrationMeasure);
        when(registrationMeasure.getFrontLineWorkerDimension()).thenReturn(toFlw);
        FrontLineWorkerDimension fromFlw = new FrontLineWorkerDimension();
        fromFlw.setOperator("Hero");
        when(frontLineWorkerDimensionService.getFrontLineWorkerDimension(Long.valueOf(frontLineWorkerRequest.getNewMsisdn()))).thenReturn(fromFlw);

        flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertEquals(Long.valueOf(frontLineWorkerRequest.getNewMsisdn()), toFlw.getMsisdn());
        assertEquals(fromFlw.getOperator(), toFlw.getOperator());
        assertEquals(status, toFlw.getStatus());
        verify(frontLineWorkerDimensionService).update(toFlw);
        verify(frontLineWorkerService).changeMsisdn(frontLineWorkerRequest.getMsisdn(), frontLineWorkerRequest.getNewMsisdn(), location);
    }

    @Test
    public void changeMsisdnShouldRetainHistory() {
        FrontLineWorkerRequest frontLineWorkerRequest = flwRequestWithForMsisdnChange();
        Location location = new Location("state", "district", "block", "village", 1, 1, 1, 1, null, null);
        when(locationService.findFor("state", "district", "block", "village")).thenReturn(location);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, callerId, name, designation, location, language, null, flwId), location)).thenReturn(new FrontLineWorker(callerId, "operator", "bihar", language));
        FrontLineWorkerDimension toFlw = new FrontLineWorkerDimension();
        when(registrationMeasureService.createOrUpdateFor(callerId)).thenReturn(registrationMeasure);
        when(registrationMeasure.getFrontLineWorkerDimension()).thenReturn(toFlw);
        FrontLineWorkerDimension fromFlw = new FrontLineWorkerDimension();
        when(frontLineWorkerDimensionService.getFrontLineWorkerDimension(Long.valueOf(frontLineWorkerRequest.getNewMsisdn()))).thenReturn(fromFlw);

        flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        verify(frontLineWorkerHistoryService).create(registrationMeasure);
        verify(frontLineWorkerHistoryService).markCurrentAsOld(fromFlw);
    }

    @Test
    public void shouldRemoveRegisteredFlwWithNewMsisdn() {
        FrontLineWorkerRequest frontLineWorkerRequest = flwRequestWithForMsisdnChange();
        Location location = new Location("state", "district", "block", "village", 1, 1, 1, 1, null, null);
        when(locationService.findFor("state", "district", "block", "village")).thenReturn(location);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, callerId, name, designation, location, language, null, flwId), location)).thenReturn(new FrontLineWorker(callerId, "operator", "bihar", language));
        FrontLineWorkerDimension toFlw = new FrontLineWorkerDimension();
        when(registrationMeasureService.createOrUpdateFor(callerId)).thenReturn(registrationMeasure);
        when(registrationMeasure.getFrontLineWorkerDimension()).thenReturn(toFlw);
        FrontLineWorkerDimension fromFlw = new FrontLineWorkerDimension();
        when(frontLineWorkerDimensionService.getFrontLineWorkerDimension(Long.valueOf(frontLineWorkerRequest.getNewMsisdn()))).thenReturn(fromFlw);

        flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        verify(frontLineWorkerDimensionService).getFrontLineWorkerDimension(Long.valueOf(frontLineWorkerRequest.getNewMsisdn()));

        verifyMocksForChangeMsisdnMeasureTransfer(frontLineWorkerRequest, toFlw, fromFlw, order);
        order.verify(registrationMeasureService).remove(fromFlw.getId());
        order.verify(frontLineWorkerDimensionService).remove(fromFlw);
    }

    @Test
    public void shouldSaveNewFLWWithoutLocationAndPublishForReport() {
        String callerId = "919986574410";
        String name = "name";
        Designation designation = Designation.AWW;
        String language = "language";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, "", name, designation.name(), null, null, flwId.toString(), VerificationStatus.OTHER.name(), language, null);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, null, name, designation, null, language, null, flwId), null)).thenReturn(new FrontLineWorker(callerId, "operator", "bihar", language));

        RegistrationResponse registrationResponse = flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Created/Updated FLW record"));
        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        Location expectedLocation = null;
        verify(frontLineWorkerService).createOrUpdate(captor.capture(), eq(expectedLocation));
        FrontLineWorker frontLineWorker = captor.getValue();
        assertEquals(callerId, frontLineWorker.getMsisdn());
        assertEquals(name, frontLineWorker.getName());
        assertEquals(designation, frontLineWorker.getDesignation());
        assertEquals(VerificationStatus.OTHER, frontLineWorker.getVerificationStatus());
        verify(registrationMeasureService).createOrUpdateFor(callerId);
    }

    @Test
    public void shouldUpdateAllMeasuresForExistingFLW() {
        String callerId = "919986574410";
        String name = "name";
        String language = "language";
        Location location = new Location("state", "district", "block", "village", 1, 1, 1, 1, null, null);
        Designation designation = Designation.AWW;
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, null, name, designation.name(), new LocationRequest("state", "district ", " block", "village"), null, flwId.toString(), null, language, null);
        when(locationService.findFor("state", "district", "block", "village")).thenReturn(location);
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, null, "operator", Designation.ANM, location, language, null, flwId);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, null, name, designation, location, language, null, flwId), location)).thenReturn(frontLineWorker);

        RegistrationResponse registrationResponse = flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Created/Updated FLW record"));
        verify(frontLineWorkerService).createOrUpdate(new FrontLineWorker(callerId, null, name, designation, location, language, null, flwId), location);
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
        String language = "language";
        Designation designation = Designation.AWW;
        when(locationService.findFor(anyString(), anyString(), anyString(), anyString())).thenReturn(null);
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, null, name, designation.name(), new LocationRequest("state", null, "block", "village"), null, flwId.toString(), null, language, null);

        RegistrationResponse registrationResponse = flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Invalid location"));
        verify(frontLineWorkerService, never()).createOrUpdate(any(FrontLineWorker.class), any(Location.class));
        verify(registrationMeasureService, never()).createOrUpdateFor(callerId);
    }

    @Test
    public void shouldNotSaveFLWForInvalidCallerId() {
        String callerId = "";
        String name = "name";
        String language = "language";
        Designation designation = Designation.AWW;
        when(locationService.findFor(anyString(), anyString(), anyString(), anyString())).thenReturn(null);

        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, null, name, designation.name(), new LocationRequest("state", "district", "block", "village"), null, flwId.toString(), null, language, null);

        RegistrationResponse registrationResponse = flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Invalid msisdn"));
        verify(frontLineWorkerService, never()).createOrUpdate(any(FrontLineWorker.class), any(Location.class));
        verify(registrationMeasureService, never()).createOrUpdateFor(callerId);

        callerId = "abcdef";
        frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, null, name, designation.name(), new LocationRequest("state", "district", "block", "village"), null, flwId.toString(), null, language, null);
        registrationResponse = flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Invalid msisdn"));
        verify(frontLineWorkerService, never()).createOrUpdate(any(FrontLineWorker.class), any(Location.class));
        verify(registrationMeasureService, never()).createOrUpdateFor(callerId);
    }

    @Test
    public void shouldSaveFLWWithInvalidDesignationAsPartiallyRegistered() {
        String callerId = "919986574410";
        String name = "name";
        String language = "language";
        String designation = "invalid_designation";
        Location location = new Location("state", "district", "block", "village", 1, 1, 1, 1, null, null);
        when(locationService.findFor("state", "district", "block", "village")).thenReturn(location);
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, null, name, designation,
                new LocationRequest("state", "district", "block", "village"), null, flwId.toString(), null, language, null);
        when(frontLineWorkerService.createOrUpdate(any(FrontLineWorker.class), any(Location.class))).
                thenReturn(new FrontLineWorker(callerId, "operator", "bihar", language));

        flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

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
        String language = "language";
        String name = "name";
        String name1 = " name1";
        String designation = Designation.AWW.name();
        UUID flwId1 = UUID.randomUUID();
        UUID flwId2 = UUID.randomUUID();
        Location location = new Location("state", "district", "block", "village", 1, 1, 1, 1, null, null);
        when(locationService.findFor("state", "district", "block", "village")).thenReturn(location);
        List<FrontLineWorkerRequest> frontLineWorkerRequestList = new ArrayList<>();
        frontLineWorkerRequestList.add(new FrontLineWorkerRequest(callerId, null, name, designation, new LocationRequest("state", "district", "block", "village"), null, flwId1.toString(), null, language, null));
        frontLineWorkerRequestList.add(new FrontLineWorkerRequest(callerId1, null, name1, designation, new LocationRequest("state", "district", "block", "village"), null, flwId2.toString(), null, language, null));
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId.trim(), null, name, Designation.valueOf(designation), location, language, null, flwId1), location)).thenReturn(new FrontLineWorker(callerId.trim(), "airtel", "bihar", language));
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId1, null, name1, Designation.valueOf(designation), location, language, null, flwId2), location)).thenReturn(new FrontLineWorker(callerId1, "airtel", "bihar", language));

        List<RegistrationResponse> registrationResponses = flwRegistrationService.registerAllFLWs(frontLineWorkerRequestList);

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
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, null, operator, circle, name, designation, status, flwId, null));
        when(frontLineWorkerDimensionService.getFilteredFLW(new ArrayList<Long>(), msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerResponse> filteredFLW = flwRegistrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, null, null);

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
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, null, operator, circle, name, designation, status, flwId, null));
        ArrayList<Long> msisdnList = new ArrayList<Long>();
        msisdnList.add(msisdn);
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(msisdnList);
        when(frontLineWorkerDimensionService.getFilteredFLW(msisdnList, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerResponse> filteredFLW = flwRegistrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, activityStartDate.toDate(), activityEndDate.toDate());

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
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, null, operator, circle, name, designation, status, flwId, null));
        ArrayList<Long> msisdnListForCC = new ArrayList<Long>();
        msisdnListForCC.add(msisdn);
        ArrayList<Long> msisdnListForJobAid = new ArrayList<Long>();
        msisdnListForCC.add(msisdn1);
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(msisdnListForCC);
        when(jobAidContentMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(msisdnListForJobAid);
        when(frontLineWorkerDimensionService.getFilteredFLW(msisdnListForCC, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerResponse> filteredFLW = flwRegistrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, activityStartDate.toDate(), activityEndDate.toDate());

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
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, null, operator, circle, name, designation, status, flwId, null));
        ArrayList<Long> msisdnList = new ArrayList<Long>();
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(msisdnList);

        List<FrontLineWorkerResponse> filteredFLW = flwRegistrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, activityStartDate.toDate(), activityEndDate.toDate());

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
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, null, operator, circle, name, designation, status, flwId, null));
        when(frontLineWorkerDimensionService.getFilteredFLW(Collections.EMPTY_LIST, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerResponse> filteredFLW = flwRegistrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, null, activityEndDate.toDate());

        verifyZeroInteractions(courseItemMeasureService);
        assertEquals(1, filteredFLW.size());
    }

    @Test
    public void shouldCreateANewLocationIfNotPresentAndUpdateTheFLW() {
        String callerId = "919986574410";
        String name = "name";
        String language = "language";
        Location location = new Location("state", "district", "block", "village", 1, 1, 1, 1, null, null);
        Designation designation = Designation.AWW;
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, null, name, designation.name(), new LocationRequest("state", "district ", " block", "village"), null, flwId.toString(), VerificationStatus.OTHER.name(), language, null);
        when(locationService.findFor("state", "district", "block", "village")).thenReturn(null, location);
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, null, name, designation, location, language, null, flwId), location)).thenReturn(new FrontLineWorker(callerId, "operator", "bihar", language));

        RegistrationResponse registrationResponse = flwRegistrationService.createOrUpdateFLW(frontLineWorkerRequest);

        LocationRequest locationRequest = new LocationRequest("state", "district", "block", "village");
        verify(locationRegistrationService).addOrUpdate(new LocationSyncRequest(locationRequest, locationRequest, LocationStatus.NOT_VERIFIED.name(), frontLineWorkerRequest.getLastModified()));

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Created/Updated FLW record"));
        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontLineWorkerService).createOrUpdate(captor.capture(), eq(location));

        FrontLineWorker frontLineWorker = captor.getValue();
        assertEquals(callerId, frontLineWorker.getMsisdn());
        verify(registrationMeasureService).createOrUpdateFor(callerId);
    }

    private void verifyMocksForChangeMsisdnMeasureTransfer(FrontLineWorkerRequest frontLineWorkerRequest, FrontLineWorkerDimension toFlw, FrontLineWorkerDimension fromFlw, InOrder inOrder) {
        inOrder.verify(registrationMeasureService).createOrUpdateFor(callerId);
        inOrder.verify(registrationMeasure).getFrontLineWorkerDimension();
        inOrder.verify(frontLineWorkerDimensionService).getFrontLineWorkerDimension(Long.valueOf(frontLineWorkerRequest.getNewMsisdn()));
        inOrder.verify(courseItemMeasureService).transfer(fromFlw, toFlw);
        inOrder.verify(callDurationMeasureService).transfer(fromFlw, toFlw);
        inOrder.verify(jobAidContentMeasureService).transfer(fromFlw, toFlw);
        inOrder.verify(smsSentMeasureService).transfer(fromFlw, toFlw);
    }

    private FrontLineWorkerRequest flwRequestWithForMsisdnChange() {
        callerId = "919986574410";
        name = "name";
        designation = Designation.AWW;
        language = "language";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, null, name, designation.name(), new LocationRequest("state", "district ", " block", "village"), null, flwId.toString(), VerificationStatus.OTHER.name(), language, null);
        frontLineWorkerRequest.setAlternateContactNumber(callerId);
        newMsisdn = "911234567890";
        frontLineWorkerRequest.setNewMsisdn(newMsisdn);
        return frontLineWorkerRequest;
    }

}
