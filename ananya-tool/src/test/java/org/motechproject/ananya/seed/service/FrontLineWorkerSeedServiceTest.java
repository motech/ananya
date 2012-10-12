package org.motechproject.ananya.seed.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllLocationDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FrontLineWorkerSeedServiceTest {

    private FrontLineWorkerSeedService seedService;
    @Mock
    private DataAccessTemplate template;
    @Mock
    private AllLocations allLocations;
    @Mock
    private AllFrontLineWorkers allFrontLineWorkers;
    @Mock
    private AllRegistrationMeasures allRegistrationMeasures;
    @Mock
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    @Mock
    private AllTimeDimensions allTimeDimensions;
    @Mock
    private AllLocationDimensions allLocationDimensions;

    @Before
    public void setUp() {
        initMocks(this);
        seedService = new FrontLineWorkerSeedService(template, allFrontLineWorkers, allFrontLineWorkerDimensions, allRegistrationMeasures,
                allLocations, allTimeDimensions, allLocationDimensions);
    }

    @Test
    public void shouldDeduceCorrectFLWStatusBasedOnInformationOld() {
        Location completeLocation = new Location("district", "block", "panchayat", 1, 1, 1);
        Location incompleteLocation = new Location("district", "block", "", 1, 1, 0);
        Location defaultLocation = Location.getDefaultLocation();

        FrontLineWorker flwWithCompleteDetails = new FrontLineWorker(
                "1234", "name", Designation.ANM, completeLocation, null, "flwGuid");
        FrontLineWorker flwWithoutName = new FrontLineWorker(
                "1234", "", Designation.ANM, completeLocation, null, "flwGuid");
        FrontLineWorker flwWithoutDesignation = new FrontLineWorker(
                "1234", "name", null, completeLocation, null, "flwGuid");
        FrontLineWorker flwWithInvalidDesignation = new FrontLineWorker(
                "1234", "name", null, completeLocation, null, "flwGuid");
        FrontLineWorker flwWithDefaultLocation = new FrontLineWorker(
                "1234", "name", Designation.ANM, defaultLocation, null, "flwGuid");
        FrontLineWorker flwWithIncompleteLocation = new FrontLineWorker(
                "1234", "name", Designation.ANM, incompleteLocation, null, "flwGuid");
        FrontLineWorker flwWithNoDetails = new FrontLineWorker(
                "1234", "", null, defaultLocation, null, "flwGuid");

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
        FrontLineWorker frontLineWorker1 = new FrontLineWorker("9876543210", "airtel", null);
        frontLineWorker1.incrementCertificateCourseAttempts();
        frontLineWorker1.addBookMark(new BookMark("type", 2, 2));
        frontLineWorker1.reportCard().addScore(new Score("1", "4", true, "987654321-1"));
        frontLineWorker1.reportCard().addScore(new Score("1", "5", true, "987654321-1"));
        frontLineWorker1.markPromptHeard("prompt1");

        FrontLineWorker frontLineWorker2 = new FrontLineWorker("9876543210", "airtel", null);
        frontLineWorker2.incrementCertificateCourseAttempts();
        frontLineWorker2.addBookMark(new BookMark("type", 2, 3));
        frontLineWorker2.reportCard().addScore(new Score("1", "4", false, "987654321-2"));
        frontLineWorker2.reportCard().addScore(new Score("1", "5", true, "987654321-2"));
        frontLineWorker1.markPromptHeard("prompt2");

        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);

        assertEquals(1, (int) frontLineWorker1.currentCourseAttempt());
        assertEquals(3, (int) frontLineWorker1.bookMark().getLessonIndex());
        assertEquals(1, (int) frontLineWorker1.reportCard().totalScore());
        assertEquals(2, frontLineWorker1.getPromptsHeard().size());
    }

    @Test
    public void shouldMergeFLWs_CourseAttemptDifferent() {
        FrontLineWorker frontLineWorker1 = new FrontLineWorker("9876543210", "airtel", null);
        frontLineWorker1.incrementCertificateCourseAttempts();
        frontLineWorker1.addBookMark(new BookMark(null, null, null));
        frontLineWorker1.markPromptHeard("welcome");

        FrontLineWorker frontLineWorker2 = new FrontLineWorker("9876543210", "airtel", null);
        frontLineWorker2.incrementCertificateCourseAttempts();
        frontLineWorker2.incrementCertificateCourseAttempts();
        frontLineWorker2.addBookMark(new BookMark("type", 2, 3));
        frontLineWorker2.reportCard().addScore(new Score("1", "4", true, "987654321-2"));
        frontLineWorker2.reportCard().addScore(new Score("1", "5", true, "987654321-2"));
        frontLineWorker1.markPromptHeard("prompt2");

        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);

        assertEquals(2, (int) frontLineWorker1.currentCourseAttempt());
        assertEquals(3, (int) frontLineWorker1.bookMark().getLessonIndex());
        assertEquals(2, (int) frontLineWorker1.reportCard().totalScore());
        assertEquals(2, frontLineWorker1.getPromptsHeard().size());
    }

    @Test
    public void shouldMergeFLWs_CourseAttemptOfFLW2Lesser() {
        FrontLineWorker frontLineWorker1 = new FrontLineWorker("9876543210", "airtel", null);
        frontLineWorker1.incrementCertificateCourseAttempts();
        frontLineWorker1.incrementCertificateCourseAttempts();
        frontLineWorker1.addBookMark(new BookMark(null, null, null));
        frontLineWorker1.markPromptHeard("prompt1");

        FrontLineWorker frontLineWorker2 = new FrontLineWorker("9876543210", "airtel", null);
        frontLineWorker2.incrementCertificateCourseAttempts();
        frontLineWorker2.addBookMark(new BookMark("type", 2, 3));
        frontLineWorker2.reportCard().addScore(new Score("1", "4", true, "987654321-2"));
        frontLineWorker2.reportCard().addScore(new Score("1", "5", true, "987654321-2"));
        frontLineWorker1.markPromptHeard("prompt2");
        frontLineWorker1.markPromptHeard("prompt3");

        seedService.mergeFrontLineWorker(frontLineWorker1, frontLineWorker2);

        assertEquals(2, (int) frontLineWorker1.currentCourseAttempt());
        assertNull(frontLineWorker1.bookMark().getLessonIndex());
        assertEquals(0, (int) frontLineWorker1.reportCard().totalScore());
        assertEquals(3, frontLineWorker1.getPromptsHeard().size());
    }

    @Test
    public void shouldMergeFLWs_OtherFields() throws Exception {
        DateTime now = DateTime.now();
        FrontLineWorker frontLineWorker1 = new FrontLineWorker("9876543210", "airtel", null);
        FrontLineWorker frontLineWorker2 = new FrontLineWorker("9876543210", "airtel", null);

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
    public void shouldCopyGUIDsFromFLWDimension() {
        FrontLineWorker frontlineWorker = new FrontLineWorker("911234567890", "Airtel", "Circle");
        FrontLineWorkerDimension frontLineWorkerDimension = new FrontLineWorkerDimension();
        when(allFrontLineWorkerDimensions.fetchFor(frontlineWorker.msisdn())).thenReturn(frontLineWorkerDimension);

        seedService.copyFlwGuidFromFLWDimension(frontlineWorker);

        verify(allFrontLineWorkerDimensions).fetchFor(frontlineWorker.msisdn());
        assertEquals(frontLineWorkerDimension.getFlwGuid(), frontlineWorker.getFlwGuid());
        verify(allFrontLineWorkers).update(frontlineWorker);
    }
}
