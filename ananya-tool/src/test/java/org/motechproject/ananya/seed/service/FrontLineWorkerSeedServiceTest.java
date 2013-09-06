package org.motechproject.ananya.seed.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;

import java.lang.reflect.Field;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FrontLineWorkerSeedServiceTest {

    private FrontLineWorkerSeedService seedService;
    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;


    @Before
    public void setUp() {
        seedService = new FrontLineWorkerSeedService(null, allFrontLineWorkers, allFrontLineWorkerDimensions, null, null,null, null);
    }

    @Test
    public void shouldDeduceCorrectFLWStatusBasedOnInformationOld() {
    	String language= "language";
        Location completeLocation = new Location("state", "district", "block", "panchayat", 1, 1, 1, 1, null, null);
        Location incompleteLocation = new Location("state", "district", "block", "", 1, 1, 1, 0, null, null);
        Location defaultLocation = Location.getDefaultLocation();

        UUID flwId = UUID.randomUUID();
        FrontLineWorker flwWithCompleteDetails = new FrontLineWorker(
                "1234", null, "name", Designation.ANM, completeLocation, language, null, flwId);
        FrontLineWorker flwWithoutName = new FrontLineWorker(
                "1234", null, "", Designation.ANM, completeLocation, language, null, flwId);
        FrontLineWorker flwWithoutDesignation = new FrontLineWorker(
                "1234", null, "name", null, completeLocation, language, null, flwId);
        FrontLineWorker flwWithInvalidDesignation = new FrontLineWorker(
                "1234", null, "name", null, completeLocation, language, null, flwId);
        FrontLineWorker flwWithDefaultLocation = new FrontLineWorker(
                "1234", null, "name", Designation.ANM, defaultLocation, language, null, flwId);
        FrontLineWorker flwWithIncompleteLocation = new FrontLineWorker(
                "1234", null, "name", Designation.ANM, incompleteLocation, language, null, flwId);
        FrontLineWorker flwWithNoDetails = new FrontLineWorker(
                "1234", null, "", null, defaultLocation, language, null, flwId);

        assertEquals(RegistrationStatus.REGISTERED,
                seedService.deduceRegistrationStatusOld(flwWithCompleteDetails, completeLocation));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED,
                seedService.deduceRegistrationStatusOld(flwWithoutName, completeLocation));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED,
                seedService.deduceRegistrationStatusOld(flwWithoutDesignation, completeLocation));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED,
                seedService.deduceRegistrationStatusOld(flwWithInvalidDesignation, completeLocation));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED,
                seedService.deduceRegistrationStatusOld(flwWithDefaultLocation, defaultLocation));
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED,
                seedService.deduceRegistrationStatusOld(flwWithIncompleteLocation, incompleteLocation));
        assertEquals(RegistrationStatus.UNREGISTERED,
                seedService.deduceRegistrationStatusOld(flwWithNoDetails, defaultLocation));
    }

    @Test
    public void shouldMergeFLWs_CourseAttemptSame() {
    	String language= "language";
        FrontLineWorker frontLineWorker1 = new FrontLineWorker("9876543210", "airtel", null, language);
        frontLineWorker1.incrementCertificateCourseAttempts();
        frontLineWorker1.setBookMark(new BookMark("type", 2, 2));
        frontLineWorker1.reportCard().addScore(new Score("1", "4", true, "987654321-1"));
        frontLineWorker1.reportCard().addScore(new Score("1", "5", true, "987654321-1"));
        frontLineWorker1.markPromptHeard("prompt1");

        FrontLineWorker frontLineWorker2 = new FrontLineWorker("9876543210", "airtel", null, language);
        frontLineWorker2.incrementCertificateCourseAttempts();
        frontLineWorker2.setBookMark(new BookMark("type", 2, 3));
        frontLineWorker2.reportCard().addScore(new Score("1", "4", false, "987654321-2"));
        frontLineWorker2.reportCard().addScore(new Score("1", "5", true, "987654321-2"));
        frontLineWorker1.markPromptHeard("prompt2");

        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);

        assertEquals(1, (int) frontLineWorker1.currentCourseAttempts());
        assertEquals(3, (int) frontLineWorker1.bookMark().getLessonIndex());
        assertEquals(1, (int) frontLineWorker1.reportCard().totalScore());
        assertEquals(2, frontLineWorker1.getPromptsHeard().size());
    }

    @Test
    public void shouldMergeFLWs_CourseAttemptDifferent() {
    	String language= "language";
        FrontLineWorker frontLineWorker1 = new FrontLineWorker("9876543210", "airtel", null, language);
        frontLineWorker1.incrementCertificateCourseAttempts();
        frontLineWorker1.setBookMark(new BookMark(null, null, null));
        frontLineWorker1.markPromptHeard("welcome");

        FrontLineWorker frontLineWorker2 = new FrontLineWorker("9876543210", "airtel", null, language);
        frontLineWorker2.incrementCertificateCourseAttempts();
        frontLineWorker2.incrementCertificateCourseAttempts();
        frontLineWorker2.setBookMark(new BookMark("type", 2, 3));
        frontLineWorker2.reportCard().addScore(new Score("1", "4", true, "987654321-2"));
        frontLineWorker2.reportCard().addScore(new Score("1", "5", true, "987654321-2"));
        frontLineWorker1.markPromptHeard("prompt2");

        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);

        assertEquals(2, (int) frontLineWorker1.currentCourseAttempts());
        assertEquals(3, (int) frontLineWorker1.bookMark().getLessonIndex());
        assertEquals(2, (int) frontLineWorker1.reportCard().totalScore());
        assertEquals(2, frontLineWorker1.getPromptsHeard().size());
    }

    @Test
    public void shouldMergeFLWs_CourseAttemptOfFLW2Lesser() {
    	String language= "language";
        FrontLineWorker frontLineWorker1 = new FrontLineWorker("9876543210", "airtel", null, language);
        frontLineWorker1.incrementCertificateCourseAttempts();
        frontLineWorker1.incrementCertificateCourseAttempts();
        frontLineWorker1.setBookMark(new BookMark(null, null, null));
        frontLineWorker1.markPromptHeard("prompt1");

        FrontLineWorker frontLineWorker2 = new FrontLineWorker("9876543210", "airtel", null, language);
        frontLineWorker2.incrementCertificateCourseAttempts();
        frontLineWorker2.setBookMark(new BookMark("type", 2, 3));
        frontLineWorker2.reportCard().addScore(new Score("1", "4", true, "987654321-2"));
        frontLineWorker2.reportCard().addScore(new Score("1", "5", true, "987654321-2"));
        frontLineWorker1.markPromptHeard("prompt2");
        frontLineWorker1.markPromptHeard("prompt3");

        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);

        assertEquals(2, (int) frontLineWorker1.currentCourseAttempts());
        assertNull(frontLineWorker1.bookMark().getLessonIndex());
        assertEquals(0, (int) frontLineWorker1.reportCard().totalScore());
        assertEquals(3, frontLineWorker1.getPromptsHeard().size());
    }

    @Test
    public void shouldMergeFLWs_OtherFields() throws Exception {
        DateTime now = DateTime.now();
        String language= "language";
        FrontLineWorker frontLineWorker1 = new FrontLineWorker("9876543210", "airtel", null, language);
        FrontLineWorker frontLineWorker2 = new FrontLineWorker("9876543210", "airtel", null, language);

        frontLineWorker1.setName(null);
        frontLineWorker2.setName("Ramya");
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals("Ramya", frontLineWorker1.getName());

        frontLineWorker1.setName("Chidiya");
        frontLineWorker2.setName(null);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals("Chidiya", frontLineWorker1.getName());

        frontLineWorker1.setDesignation(null);
        frontLineWorker2.setDesignation(Designation.ANM);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(Designation.ANM, frontLineWorker1.getDesignation());

        frontLineWorker1.setCircle(null);
        frontLineWorker2.setCircle("bihar");
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals("bihar", frontLineWorker1.getCircle());

        frontLineWorker1.setLocation(Location.getDefaultLocation());
        frontLineWorker2.setLocation(new Location() {
            public String getExternalId() {
                return "expectedLocationId";
            }
        });
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals("expectedLocationId", frontLineWorker1.getLocationId());


        Field lastModifiedField = FrontLineWorker.class.getDeclaredField("lastModified");
        lastModifiedField.setAccessible(true);

        lastModifiedField.set(frontLineWorker1, null);
        lastModifiedField.set(frontLineWorker2, now);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now, frontLineWorker1.getLastModified());

        lastModifiedField.set(frontLineWorker1, now);
        lastModifiedField.set(frontLineWorker2, null);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now, frontLineWorker1.getLastModified());

        lastModifiedField.set(frontLineWorker1, now);
        lastModifiedField.set(frontLineWorker2, now.plusDays(1));
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now.plusDays(1), frontLineWorker1.getLastModified());

        lastModifiedField.set(frontLineWorker1, now);
        lastModifiedField.set(frontLineWorker2, now.minusDays(1));
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now, frontLineWorker1.getLastModified());


        frontLineWorker1.setLastJobAidAccessTime(null);
        frontLineWorker2.setLastJobAidAccessTime(now);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now, frontLineWorker1.getLastJobAidAccessTime());

        frontLineWorker1.setLastJobAidAccessTime(now);
        frontLineWorker2.setLastJobAidAccessTime(null);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now, frontLineWorker1.getLastJobAidAccessTime());

        frontLineWorker1.setLastJobAidAccessTime(now);
        frontLineWorker2.setLastJobAidAccessTime(now.plusDays(1));
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now.plusDays(1), frontLineWorker1.getLastJobAidAccessTime());

        frontLineWorker1.setLastJobAidAccessTime(now);
        frontLineWorker2.setLastJobAidAccessTime(now.minusDays(1));
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now, frontLineWorker1.getLastJobAidAccessTime());


        frontLineWorker1.setRegisteredDate(null);
        frontLineWorker2.setRegisteredDate(now);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now, frontLineWorker1.getRegisteredDate());

        frontLineWorker1.setRegisteredDate(now);
        frontLineWorker2.setRegisteredDate(null);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now, frontLineWorker1.getRegisteredDate());

        frontLineWorker1.setRegisteredDate(now);
        frontLineWorker2.setRegisteredDate(now.plusDays(1));
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now, frontLineWorker1.getRegisteredDate());

        frontLineWorker1.setRegisteredDate(now);
        frontLineWorker2.setRegisteredDate(now.minusDays(1));
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(now.minusDays(1), frontLineWorker1.getRegisteredDate());


        frontLineWorker1.setCurrentJobAidUsage(null);
        frontLineWorker2.setCurrentJobAidUsage(100);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(100, (int) frontLineWorker1.getCurrentJobAidUsage());

        frontLineWorker1.setCurrentJobAidUsage(100);
        frontLineWorker2.setCurrentJobAidUsage(null);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(100, (int) frontLineWorker1.getCurrentJobAidUsage());

        frontLineWorker1.setCurrentJobAidUsage(100);
        frontLineWorker2.setCurrentJobAidUsage(200);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(200, (int) frontLineWorker1.getCurrentJobAidUsage());

        frontLineWorker1.setCurrentJobAidUsage(200);
        frontLineWorker2.setCurrentJobAidUsage(100);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(200, (int) frontLineWorker1.getCurrentJobAidUsage());


        frontLineWorker1.setRegistrationStatus(RegistrationStatus.UNREGISTERED);
        frontLineWorker2.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, frontLineWorker1.getStatus());

        frontLineWorker1.setRegistrationStatus(RegistrationStatus.REGISTERED);
        frontLineWorker2.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);
        assertEquals(RegistrationStatus.REGISTERED, frontLineWorker1.getStatus());
    }

    @Test
    public void shouldCopyIDsFromFLWDimension() {
        FrontLineWorker frontlineWorker = new FrontLineWorker("911234567890", "Airtel", "Circle", "language");
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension();
        when(allFrontLineWorkerDimensions.fetchFor(frontlineWorker.msisdn())).thenReturn(frontLineWorkerDimension);

        seedService.copyFlwIdFromFLWDimension(frontlineWorker);

        verify(allFrontLineWorkerDimensions).fetchFor(frontlineWorker.msisdn());
        assertEquals(frontLineWorkerDimension.getFlwId(), frontlineWorker.getFlwId());
        verify(allFrontLineWorkers).update(frontlineWorker);
    }
}
