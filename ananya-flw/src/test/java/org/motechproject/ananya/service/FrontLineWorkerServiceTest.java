package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllSMSReferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerServiceTest {

    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private AllSMSReferences allSMSReferences;
    @Mock
    private FrontLineWorker mockedFrontLineWorker;
    @Mock
    private LocationService locationService;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers, locationService);
    }

    @Test
    public void shouldCreateNewFLWIfNotPresentInDB() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("123", "name", Designation.ANM, new Location(), RegistrationStatus.REGISTERED);
        String msisdn = frontLineWorker.getMsisdn();

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(null);

        frontLineWorkerService.createOrUpdateForCall(msisdn, frontLineWorker.getOperator(), "circle");

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker savedFrontLineWorker = captor.getValue();
        assertEquals(frontLineWorker.getMsisdn(), savedFrontLineWorker.getMsisdn());
        assertEquals(frontLineWorker.getOperator(), savedFrontLineWorker.getOperator());
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, savedFrontLineWorker.status());
    }

    @Test
    public void shouldNotCreateFLWIfExistsInDBAndNotUpdateFLWIfOperatorIsNotModified() {
        String msisdn = "123";
        String circle = "circle";
        String operator = "airtel";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANM, new Location(), RegistrationStatus.REGISTERED);
        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        FrontLineWorker frontLineWorkerFromDb = frontLineWorkerService.createOrUpdateForCall(msisdn, operator, circle);

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers, never()).update(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerFromDb);
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButUpdateWhenOperatorIsDifferent() {
        String msisdn = "123";
        String circle = "circle";
        String oldOperator = "vodafone";
        String newOperator = "airtel";
        Location location = new Location();

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANM, location, RegistrationStatus.REGISTERED);
        frontLineWorker.setOperator(oldOperator);
        frontLineWorker.setCircle(circle);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorker frontLineWorkerFromDb = frontLineWorkerService.createOrUpdateForCall(msisdn, newOperator, circle);

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers).update(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerFromDb);
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButUpdateWhenCircleIsDifferent() {
        String msisdn = "123";
        String operator = "vodafone";
        Location location = new Location();
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANM, location, RegistrationStatus.REGISTERED);
        frontLineWorker.setOperator(operator);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorker frontLineWorkerFromDb = frontLineWorkerService.createOrUpdateForCall(msisdn, operator, "circle");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers).update(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerFromDb);
    }

    @Test
    public void shouldNotCreateFLWIfAlreadyPresentInDBButShouldUpdateWhenCircleAndOperatorIsDifferent() {
        String msisdn = "123";
        String oldOperator = "vodafone";
        String newOperator = "airtel";
        Location location = new Location();
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANM, location, RegistrationStatus.REGISTERED);
        frontLineWorker.setOperator(oldOperator);
        frontLineWorker.setCircle("oldCircle");

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorker frontLineWorkerFromDb = frontLineWorkerService.createOrUpdateForCall(msisdn, newOperator, "newCircle");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers).update(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerFromDb);
    }

    @Test
    public void shouldFindByCallerId() {
        FrontLineWorker expectedFrontLineWorker = new FrontLineWorker();
        when(allFrontLineWorkers.findByMsisdn("123")).thenReturn(expectedFrontLineWorker);

        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId("123");

        assertEquals(expectedFrontLineWorker, frontLineWorker);
    }

    @Test
    public void shouldCreateFLWIfNotExists() {
        String msisdn = "919986574410";
        String name = "name";
        Designation designation = Designation.AWW;
        Location location = new Location("district", "block", "panchayat", 123, 124, 125);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(null);

        FrontLineWorker frontLineWorker = frontLineWorkerService.createOrUpdateForImport(
                msisdn, name, designation, location);

        verify(allFrontLineWorkers).add(frontLineWorker);
        assertEquals(frontLineWorker.getMsisdn(), msisdn);
        assertEquals(frontLineWorker.getName(), name);
        assertEquals(frontLineWorker.getDesignation(), designation);
        assertEquals(frontLineWorker.getLocationId(), location.getExternalId());
        assertEquals(RegistrationStatus.UNREGISTERED, frontLineWorker.status());
    }

    @Test
    public void shouldUpdateFLWIfExists() {
        String msisdn = "123";
        String name = "name";
        Designation designation = Designation.AWW;
        Location location = new Location("district", "block", "panchayat", 123, 124, 125);
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, null, new Location(), RegistrationStatus.REGISTERED);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        frontLineWorker = frontLineWorkerService.createOrUpdateForImport(msisdn, name, designation, location);

        verify(allFrontLineWorkers).update(frontLineWorker);
        assertEquals(frontLineWorker.getName(), name);
        assertEquals(frontLineWorker.getDesignation(), designation);
        assertEquals(frontLineWorker.getLocationId(), location.getExternalId());
        assertEquals(RegistrationStatus.REGISTERED, frontLineWorker.status());
    }

    @Test
    public void shouldResetLastAccessTimeForJobAidAndMaxUsagePromptHeardAtBeginningOfTheMonth() {
        String callerId = "callerId";
        String randomPromptKey = "random";
        String operator = "airtel";
        String promptKey = "Max_Usage";
        String circle = "circle";
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now().minusMonths(2));
        frontLineWorker.markPromptHeard(promptKey);
        frontLineWorker.markPromptHeard(randomPromptKey);

        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(Location.getDefaultLocation());

        FrontLineWorker flwForJobAidCallerData = frontLineWorkerService.findForJobAidCallerData("callerId", "operator", circle);

        assertEquals((Object) 0, flwForJobAidCallerData.getCurrentJobAidUsage());
        assertFalse(flwForJobAidCallerData.getPromptsHeard().containsKey(promptKey));
    }

    @Test
    public void shouldUpdateJobAidState() {
        String callerId = "callerId";
        String operator = "airtel";

        int currentUsage = 20;
        int callDuration = 15;
        int newUsage = currentUsage + callDuration;

        List<String> promptIds = Arrays.asList("prompt1", "prompt2");

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setCurrentJobAidUsage(currentUsage);
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);

        frontLineWorkerService.updateJobAidState(callerId, promptIds, callDuration);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());

        FrontLineWorker captured = captor.getValue();
        assertEquals(new Integer(newUsage), captured.getCurrentJobAidUsage());
        Map<String, Integer> promptsHeard = captured.getPromptsHeard();
        assertEquals(new Integer(1), promptsHeard.get("prompt1"));
        assertEquals(new Integer(1), promptsHeard.get("prompt2"));

        assertEquals(captured.getLastJobAidAccessTime().getMonthOfYear(), DateTime.now().getMonthOfYear());
        assertEquals(captured.getLastJobAidAccessTime().getYear(), DateTime.now().getYear());
    }

    @Test
    public void shouldResetCurrentUsageAtBeginningOfMonthEvenThoughMaxUsageIsNotReached() {
        String callerId = "callerId";
        String operator = "airtel";
        String circle = "circle";
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now().minusMonths(2));
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(Location.getDefaultLocation());

        FrontLineWorker flwForJobAidCallerData = frontLineWorkerService.findForJobAidCallerData("callerId", "operator", circle);

        assertEquals((Object) 0, flwForJobAidCallerData.getCurrentJobAidUsage());
        assertFalse(flwForJobAidCallerData.getPromptsHeard().containsKey("Max_Usage"));
    }


    @Test
    public void shouldGetAllFrontLineWorkers() {
        ArrayList<FrontLineWorker> expectedFrontLineWorkerList = new ArrayList<FrontLineWorker>();
        when(allFrontLineWorkers.getAll()).thenReturn(expectedFrontLineWorkerList);

        List<FrontLineWorker> actualFrontLineWorkerList = frontLineWorkerService.getAll();

        assertEquals(expectedFrontLineWorkerList, actualFrontLineWorkerList);
    }

    @Test
    public void shouldCreateANewFrontLineWorkerWithIfDoesNotExist() {
        String callerId = "1234";
        String circle = "bihar";
        String operator = "airtel";

        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(null);

        frontLineWorkerService.findForJobAidCallerData(callerId, operator, circle);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();
        assertEquals(circle, frontLineWorker.getCircle());
        assertEquals(operator, frontLineWorker.getOperator());
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, frontLineWorker.status());
    }

}
