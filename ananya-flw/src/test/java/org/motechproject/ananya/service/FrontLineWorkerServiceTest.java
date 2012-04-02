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
import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerServiceTest {

    private FrontLineWorkerService frontLineWorkerService;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private AllSMSReferences allSMSReferences;
    @Mock
    private SendSMSService sendSMSService;
    @Mock
    private SMSPublisherService publisherService;
    @Mock
    private FrontLineWorker mockedFrontLineWorker;

    @Before
    public void setUp() {
        initMocks(this);
        frontLineWorkerService = new FrontLineWorkerService(allFrontLineWorkers, sendSMSService, publisherService, allSMSReferences);
    }

    @Test
    public void shouldCreateNewFLWIfNotPresentInDB() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("123", "name", Designation.ANM, new Location(), RegistrationStatus.REGISTERED);
        String msisdn = frontLineWorker.getMsisdn();

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(null);

        frontLineWorkerService.createOrUpdatePartiallyRegistered(msisdn, frontLineWorker.getOperator());

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
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANM, new Location(), RegistrationStatus.REGISTERED);
        frontLineWorker.setOperator("airtel");
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        FrontLineWorker frontLineWorkerFromDb = frontLineWorkerService.createOrUpdatePartiallyRegistered(msisdn, "airtel");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers, never()).update(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerFromDb);
    }

    @Test
    public void shouldNotCreateNewFLWIfAlreadyPresentInDBButUpdateWhenOperatorIsDifferent() {
        String msisdn = "123";
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, "name", Designation.ANM, new Location(), RegistrationStatus.REGISTERED);
        frontLineWorker.setOperator("vodafone");

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        FrontLineWorker frontLineWorkerFromDb = frontLineWorkerService.createOrUpdatePartiallyRegistered(msisdn, "airtel");

        verify(allFrontLineWorkers, never()).add(frontLineWorker);
        verify(allFrontLineWorkers).update(frontLineWorker);
        assertEquals(frontLineWorker, frontLineWorkerFromDb);
    }

    @Test
    public void shouldUpdatePromptsForFLW() {
        String callerId = "callerId";
        List<String> promptIds = Arrays.asList("prompt1", "prompt2");

        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(mockedFrontLineWorker);

        try {
            frontLineWorkerService.updatePromptsFor(callerId, promptIds);
        } catch (Exception e) {
        }

        verify(mockedFrontLineWorker).markPromptHeard(promptIds.get(0));
        verify(mockedFrontLineWorker).markPromptHeard(promptIds.get(1));
        verify(allFrontLineWorkers).update(mockedFrontLineWorker);
    }

    @Test
    public void shouldUpdateTheFrontLineWorkerWithUsage() {
        String callerId = "callerId";
        String operator = "airtel";
        Integer currentUsage = 20;
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);

        frontLineWorkerService.updateJobAidCurrentUsageFor(callerId, currentUsage);

        assertEquals(currentUsage, frontLineWorker.getCurrentJobAidUsage());
        verify(allFrontLineWorkers).update(frontLineWorker);
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
        String msisdn = "123";
        String name = "name";
        Designation designation = Designation.ANGANWADI;
        Location location = new Location("district", "block", "panchayat", 123, 124, 125);
        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(null);

        FrontLineWorker frontLineWorker = frontLineWorkerService.createOrUpdate(msisdn, name, designation, location, RegistrationStatus.REGISTERED);

        verify(allFrontLineWorkers).add(frontLineWorker);
        assertEquals(frontLineWorker.getMsisdn(), msisdn);
        assertEquals(frontLineWorker.getName(), name);
        assertEquals(frontLineWorker.getDesignation(), designation);
        assertEquals(frontLineWorker.getLocationId(), location.getExternalId());
    }

    @Test
    public void shouldUpdateFLWIfExists() {
        String msisdn = "123";
        String name = "name";
        Designation designation = Designation.ANGANWADI;
        Location location = new Location("district", "block", "panchayat", 123, 124, 125);
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, null, null, new Location(), RegistrationStatus.REGISTERED);

        when(allFrontLineWorkers.findByMsisdn(msisdn)).thenReturn(frontLineWorker);

        frontLineWorker = frontLineWorkerService.createOrUpdate(msisdn, name, designation, location, RegistrationStatus.REGISTERED);

        verify(allFrontLineWorkers).update(frontLineWorker);
        assertEquals(frontLineWorker.getName(), name);
        assertEquals(frontLineWorker.getDesignation(), designation);
        assertEquals(frontLineWorker.getLocationId(), location.getExternalId());
    }

    @Test
    public void shouldUpdateFLWUsageByAddingCurrentCallDuration() {
        String callerId = "callerId";
        String operator = "airtel";
        Integer currentUsage = 20;
        int callDuration = 15;
        int newUsage = currentUsage + callDuration;
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setCurrentJobAidUsage(currentUsage);
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);

        frontLineWorkerService.updateJobAidCurrentUsageFor(callerId, callDuration);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());

        FrontLineWorker captorValue = captor.getValue();
        assertEquals((Object) newUsage, captorValue.getCurrentJobAidUsage());
    }

    @Test
    public void shouldUpdateTheLastAccessTimeForFlw() {
        String callerId = "callerId";
        String operator = "airtel";

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);

        frontLineWorkerService.updateLastJobAidAccessTime(callerId);

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());

        FrontLineWorker captorValue = captor.getValue();
        assertEquals(captorValue.getLastJobAidAccessTime().getMonthOfYear(), DateTime.now().getMonthOfYear());
        assertEquals(captorValue.getLastJobAidAccessTime().getYear(), DateTime.now().getYear());
    }

    @Test
    public void shouldResetLastAccessTimeForJobAidAndMaxUsagePromptHeardAtBeginningOfTheMonth() {
        String callerId = "callerId";
        String randomPromptKey = "random";
        String operator = "airtel";
        String promptKey = "Max_Usage";
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now().minusMonths(2));
        frontLineWorker.markPromptHeard(promptKey);
        frontLineWorker.markPromptHeard(randomPromptKey);
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);

        FrontLineWorker flwForJobAidCallerData = frontLineWorkerService.getFLWForJobAidCallerData("callerId", "operator");

        assertEquals((Object) 0, flwForJobAidCallerData.getCurrentJobAidUsage());
        assertFalse(flwForJobAidCallerData.getPromptsHeard().containsKey(promptKey));
    }

    @Test
    public void shouldResetCurrentUsageAtBeginningOfMonthEvenThoughMaxUsageIsNotReached() {
        String callerId = "callerId";
        String operator = "airtel";
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());

        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now().minusMonths(2));
        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);

        FrontLineWorker flwForJobAidCallerData = frontLineWorkerService.getFLWForJobAidCallerData("callerId", "operator");

        assertEquals((Object) 0, flwForJobAidCallerData.getCurrentJobAidUsage());
        assertFalse(flwForJobAidCallerData.getPromptsHeard().containsKey("Max_Usage"));
    }

    @Test
    public void shouldAddSMSReference() throws Exception {
        String callerId = "9876543210";
        String operator = "airtel";
        String smsReferenceNumber = "0102987654321001";


        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator, Designation.ANGANWADI, new Location(), RegistrationStatus.REGISTERED);
        frontLineWorker.incrementCertificateCourseAttempts();

        when(allFrontLineWorkers.findByMsisdn(callerId)).thenReturn(frontLineWorker);
        frontLineWorkerService.addSMSReferenceNumber(callerId, smsReferenceNumber);

        ArgumentCaptor<SMSReference> smsReference = ArgumentCaptor.forClass(SMSReference.class);
        verify(allSMSReferences).update(smsReference.capture());

        SMSReference smsReferenceValue = smsReference.getValue();
        assertEquals(smsReferenceValue.getMsisdn(), callerId);
        assertEquals(smsReferenceValue.referenceNumbers(1), smsReferenceNumber);
        verify(publisherService).publishSMSSent(callerId);
    }

    @Test
    public void shouldUpdateTheFrontLineWorkerAndNotSendSMS() {
        FrontLineWorker frontLineWorker = new FrontLineWorker();

        frontLineWorkerService.update(new CertificateCourseStateFlwRequest(frontLineWorker, false));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());
        assertEquals(frontLineWorker, captor.getValue());
        verify(sendSMSService, never()).buildAndSendSMS(anyString(), anyString(), anyInt());
    }

    @Test
    public void shouldUpdateTheFrontLineWorkerAndSendSMS() {
        Location location = Location.getDefaultLocation();
        FrontLineWorker frontLineWorker = new FrontLineWorker("123","name",Designation.ASHA,location,RegistrationStatus.REGISTERED);

        frontLineWorkerService.update(new CertificateCourseStateFlwRequest(frontLineWorker, true));

        ArgumentCaptor<FrontLineWorker> captor = ArgumentCaptor.forClass(FrontLineWorker.class);
        verify(allFrontLineWorkers).update(captor.capture());
        assertEquals(frontLineWorker, captor.getValue());
        verify(sendSMSService).buildAndSendSMS(frontLineWorker.getMsisdn(),location.getExternalId(),frontLineWorker.currentCourseAttempt());
    }
}
