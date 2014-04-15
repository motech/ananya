package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ananya.contract.FrontLineWorkerCreateResponse;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFailedRecordsProcessingStates;
import org.motechproject.ananya.repository.AllFrontLineWorkerKeys;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllSMSReferences;

import java.util.*;

import static junit.framework.Assert.*;
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
    @Mock
    private AllFrontLineWorkerKeys allFrontLineWorkerKeys;
    @Mock
    private AllFailedRecordsProcessingStates allFailedRecordsProcessingStates;
    @Mock
    private OperatorService operatorService;

    private UUID flwId = UUID.randomUUID();
    private Location location;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers, locationService, allFrontLineWorkerKeys, allFailedRecordsProcessingStates, operatorService);
        location = new Location();
    }

    @Test
    public void shouldCreateNewFLWIfNotPresentInDB() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("123", null, "name", Designation.ANM, new Location(), "language", DateTime.now(), null);
        String msisdn = frontLineWorker.getMsisdn();

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(null);

        frontLineWorkerService.createOrUpdateForCall(msisdn, frontLineWorker.getOperator(), "circle", "language");

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).add(captor.capture());
        FrontLineWorker savedFrontLineWorker = captor.getValue();
        assertEquals(frontLineWorker.getMsisdn(), savedFrontLineWorker.getMsisdn());
        assertEquals(frontLineWorker.getOperator(), savedFrontLineWorker.getOperator());
        assertEquals(new Integer(0), savedFrontLineWorker.getCurrentJobAidUsage());
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, savedFrontLineWorker.getStatus());
        assertNotNull(savedFrontLineWorker.getFlwId());
    }

    @Test
    public void shouldNotCreateFLWIfExistsInDBAndNotUpdateFLWIfOperatorIsNotModified() {
        String msisdn = "123";
        String circle = "circle";
        String operator = "airtel";
        location = Location.getDefaultLocation();
        Integer currentJobAidUsage = 2;
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "name", Designation.ANM, location, "language", null, flwId);
        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setCurrentJobAidUsage(currentJobAidUsage);
        frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        FrontLineWorkerCreateResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdateForCall(msisdn, operator, circle, "language");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers, never()).updateFlw(frontLineWorker);
        FrontLineWorker flw = frontLineWorkerResponse.getFrontLineWorker();
        assertEquals(frontLineWorker, flw);
        assertEquals(currentJobAidUsage, flw.getCurrentJobAidUsage());
        assertEquals(false, frontLineWorkerResponse.isModified());
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButUpdateWhenOperatorIsDifferent() {
        String msisdn = "123";
        String circle = "circle";
        String oldOperator = "vodafone";
        String newOperator = "airtel";
        Integer minimumResetLimit = 0;

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "name", Designation.ANM, location, "language", null, flwId);
        frontLineWorker.setOperator(oldOperator);
        frontLineWorker.setCircle(circle);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorkerCreateResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdateForCall(msisdn, newOperator, circle, "language");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers).updateFlw(frontLineWorker);
        FrontLineWorker flw = frontLineWorkerResponse.getFrontLineWorker();
        assertEquals(frontLineWorker, flw);
        assertEquals(minimumResetLimit, flw.getCurrentJobAidUsage());
        assertEquals(true, frontLineWorkerResponse.isModified());
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButUpdateWhenLanguageWasNull() {
        String msisdn = "123";
        String circle = "circle";
        String operator = "vodafone";
        Integer currentJobAidUsage = 3;

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "name", Designation.ANM, location, null, null, flwId);
        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setCurrentJobAidUsage(currentJobAidUsage);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorkerCreateResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdateForCall(msisdn, operator, circle, "language");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers).updateFlw(frontLineWorker);
        FrontLineWorker flw = frontLineWorkerResponse.getFrontLineWorker();
        assertEquals(frontLineWorker, flw);
        assertEquals(currentJobAidUsage, flw.getCurrentJobAidUsage());
        assertEquals(true, frontLineWorkerResponse.isModified());
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButNotUpdateFLWIfLanguageIsModified() {
        String msisdn = "123";
        String circle = "circle";
        String operator = "vodafone";
        String oldLanguage = "bhojpuri";
        String newLanguage = "hindi";
        Integer usage = 1;
        location = Location.getDefaultLocation();

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "name", Designation.ANM, location, oldLanguage, null, flwId);
        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setCurrentJobAidUsage(usage);
        frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorkerCreateResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdateForCall(msisdn, operator, circle, newLanguage);

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers, never()).updateFlw(frontLineWorker);
        FrontLineWorker flw = frontLineWorkerResponse.getFrontLineWorker();
        assertEquals(frontLineWorker, flw);
        assertEquals(usage, flw.getCurrentJobAidUsage());
        assertEquals(false, frontLineWorkerResponse.isModified());
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButUpdateWhenCircleIsDifferent() {
        String msisdn = "123";
        String operator = "vodafone";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "name", Designation.ANM, location, "language", null, flwId);
        frontLineWorker.setOperator(operator);
        Integer usage = 1;
        frontLineWorker.setCurrentJobAidUsage(usage);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorkerCreateResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdateForCall(msisdn, operator, "circle", "language");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers).updateFlw(frontLineWorker);
        FrontLineWorker flw = frontLineWorkerResponse.getFrontLineWorker();
        assertEquals(frontLineWorker, flw);
        assertEquals(usage, flw.getCurrentJobAidUsage());
        assertEquals(true, frontLineWorkerResponse.isModified());
    }

    @Test
    public void shouldFindByCallerId() {
        FrontLineWorker expectedFrontLineWorker = new FrontLineWorker();
        when(allFrontLineWorkers.findByMsisdn("123")).thenReturn(expectedFrontLineWorker);

        FrontLineWorker frontLineWorker = frontLineWorkerService.findByCallerId("123");

        assertEquals(expectedFrontLineWorker, frontLineWorker);
    }

    @Test
    public void shouldUpdateFLWIfExists() {
        String msisdn = "123";
        String name = "name";
        Designation designation = Designation.AWW;
        location = new Location("state", "district", "block", "panchayat", 1, 123, 124, 125, LocationStatus.VALID, null);
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, null, null, new Location(), "language", null, flwId);
        frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        String alternateContactNumber = "1";
        frontLineWorker = frontLineWorkerService.createOrUpdate(new FrontLineWorker(msisdn, alternateContactNumber, name, designation, location, "language", null, flwId), location);

        verify(allFrontLineWorkers).update(frontLineWorker);
        assertEquals(frontLineWorker.getName(), name);
        assertEquals(frontLineWorker.getDesignation(), designation);
        assertEquals(frontLineWorker.getLocationId(), location.getExternalId());
        assertEquals(RegistrationStatus.REGISTERED, frontLineWorker.getStatus());
        assertEquals(alternateContactNumber, frontLineWorker.getAlternateContactNumber());
    }

    @Test
    public void shouldResetCurrentUsageAtBeginningOfMonthEvenThoughMaxUsageIsNotReached() {
        String callerId = "callerId";
        String operator = "airtel";
        String circle = "circle";
        String language = "language";
        String maxUsagePrompt = "Max_Usage";

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator, circle, language);
        frontLineWorker.markPromptHeard(maxUsagePrompt);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now().minusMonths(2));
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);

        FrontLineWorker flwForJobAidCallerData = frontLineWorkerService.findForJobAidCallerData("callerId");

        assertEquals((Object) 0, flwForJobAidCallerData.getCurrentJobAidUsage());
        assertFalse(flwForJobAidCallerData.getPromptsHeard().containsKey(maxUsagePrompt));
    }

    @Test
    public void shouldUpdateJobAidState() {
        String callerId = "callerId";
        String operator = "airtel";

        int currentUsage = 20;
        int callDuration = 15;
        int newUsage = currentUsage + 60;

        List<String> promptIds = Arrays.asList("prompt1", "prompt2");

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator, "circle", "language");
        frontLineWorker.setCurrentJobAidUsage(currentUsage);
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);
        when(operatorService.usageByPulseInMilliSec(operator, callDuration)).thenReturn(60);

        frontLineWorkerService.updateJobAidState(frontLineWorker, promptIds, callDuration);

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
    public void shouldGetAllFrontLineWorkers() {
        ArrayList<FrontLineWorker> expectedFrontLineWorkerList = new ArrayList<FrontLineWorker>();
        when(allFrontLineWorkers.getAll()).thenReturn(expectedFrontLineWorkerList);

        List<FrontLineWorker> actualFrontLineWorkerList = frontLineWorkerService.getAll();

        assertEquals(expectedFrontLineWorkerList, actualFrontLineWorkerList);
    }

    @Test
    public void shouldCreateANewFrontLineWorkerWithIfDoesNotExist() {
        String callerId = "1234";
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(null);

        FrontLineWorker frontLineWorker = frontLineWorkerService.findForJobAidCallerData(callerId);

        assertNull(frontLineWorker);
    }

    @Test
    public void shouldNotUpdateExistingFLWInDBWhenUpdateIsNotSuccessful() {
        FrontLineWorker existingFrontLineWorker = mock(FrontLineWorker.class);
        String msisdn = "123";
        String name = "name";
        Designation designation = Designation.AWW;
        DateTime lastModified = DateTime.now();
        VerificationStatus verificationStatus = VerificationStatus.SUCCESS;
        UUID flwId = UUID.randomUUID();
        location = new Location("state", "district", "block", "panchayat", 1, 123, 124, 125, null, null);

        when(existingFrontLineWorker.update(name, designation, location, lastModified, flwId, verificationStatus, null)).thenReturn(false);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(existingFrontLineWorker);

        frontLineWorkerService.createOrUpdate(new FrontLineWorker(msisdn, null, name, designation, location, "language", lastModified.minusDays(1), flwId), location);

        verify(allFrontLineWorkers, never()).update(any(FrontLineWorker.class));
    }

    @Test
    public void shouldGetLastFailedRecordsProcessedDate_whenAvailable() {
        ArrayList<FailedRecordsProcessingState> failedRecordsProcessingStates = new ArrayList<FailedRecordsProcessingState>();
        FailedRecordsProcessingState failedRecordsProcessingState = new FailedRecordsProcessingState(DateTime.now());
        failedRecordsProcessingStates.add(failedRecordsProcessingState);
        when(allFailedRecordsProcessingStates.getAll()).thenReturn(failedRecordsProcessingStates);

        DateTime lastFailedRecordsProcessedDate = frontLineWorkerService.getLastFailedRecordsProcessedDate();

        assertEquals(failedRecordsProcessingState.getLastProcessedDate(), lastFailedRecordsProcessedDate);
    }

    @Test
    public void shouldReturnNullForGetLastFailedRecordsProcessedDate_whenNotAvailable() {
        List<FailedRecordsProcessingState> failedRecordsProcessingStates = Collections.emptyList();
        when(allFailedRecordsProcessingStates.getAll()).thenReturn(failedRecordsProcessingStates);

        DateTime lastFailedRecordsProcessedDate = frontLineWorkerService.getLastFailedRecordsProcessedDate();

        assertNull(lastFailedRecordsProcessedDate);
    }

    @Test
    public void shouldUpdateLastFailedRecordsProcessedDate() {
        DateTime recordDate = DateTime.now().plusDays(1);
        ArrayList<FailedRecordsProcessingState> failedRecordsProcessingStates = new ArrayList<FailedRecordsProcessingState>();
        FailedRecordsProcessingState failedRecordsProcessingState = mock(FailedRecordsProcessingState.class);
        failedRecordsProcessingStates.add(failedRecordsProcessingState);
        when(allFailedRecordsProcessingStates.getAll()).thenReturn(failedRecordsProcessingStates);

        frontLineWorkerService.updateLastFailedRecordsProcessedDate(recordDate);

        verify(failedRecordsProcessingState).update(recordDate);
        verify(allFailedRecordsProcessingStates).update(failedRecordsProcessingState);
    }

    @Test
    public void shouldAddLastFailedRecordsProcessedDate() {
        DateTime recordDate = DateTime.now().plusDays(1);
        List<FailedRecordsProcessingState> failedRecordsProcessingStates = Collections.emptyList();
        when(allFailedRecordsProcessingStates.getAll()).thenReturn(failedRecordsProcessingStates);

        frontLineWorkerService.updateLastFailedRecordsProcessedDate(recordDate);

        ArgumentCaptor<FailedRecordsProcessingState> failedRecordsProcessingStateArgumentCaptor = ArgumentCaptor.forClass(FailedRecordsProcessingState.class);
        verify(allFailedRecordsProcessingStates).add(failedRecordsProcessingStateArgumentCaptor.capture());
        FailedRecordsProcessingState failedRecordsProcessingState = failedRecordsProcessingStateArgumentCaptor.getValue();
        assertEquals(recordDate, failedRecordsProcessingState.getLastProcessedDate());
    }

    @Test
    public void shouldUpdateFrontLineWorkerLocation() {
        Location newLocation = new Location("S1", "D1", "B1", "P1", 1, 1, 1, 1, null, null);
        Location oldLocation = new Location("S1", "D2", "B2", "P2", 1, 1, 2, 1, null, null);
        ArrayList<FrontLineWorker> frontLineWorkers = new ArrayList<>();
        String msisdn = "123";
        frontLineWorkers.add(new FrontLineWorker(msisdn, "airtel", "bihar", "language"));
        when(allFrontLineWorkers.findByLocationId(oldLocation.getExternalId())).thenReturn(frontLineWorkers);

        frontLineWorkerService.updateLocation(oldLocation, newLocation);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).updateFlw(captor.capture());
        FrontLineWorker frontLineWorker = captor.getValue();
        assertEquals(newLocation.getExternalId(), frontLineWorker.getLocationId());
        assertEquals(msisdn, frontLineWorker.getMsisdn());
    }

    @Test
    public void shouldChangeMsisdn() {
        String msisdn = "123";
        String newMsisdn = "456";
        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        FrontLineWorker toFlw = createFLW(newMsisdn, "Airtel");
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(toFlw);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(toFlw.getOperator(), fromFlw.getOperator());
        verify(allFrontLineWorkers).remove(toFlw);
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldNotCopyOverOperatorWhenNewOperatorIsMissing() {
        String msisdn = "123";
        String newMsisdn = "456";
        String operator = "Voda";
        FrontLineWorker fromFlw = createFLW(msisdn, operator);
        FrontLineWorker toFlw = createFLW(newMsisdn, null);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(toFlw);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(operator, fromFlw.getOperator());
        verify(allFrontLineWorkers).remove(toFlw);
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldNotCopyOverNewCourseAttemptsWhenNull() {
        String msisdn = "123";
        String newMsisdn = "456";
        Integer certificateCourseAttempts = 1;
        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        fromFlw.setCertificateCourseAttempts(certificateCourseAttempts);
        FrontLineWorker toFlw = createFLW(msisdn, "abba");
        toFlw.setCertificateCourseAttempts(null);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(toFlw);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(certificateCourseAttempts, fromFlw.currentCourseAttempts());
        verify(allFrontLineWorkers).remove(toFlw);
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldCopyOverNewCourseAttempts() {
        String msisdn = "123";
        String newMsisdn = "456";
        Integer certificateCourseAttempts = 1;
        Integer newCertificateCourseAttempts = 2;
        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        fromFlw.setCertificateCourseAttempts(certificateCourseAttempts);
        FrontLineWorker toFlw = createFLW(newMsisdn, null);
        toFlw.setCertificateCourseAttempts(newCertificateCourseAttempts);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(toFlw);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(newCertificateCourseAttempts, fromFlw.currentCourseAttempts());
        verify(allFrontLineWorkers).remove(toFlw);
        verify(allFrontLineWorkers).update(fromFlw);
    }


    @Test
    public void shouldCopyOverNewJobAidUsage() {
        Integer newCurrentJobAidUsage = 1;
        String msisdn = "123";
        String newMsisdn = "456";
        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        fromFlw.setCurrentJobAidUsage(2);
        FrontLineWorker toFlw = createFLW(newMsisdn, null);
        toFlw.setCurrentJobAidUsage(newCurrentJobAidUsage);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(toFlw);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(newCurrentJobAidUsage, fromFlw.getCurrentJobAidUsage());
        verify(allFrontLineWorkers).remove(toFlw);
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldCopyOverNewLastJobAidAccessTime() {
        String msisdn = "123";
        String newMsisdn = "456";
        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        DateTime newJobAidAccessTime = new DateTime();
        fromFlw.setLastJobAidAccessTime(new DateTime().minusDays(1));
        FrontLineWorker toFlw = createFLW(newMsisdn, null);
        toFlw.setLastJobAidAccessTime(newJobAidAccessTime);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(toFlw);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(newJobAidAccessTime, fromFlw.getLastJobAidAccessTime());
        verify(allFrontLineWorkers).remove(toFlw);
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldNotCopyOverFieldsWhenNewFlwDoesNotExist() {
        String msisdn = "123";
        String newMsisdn = "456";
        Integer currentJobAidUsage = 2;
        Integer certificateCourseAttempts = 3;
        DateTime lastJobAidAccessTime = new DateTime();
        ReportCard reportCard = new ReportCard();
        BookMark bookMark = new BookMark();
        String operator = "Voda";

        FrontLineWorker fromFlw = createFLW(msisdn, operator);
        fromFlw.setCurrentJobAidUsage(currentJobAidUsage);
        fromFlw.setCertificateCourseAttempts(certificateCourseAttempts);
        fromFlw.setLastJobAidAccessTime(lastJobAidAccessTime);
        fromFlw.setReportCard(reportCard);
        fromFlw.setBookMark(bookMark);
        fromFlw.setPromptsHeard(Collections.singletonMap("1", 1));

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(null);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(operator, fromFlw.getOperator());
        assertEquals(currentJobAidUsage, fromFlw.getCurrentJobAidUsage());
        assertEquals(certificateCourseAttempts, fromFlw.currentCourseAttempts());
        assertNull(fromFlw.getLastJobAidAccessTime());
        assertEquals(0, fromFlw.getPromptsHeard().size());
        assertEquals(reportCard, fromFlw.getReportCard());
        assertEquals(bookMark, fromFlw.getBookmark());
        verify(allFrontLineWorkers, never()).remove(any(FrontLineWorker.class));
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldCopyOverNewLastJobAidAccessTimeEvenWhenNull() {
        String msisdn = "123";
        String newMsisdn = "456";
        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        DateTime lastJobAidAccessTime = new DateTime().minusDays(1);
        fromFlw.setLastJobAidAccessTime(lastJobAidAccessTime);
        FrontLineWorker toFlw = createFLW(newMsisdn, null);
        toFlw.setLastJobAidAccessTime(null);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(toFlw);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(null, fromFlw.getLastJobAidAccessTime());
        verify(allFrontLineWorkers).remove(toFlw);
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldNotCopyOverNewJobAidUsageWhenNull() {
        Integer currentJobAidUsage = 2;
        String msisdn = "123";
        String newMsisdn = "456";
        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        fromFlw.setCurrentJobAidUsage(currentJobAidUsage);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(null);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(currentJobAidUsage, fromFlw.getCurrentJobAidUsage());
        verify(allFrontLineWorkers, never()).remove(any(FrontLineWorker.class));
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldCopyOverHighestBookMarkAndScores() {
        String msisdn = "123";
        String newMsisdn = "456";
        BookMark lowerBookMark = new BookMark("A", 5, 2);
        BookMark higherBookMark = new BookMark("B", 7, 2);
        ReportCard higherReport = createReportCardWithSomeScores();
        higherReport.addScore(new Score("3", "3", true));
        ReportCard lowerReport = createReportCardWithSomeScores();

        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        FrontLineWorker toFlw = createFLW(newMsisdn, "Airtel");
        setBookMarkAndReportCard(lowerBookMark, higherReport, fromFlw);
        setBookMarkAndReportCard(higherBookMark, lowerReport, toFlw);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(toFlw);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(toFlw.getOperator(), fromFlw.getOperator());
        assertEquals(toFlw.getBookmark(), fromFlw.getBookmark());
        assertEquals(higherReport, fromFlw.getReportCard());
        verify(allFrontLineWorkers).remove(toFlw);
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldNotCopyOverBookMarkAndScores() {
        String msisdn = "123";
        String newMsisdn = "456";
        BookMark bookMark = new BookMark("A", 5, 2);
        ReportCard reportCard = createReportCardWithSomeScores();
        reportCard.addScore(new Score("3", "3", true));

        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        FrontLineWorker toFlw = createFLW(newMsisdn, "Airtel");
        setBookMarkAndReportCard(bookMark, reportCard, fromFlw);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(toFlw);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(toFlw.getOperator(), fromFlw.getOperator());
        assertEquals(bookMark, fromFlw.getBookmark());
        assertEquals(reportCard, fromFlw.getReportCard());
        verify(allFrontLineWorkers).remove(toFlw);
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldRecalculateRegistrationStatus() {
        String msisdn = "123";
        String newMsisdn = "456";
        location = new Location("S", "D", "B", "P", 1, 1, 1, 1, LocationStatus.VALID, DateTime.now());
        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        FrontLineWorker toFlw = createFLW(newMsisdn, "Airtel");
        toFlw.setRegistrationStatus(RegistrationStatus.REGISTERED);
        fromFlw.setName("ABC");
        fromFlw.setLocation(location);
        fromFlw.setDesignation(Designation.ANM);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(toFlw);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(toFlw.getOperator(), fromFlw.getOperator());
        assertEquals(RegistrationStatus.REGISTERED, fromFlw.getStatus());
        verify(allFrontLineWorkers).remove(toFlw);
        verify(allFrontLineWorkers).update(fromFlw);
    }

    @Test
    public void shouldSetRegistrationStatusAsUnRegisteredIfNewMsisdnIsNotRegistered() {
        String msisdn = "123";
        String newMsisdn = "456";
        location = new Location("S", "D", "B", "P", 1, 1, 1, 1, LocationStatus.VALID, DateTime.now());
        FrontLineWorker fromFlw = createFLW(msisdn, "Voda");
        fromFlw.setName("ABC");
        fromFlw.setLocation(location);
        fromFlw.setDesignation(Designation.ANM);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(fromFlw);
        when(allFrontLineWorkers.findByMsisdn(newMsisdn)).thenReturn(null);

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn, location);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(RegistrationStatus.UNREGISTERED, fromFlw.getStatus());
        verify(allFrontLineWorkers, never()).remove(any(FrontLineWorker.class));
        verify(allFrontLineWorkers).update(fromFlw);
    }

    private void setBookMarkAndReportCard(BookMark bookMark1, ReportCard reportCard1, FrontLineWorker fromFlw) {
        fromFlw.setBookMark(bookMark1);
        fromFlw.setReportCard(reportCard1);
    }

    private ReportCard createReportCardWithSomeScores() {
        ReportCard reportCard = new ReportCard();
        reportCard.addScore(new Score("1", "1", true));
        reportCard.addScore(new Score("2", "2", true));
        return reportCard;
    }

    private FrontLineWorker createFLW(String newMsisdn, String operator) {
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.setMsisdn(newMsisdn);
        frontLineWorker.setOperator(operator);
        return frontLineWorker;
    }
}
