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

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers, locationService, allFrontLineWorkerKeys, allFailedRecordsProcessingStates, operatorService);
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
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, savedFrontLineWorker.getStatus());
        assertNotNull(savedFrontLineWorker.getFlwId());
    }

    @Test
    public void shouldNotCreateFLWIfExistsInDBAndNotUpdateFLWIfOperatorIsNotModified() {
        String msisdn = "123";
        String circle = "circle";
        String operator = "airtel";
        Location location = Location.getDefaultLocation();
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "name", Designation.ANM, location, "language", null, flwId);
        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        FrontLineWorkerCreateResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdateForCall(msisdn, operator, circle, "language");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers, never()).updateFlw(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerResponse.getFrontLineWorker());
        assertEquals(false, frontLineWorkerResponse.isModified());
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButUpdateWhenOperatorIsDifferent() {
        String msisdn = "123";
        String circle = "circle";
        String oldOperator = "vodafone";
        String newOperator = "airtel";
        Location location = new Location();

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "name", Designation.ANM, location, "language", null, flwId);
        frontLineWorker.setOperator(oldOperator);
        frontLineWorker.setCircle(circle);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorkerCreateResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdateForCall(msisdn, newOperator, circle, "language");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers).updateFlw(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerResponse.getFrontLineWorker());
        assertEquals(true, frontLineWorkerResponse.isModified());
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButUpdateWhenLanguageWasNull() {
        String msisdn = "123";
        String circle = "circle";
        String operator = "vodafone";
        Location location = new Location();

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "name", Designation.ANM, location, null, null, flwId);
        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorkerCreateResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdateForCall(msisdn, operator, circle, "language");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers).updateFlw(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerResponse.getFrontLineWorker());
        assertEquals(true, frontLineWorkerResponse.isModified());
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButNotUpdateFLWIfLanguageIsModified() {
        String msisdn = "123";
        String circle = "circle";
        String operator = "vodafone";
        String oldLanguage = "bhojpuri";
        String newLanguage = "hindi";
        Location location = Location.getDefaultLocation();

        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "name", Designation.ANM, location, oldLanguage, null, flwId);
        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorkerCreateResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdateForCall(msisdn, operator, circle, newLanguage);

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers, never()).updateFlw(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerResponse.getFrontLineWorker());
        assertEquals(false, frontLineWorkerResponse.isModified());
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButUpdateWhenCircleIsDifferent() {
        String msisdn = "123";
        String operator = "vodafone";
        Location location = new Location();
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, "name", Designation.ANM, location, "language", null, flwId);
        frontLineWorker.setOperator(operator);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);
        when(locationService.findByExternalId(frontLineWorker.getLocationId())).thenReturn(location);

        FrontLineWorkerCreateResponse frontLineWorkerResponse = frontLineWorkerService.createOrUpdateForCall(msisdn, operator, "circle", "language");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers).updateFlw(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerResponse.getFrontLineWorker());
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
        Location location = new Location("state", "district", "block", "panchayat", 1, 123, 124, 125, LocationStatus.VALID, null);
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
        Location location = new Location("state", "district", "block", "panchayat", 1, 123, 124, 125, null, null);

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

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(toFlw.getOperator(), fromFlw.getOperator());
        verify(allFrontLineWorkers).remove(toFlw);
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

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn);

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

        frontLineWorkerService.changeMsisdn(msisdn, newMsisdn);

        assertEquals(newMsisdn, fromFlw.getMsisdn());
        assertEquals(toFlw.getOperator(), fromFlw.getOperator());
        assertEquals(bookMark, fromFlw.getBookmark());
        assertEquals(reportCard, fromFlw.getReportCard());
        verify(allFrontLineWorkers).remove(toFlw);
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
