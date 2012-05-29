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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
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
    private LocationService locationService;
    @Mock
    private FrontLineWorkerDimensionService frontLineWorkerDimensionService;
    @Mock
    private CourseItemMeasureService courseItemMeasureService;

    @Before
    public void setUp() {
        initMocks(this);
        registrationService = new RegistrationService(frontLineWorkerService, courseItemMeasureService, frontLineWorkerDimensionService, registrationMeasureService, locationService);
    }

    @Test
    public void shouldSaveNewFLWAndPublishForReport() {
        String callerId = "919986574410";
        String name = "name";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        Designation designation = Designation.AWW;
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), null, "bihar", new LocationRequest("district", "block", "village"));
        when(locationService.getAll()).thenReturn(Arrays.asList(location));
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, name, designation, location, RegistrationStatus.REGISTERED), location)).thenReturn(new FrontLineWorker(callerId, "operator"));

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Created/Updated FLW record"));
        verify(frontLineWorkerService).createOrUpdate(new FrontLineWorker(callerId, name, designation, location, RegistrationStatus.REGISTERED), location);
        verify(registrationMeasureService).createRegistrationMeasure(callerId);
    }

    @Test
    public void shouldNotSaveFLWForInvalidLocation() {
        String callerId = "919986574410";
        String name = "name";
        Designation designation = Designation.AWW;
        when(locationService.getAll()).thenReturn(new ArrayList<Location>());
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), null, "bihar", new LocationRequest("district", "block", "village"));

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Invalid location"));
        verify(frontLineWorkerService, never()).createOrUpdate(any(FrontLineWorker.class), any(Location.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId);
    }

    @Test
    public void shouldNotSaveFLWForInvalidCallerId() {
        String callerId = "";
        String name = "name";
        Designation designation = Designation.AWW;
        when(locationService.getAll()).thenReturn(new ArrayList<Location>());
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), null, "bihar", new LocationRequest("district", "block", "village"));
        
        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Invalid msisdn"));
        verify(frontLineWorkerService, never()).createOrUpdate(any(FrontLineWorker.class), any(Location.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId);

        callerId = "abcdef";
        frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), null, "bihar", new LocationRequest("district", "block", "village"));
        registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Invalid msisdn"));
        verify(frontLineWorkerService, never()).createOrUpdate(any(FrontLineWorker.class), any(Location.class));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId);
    }

    @Test
    public void shouldSaveFLWWithInvalidNameAsPartiallyRegistered() {
        String callerId = "919986574410";
        String name = "";
        Designation designation = Designation.AWW;
        Location location = new Location("district", "block", "village", 1, 1, 1);
        registrationService = new RegistrationService(frontLineWorkerService, courseItemMeasureService, frontLineWorkerDimensionService, registrationMeasureService, locationService);
        when(locationService.getAll()).thenReturn(Arrays.asList(location));
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation.name(), null, "bihar", new LocationRequest("district", "block", "village"));
        when(frontLineWorkerService.createOrUpdate(any(FrontLineWorker.class), any(Location.class))).thenReturn(new FrontLineWorker(callerId, "operator"));

        RegistrationResponse registrationResponse = registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        assertTrue(StringUtils.contains(registrationResponse.getMessage(), "Invalid name"));
        verify(frontLineWorkerService, never()).createOrUpdate(any(FrontLineWorker.class), any(Location.class));
    }

    @Test
    public void shouldSaveFLWWithInvalidDesignationAsPartiallyRegistered() {
        String callerId = "919986574410";
        String name = "name";
        String designation = "invalid_designation";
        Location location = new Location("district", "block", "village", 1, 1, 1);
        registrationService = new RegistrationService(frontLineWorkerService, courseItemMeasureService, frontLineWorkerDimensionService, registrationMeasureService, locationService);
        when(locationService.getAll()).thenReturn(Arrays.asList(location));
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(callerId, name, designation, null, "bihar", new LocationRequest("district", "block", "village"));
        when(frontLineWorkerService.createOrUpdate(any(FrontLineWorker.class), any(Location.class))).thenReturn(new FrontLineWorker(callerId, "operator"));

        registrationService.createOrUpdateFLW(frontLineWorkerRequest);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(frontLineWorkerService).createOrUpdate(captor.capture(), any(Location.class));
        FrontLineWorker value = captor.getValue();
        assertEquals(callerId, value.getMsisdn());
        assertEquals(Designation.INVALID, value.getDesignation());
    }

    @Test
    public void shouldRegisterMultipleFLWs() {
        String callerId = "123";
        String callerId1 = "1234";
        String name = "name";
        String name1 = "name1";
        String designation = Designation.AWW.name();
        Location location = new Location("district", "block", "village", 1, 1, 1);
        when(locationService.getAll()).thenReturn(Arrays.asList(location));
        List<FrontLineWorkerRequest> frontLineWorkerRequestList = new ArrayList<FrontLineWorkerRequest>();
        frontLineWorkerRequestList.add(new FrontLineWorkerRequest(callerId, name, designation, null, "bihar", new LocationRequest("district", "block", "village")));
        frontLineWorkerRequestList.add(new FrontLineWorkerRequest(callerId1, name1, designation, null, "bihar", new LocationRequest("district", "block", "village")));
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId, name, Designation.valueOf(designation), location, RegistrationStatus.REGISTERED), location)).thenReturn(new FrontLineWorker(callerId, "airtel"));
        when(frontLineWorkerService.createOrUpdate(new FrontLineWorker(callerId1, name1, Designation.valueOf(designation), location, RegistrationStatus.REGISTERED), location)).thenReturn(new FrontLineWorker(callerId1, "airtel"));

        List<RegistrationResponse> registrationResponses = registrationService.registerAllFLWs(frontLineWorkerRequestList);

        assertTrue(StringUtils.contains(registrationResponses.get(0).getMessage(), "Invalid msisdn"));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId);
        assertTrue(StringUtils.contains(registrationResponses.get(1).getMessage(), "Invalid msisdn"));
        verify(registrationMeasureService, never()).createRegistrationMeasure(callerId1);
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
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));
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
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));
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
        String name = "name";
        String status = RegistrationStatus.REGISTERED.name();
        String designation = Designation.AWW.name();
        String operator = "airtel";
        String circle = "bihar";
        ArrayList<FrontLineWorkerDimension> frontLineWorkerDimensions = new ArrayList<FrontLineWorkerDimension>();
        DateTime activityStartDate = DateTime.now();
        DateTime activityEndDate = DateTime.now().plusDays(1);
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));
        ArrayList<Long> msisdnList = new ArrayList<Long>();
        msisdnList.add(msisdn);
        when(courseItemMeasureService.getAllFrontLineWorkerMsisdnsBetween(activityStartDate.toDate(), activityEndDate.toDate())).thenReturn(msisdnList);
        when(frontLineWorkerDimensionService.getFilteredFLW(msisdnList, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

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
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));
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
        frontLineWorkerDimensions.add(new FrontLineWorkerDimension(msisdn, operator, circle, name, designation, status));
        when(frontLineWorkerDimensionService.getFilteredFLW(Collections.EMPTY_LIST, msisdn, name, status, designation, operator, circle)).thenReturn(frontLineWorkerDimensions);

        List<FrontLineWorkerResponse> filteredFLW = registrationService.getFilteredFLW(msisdn, name, status, designation, operator, circle, null, activityEndDate.toDate());

        verifyZeroInteractions(courseItemMeasureService);
        assertEquals(1, filteredFLW.size());
    }
}
