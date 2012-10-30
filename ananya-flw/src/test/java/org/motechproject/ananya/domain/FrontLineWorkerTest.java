package org.motechproject.ananya.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FrontLineWorkerTest {

    private UUID flwId = UUID.randomUUID();

    @Test
    public void shouldReturnEmptyBookmarkIfThereIsNoBookmark() {
        FrontLineWorker flw = new FrontLineWorker("msisdn", "name", Designation.AWW, new Location(), null, flwId);
        assertNotNull(flw.bookMark());
        assertThat(flw.bookMark(), is(EmptyBookmark.class));
    }

    @Test
    public void shouldIncrementPromptHeard() {
        FrontLineWorker flw = new FrontLineWorker("msisdn", "name", Designation.AWW, new Location(), null, flwId);
        String promptKey = "prompt1";

        Map<String, Integer> promptsHeard = flw.getPromptsHeard();

        assertNotNull(promptsHeard);
        assertFalse(promptsHeard.containsKey(promptKey));

        flw.markPromptHeard(promptKey);
        assertEquals((int) promptsHeard.get(promptKey), 1);

        flw.markPromptHeard(promptKey);
        assertEquals((int) promptsHeard.get(promptKey), 2);
    }

    @Test
    public void shouldAppend91ToCallerId() {
        FrontLineWorker flw = new FrontLineWorker("9986554790", "name", Designation.AWW, new Location(), null, flwId);
        assertEquals("919986554790", flw.getMsisdn());

        FrontLineWorker flw2 = new FrontLineWorker("9986554790", "airtel", "circle");
        assertEquals("919986554790", flw2.getMsisdn());
    }

    @Test
    public void shouldUpdateLastModifiedTimeToGivenTime() {
        FrontLineWorker flw = new FrontLineWorker("9986554790", "name", Designation.AWW, new Location(), null, flwId);
        DateTime lastModified = new DateTime(2012, 3, 16, 8, 15, 0, 0);

        flw.update("newName", Designation.AWW, new Location("D1", "B1", "P1", 1, 1, 1), lastModified, flwId);

        assertEquals(lastModified, flw.getLastModified());
    }

    @Test
    public void shouldNotUpdateRegistrationStatusIfRegistrationStatusIsPartiallyRegistered() {
        FrontLineWorker flw = new FrontLineWorker("9986554790", "name", Designation.AWW, new Location(), null, flwId);
        DateTime lastModified = new DateTime(2012, 3, 16, 8, 15, 0, 0);

        flw.update("newName", Designation.AWW, new Location(), lastModified, flwId);

        assertEquals(RegistrationStatus.UNREGISTERED, flw.getStatus());
    }

    @Test
    public void shouldCreateAnFlwWhenMsisdnIsNotGiven() {
        FrontLineWorker flw = new FrontLineWorker(null, "name", Designation.AWW, new Location(), null, flwId);
        assertNull(flw.getMsisdn());
    }

    @Test
    public void shouldAssignTheGivenDateTimeAsLastModifiedTime() {
        DateTime lastModified = DateTime.now();
        FrontLineWorker frontLineWorker = new FrontLineWorker("msisdn", "name1", Designation.ASHA, new Location("distrcit1", "block1", "panchayat1", 1, 2, 3), lastModified, flwId);
        assertEquals(lastModified, frontLineWorker.getLastModified());
    }

    @Test
    public void shouldDeduceCorrectFLWStatusBasedOnInformation() {
        Location completeLocation = new Location("district", "block", "panchayat", 1, 1, 1);
        Location incompleteLocation = new Location("district", "block", "", 1, 1, 0);
        Location defaultLocation = Location.getDefaultLocation();

        FrontLineWorker flwWithCompleteDetails = new FrontLineWorker(
                "1234", "name", Designation.ANM, completeLocation, null, flwId);
        flwWithCompleteDetails.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.REGISTERED, flwWithCompleteDetails.getStatus());

        FrontLineWorker flwWithoutName = new FrontLineWorker(
                "1234", "", Designation.ANM, completeLocation, null, flwId);
        flwWithoutName.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithoutName.getStatus());

        FrontLineWorker flwWithoutDesignation = new FrontLineWorker(
                "1234", "name", null, completeLocation, null, flwId);
        flwWithoutDesignation.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithoutDesignation.getStatus());

        FrontLineWorker flwWithInvalidDesignation = new FrontLineWorker(
                "1234", "name", null, completeLocation, null, flwId);
        flwWithInvalidDesignation.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithInvalidDesignation.getStatus());

        FrontLineWorker flwWithDefaultLocation = new FrontLineWorker(
                "1234", "name", Designation.ANM, defaultLocation, null, flwId);
        flwWithDefaultLocation.decideRegistrationStatus(defaultLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithDefaultLocation.getStatus());

        FrontLineWorker flwWithIncompleteLocation = new FrontLineWorker(
                "1234", "name", Designation.ANM, incompleteLocation, null, flwId);
        flwWithIncompleteLocation.decideRegistrationStatus(incompleteLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithIncompleteLocation.getStatus());

        FrontLineWorker flwWithNoDetails = new FrontLineWorker(
                "1234", "", null, defaultLocation, null, flwId);
        flwWithNoDetails.decideRegistrationStatus(defaultLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithNoDetails.getStatus());

    }

    @Test
    public void shouldCallBookmarkToCheckIfCourseIsInProgress() {
        BookMark bookMark = mock(BookMark.class);
        when(bookMark.notAtPlayCourseResult()).thenReturn(true);

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.addBookMark(bookMark);

        boolean courseInProgress = frontLineWorker.courseInProgress();

        verify(bookMark).notAtPlayCourseResult();
        assertTrue(courseInProgress);
    }

    @Test
    public void shouldSetADefaultFlwId() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("911234567890", "airtel", "circle");

        assertNotNull(frontLineWorker.getFlwId());
    }

    @Test
    public void shouldUpdateJobAidUsageByAddingCurrentDuration(){
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.setCurrentJobAidUsage(10);

        frontLineWorker.updateJobAidUsage(30);

        assertEquals(40, (int) frontLineWorker.getCurrentJobAidUsage());
    }
}
