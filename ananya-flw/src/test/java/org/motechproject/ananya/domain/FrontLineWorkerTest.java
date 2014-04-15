package org.motechproject.ananya.domain;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FrontLineWorkerTest {

    private UUID flwId = UUID.randomUUID();

    @Test
    public void shouldReturnEmptyBookmarkIfThereIsNoBookmark() {
        FrontLineWorker flw = new FrontLineWorker("msisdn", null, "name", Designation.AWW, new Location(), "language", null, flwId);
        assertNotNull(flw.bookMark());
        assertThat(flw.bookMark(), is(EmptyBookmark.class));
    }

    @Test
    public void shouldIncrementPromptHeard() {
        FrontLineWorker flw = new FrontLineWorker("msisdn", null, "name", Designation.AWW, new Location(), "language", null, flwId);
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
        FrontLineWorker flw = new FrontLineWorker("9986554790", null, "name", Designation.AWW, new Location(), "language", null, flwId);
        assertEquals("919986554790", flw.getMsisdn());

        FrontLineWorker flw2 = new FrontLineWorker("9986554790", "airtel", "circle", "language");
        assertEquals("919986554790", flw2.getMsisdn());
    }

    @Test
    public void shouldUpdateFrontLineWorkerDetails() {
        Location existingLocation = mock(Location.class);
        String existingMsisdn = "9986554790";
        when(existingLocation.getExternalId()).thenReturn("existingLocationId");
        FrontLineWorker existingFlw = new FrontLineWorker(existingMsisdn, null, "existingFLWName", Designation.AWW, existingLocation, "language", new DateTime(2011, 3, 16, 8, 18, 0, 0), UUID.randomUUID());
        existingFlw.setVerificationStatus(VerificationStatus.SUCCESS);

        String newLocationId = "newLocationId";
        String newFlwName = "newFlwName";
        Location newLocation = mock(Location.class);
        when(newLocation.getExternalId()).thenReturn(newLocationId);
        DateTime newLastModified = new DateTime(2012, 3, 16, 8, 15, 0, 0);

        String alternateContactNumber = "1";
        boolean updated = existingFlw.update(newFlwName, Designation.ANM, newLocation, newLastModified, flwId, VerificationStatus.INVALID, alternateContactNumber);

        assertTrue(updated);
        assertEquals(Designation.ANM, existingFlw.getDesignation());
        assertEquals(newLocationId, existingFlw.getLocationId());
        assertEquals(newFlwName, existingFlw.getName());
        assertEquals(newLastModified, existingFlw.getLastModified());
        assertEquals(VerificationStatus.INVALID, existingFlw.getVerificationStatus());
        assertEquals(alternateContactNumber, existingFlw.getAlternateContactNumber());
    }

    @Test
    public void shouldUpdateRegistrationStatusIfNotUnregistered() {
        Location existingLocation = mock(Location.class);
        when(existingLocation.isMissingDetails()).thenReturn(true);
        when(existingLocation.getLocationStatusAsEnum()).thenReturn(LocationStatus.NOT_VERIFIED);
        FrontLineWorker existingFlw = new FrontLineWorker("9986554790", null, "existingFLWName", Designation.AWW, null, "language", new DateTime(2011, 3, 16, 8, 18, 0, 0), UUID.randomUUID());

        existingFlw.decideRegistrationStatus(existingLocation);

        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, existingFlw.getStatus());

        Location newLocation = mock(Location.class);
        when(newLocation.isMissingDetails()).thenReturn(false);
        when(newLocation.getLocationStatusAsEnum()).thenReturn(LocationStatus.VALID);

        boolean updated = existingFlw.update("existingFLWName", Designation.AWW, newLocation, new DateTime(2011, 3, 16, 8, 18, 0, 0), UUID.randomUUID(), VerificationStatus.INVALID, null);

        assertTrue(updated);
        assertEquals(RegistrationStatus.REGISTERED, existingFlw.getStatus());
    }

    @Test
    public void shouldNotUpdateRegistrationStatusIfUnregistered() {
        FrontLineWorker existingFlw = new FrontLineWorker("9986554790", null, "existingFLWName", Designation.AWW, null, "language", new DateTime(2011, 3, 16, 8, 18, 0, 0), UUID.randomUUID());

        assertEquals(RegistrationStatus.UNREGISTERED, existingFlw.getStatus());

        Location newLocation = mock(Location.class);
        when(newLocation.isMissingDetails()).thenReturn(false);
        when(newLocation.getLocationStatusAsEnum()).thenReturn(LocationStatus.VALID);

        boolean updated = existingFlw.update("existingFLWName", Designation.AWW, newLocation, new DateTime(2011, 3, 16, 8, 18, 0, 0), UUID.randomUUID(), VerificationStatus.SUCCESS, null);

        assertTrue(updated);
        assertEquals(RegistrationStatus.UNREGISTERED, existingFlw.getStatus());
    }


    @Test
    public void shouldNotUpdateLastModifiedIfNull() {
        DateTime existingLastModifiedTime = new DateTime(2011, 3, 16, 8, 18, 0, 0);
        FrontLineWorker existingFlw = new FrontLineWorker("9900503456", null, "existingFLWName", Designation.AWW, new Location(), "language", existingLastModifiedTime, UUID.randomUUID());

        boolean updated = existingFlw.update("newFlwName", Designation.ANM, new Location(), null, flwId, VerificationStatus.SUCCESS, null);

        assertTrue(updated);
        assertEquals(existingLastModifiedTime, existingFlw.getLastModified());
    }

    @Test
    public void shouldSetLocationIdAsDefaultIfLocationIsNullAndRegistrationStatusAsPartiallyRegistered() {
        Location existingLocation = mock(Location.class);
        when(existingLocation.getExternalId()).thenReturn("existingLocaitonId");
        when(existingLocation.getLocationStatusAsEnum()).thenReturn(LocationStatus.VALID);
        when(existingLocation.isMissingDetails()).thenReturn(false);
        FrontLineWorker existingFlw = new FrontLineWorker("9900503456", null, "existingFLWName", Designation.AWW, existingLocation, "language", DateTime.now(), UUID.randomUUID());
        existingFlw.decideRegistrationStatus(existingLocation);

        assertEquals(RegistrationStatus.REGISTERED, existingFlw.getStatus());

        boolean updated = existingFlw.update("newFlwName", Designation.ANM, null, null, flwId, VerificationStatus.SUCCESS, null);

        assertTrue(updated);
        assertEquals(Location.getDefaultLocation().getExternalId(), existingFlw.getLocationId());
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, existingFlw.getStatus());
    }

    @Test
    public void shouldCreateAnFlwWhenMsisdnIsNotGiven() {
        FrontLineWorker flw = new FrontLineWorker(null, null, "name", Designation.AWW, new Location(), "language", null, flwId);
        assertNull(flw.getMsisdn());
    }

    @Test
    public void shouldAssigntheGivenDateTimeAsLastModifiedTime() {
        DateTime lastModified = DateTime.now();

        FrontLineWorker frontLineWorker = new FrontLineWorker("msisdn", null, "name1", Designation.ASHA, new Location("state1", "distrcit1", "block1", "panchayat1", 1, 1, 2, 3, null, null), "language", lastModified, flwId);

        assertEquals(lastModified, frontLineWorker.getLastModified());
    }

    @Test
    public void shouldDeduceCorrectFLWStatusBasedOnInformation() {
        Location completeLocation = new Location("state", "district", "block", "panchayat", 1, 1, 1, 1, LocationStatus.VALID, null);
        Location incompleteLocation = new Location("state", "district", "block", "", 1, 1, 1, 0, LocationStatus.VALID, null);
        Location defaultLocation = Location.getDefaultLocation();
        Location locationWithStatusNotValid = new Location("state", "district", "block", "panchayat", 1, 1, 1, 1, LocationStatus.INVALID, null);

        FrontLineWorker flwWithCompleteDetails = new FrontLineWorker(
                "1234", null, "name", Designation.ANM, completeLocation, "language", null, flwId);
        flwWithCompleteDetails.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.REGISTERED, flwWithCompleteDetails.getStatus());

        FrontLineWorker flwWithoutName = new FrontLineWorker(
                "1234", null, "", Designation.ANM, completeLocation, "language", null, flwId);
        flwWithoutName.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithoutName.getStatus());

        FrontLineWorker flwWithoutDesignation = new FrontLineWorker(
                "1234", null, "name", null, completeLocation, "language", null, flwId);
        flwWithoutDesignation.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithoutDesignation.getStatus());

        FrontLineWorker flwWithInvalidDesignation = new FrontLineWorker(
                "1234", null, "name", null, completeLocation, "language", null, flwId);
        flwWithInvalidDesignation.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithInvalidDesignation.getStatus());

        FrontLineWorker flwWithDefaultLocation = new FrontLineWorker(
                "1234", null, "name", Designation.ANM, defaultLocation, "language", null, flwId);
        flwWithDefaultLocation.decideRegistrationStatus(defaultLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithDefaultLocation.getStatus());

        FrontLineWorker flwWithIncompleteLocation = new FrontLineWorker(
                "1234", null, "name", Designation.ANM, incompleteLocation, "language", null, flwId);
        flwWithIncompleteLocation.decideRegistrationStatus(incompleteLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithIncompleteLocation.getStatus());

        FrontLineWorker flwWithNoDetails = new FrontLineWorker(
                "1234", null, "", null, defaultLocation, "language", null, flwId);
        flwWithNoDetails.decideRegistrationStatus(defaultLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithNoDetails.getStatus());

        FrontLineWorker flwWithLocationStatusNotAsValid = new FrontLineWorker(
                "1234", null, "", null, locationWithStatusNotValid, "language", null, flwId);
        flwWithLocationStatusNotAsValid.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        flwWithNoDetails.decideRegistrationStatus(locationWithStatusNotValid);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithNoDetails.getStatus());
    }

    @Test
    public void shouldCallBookmarkToCheckIfCourseIsInProgress() {
        BookMark bookMark = mock(BookMark.class);
        when(bookMark.notAtPlayThanks()).thenReturn(true);

        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.setBookMark(bookMark);

        boolean courseInProgress = frontLineWorker.courseInProgress();

        verify(bookMark).notAtPlayThanks();
        assertTrue(courseInProgress);
    }

    @Test
    public void shouldSetADefaultFlwId() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("911234567890", "airtel", "circle", "language");

        assertNotNull(frontLineWorker.getFlwId());
    }

    @Test
    public void shouldUpdateJobAidUsageByAddingCurrentDuration() {
        FrontLineWorker frontLineWorker = new FrontLineWorker();
        frontLineWorker.setCurrentJobAidUsage(10);

        frontLineWorker.updateJobAidUsage(30);

        assertEquals(40, (int) frontLineWorker.getCurrentJobAidUsage());
    }

    @Test
    public void shouldUpdateLocationAndUpdateRegistrationStatusAccordingly() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("1234567890", null, "bane", Designation.ANM, null, "language", DateTime.now(), UUID.randomUUID());
        frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);

        frontLineWorker.updateLocation(new Location("S1", "D1", "B1", "P1", 1, 1, 1, 1, LocationStatus.VALID, null));

        assertEquals(RegistrationStatus.REGISTERED, frontLineWorker.getStatus());
    }

    @Test
    public void shouldUpdateLocationAndNotUpdateRegistrationStatusIfRegStatusIsUnregistered() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("1234567890", null, "bane", Designation.ANM, null, "language", DateTime.now(), UUID.randomUUID());
        frontLineWorker.setRegistrationStatus(RegistrationStatus.UNREGISTERED);

        frontLineWorker.updateLocation(new Location("S1", "D1", "B1", "P1", 1, 1, 1, 1, LocationStatus.VALID, null));

        assertEquals(RegistrationStatus.UNREGISTERED, frontLineWorker.getStatus());
    }

    @Test
    public void shouldSetDummyFlwIdWhenFlwIsCreated() {
        FrontLineWorker frontLineWorker = new FrontLineWorker();

        assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), frontLineWorker.getFlwId());
    }

    @Test
    public void shouldSetLocationToDefaultIfNull() {
        FrontLineWorker frontLineWorker = new FrontLineWorker("9900495678", null, "name", Designation.ANM, null, "language", null, UUID.randomUUID());
        assertEquals(Location.getDefaultLocation().getExternalId(), frontLineWorker.getLocationId());
    }


    @Test
    public void shouldNotUpdateIfNewLastModifiedIsBeforeExistingLastModifiedTime() {
        DateTime now = DateTime.now();
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(null, null, null, null, null, null, now, null);

        boolean update = existingFrontLineWorker.update(null, null, null, now.minusDays(1), UUID.randomUUID(), null, null);

        assertFalse(update);
    }

    @Test
    public void shouldUpdateIfNewLastModifiedIsAfterExistingLastModifiedTime() {
        DateTime now = DateTime.now();
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(null, null, null, null, null, null, now, null);

        boolean update = existingFrontLineWorker.update(null, null, null, now.plusDays(1), UUID.randomUUID(), null, null);

        assertTrue(update);
    }

    @Test
    public void shouldUpdateIfNewLastModifiedIsSameAsExistingLastModifiedTime() {
        DateTime now = DateTime.now();
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(null, null, null, null, null, null, now, null);

        boolean update = existingFrontLineWorker.update(null, null, null, now, UUID.randomUUID(), null, null);

        assertTrue(update);
    }

    @Test
    public void shouldUpdateIfNewLastModifiedIsNull() {
        DateTime now = DateTime.now();
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(null, null, null, null, null, null, now, null);

        boolean update = existingFrontLineWorker.update(null, null, null, null, UUID.randomUUID(), null, null);

        assertTrue(update);
    }

    @Test
    public void shouldUpdateIfExistingLastModifiedTimeIsNull() {
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(null, null, null, null, null, null, null, null);

        boolean update = existingFrontLineWorker.update(null, null, null, DateTime.now(), UUID.randomUUID(), null, null);

        assertTrue(update);
    }

    @Test
    public void shouldUpdateIfNewAndExistingLastModifiedTimeAreNull() {
        FrontLineWorker existingFrontLineWorker = new FrontLineWorker(null, null, null, null, null, null, null, null);

        boolean update = existingFrontLineWorker.update(null, null, null, null, UUID.randomUUID(), null, null);

        assertTrue(update);
    }

    @Test
    public void shouldMergeAlternateContactNumber() {
        FrontLineWorker source = new FrontLineWorker();
        String alternateContactNumber = "123";
        source.setAlternateContactNumber(alternateContactNumber);
        FrontLineWorker destination = new FrontLineWorker();
        destination.merge(source);
        assertEquals(alternateContactNumber, destination.getAlternateContactNumber());

    }
}
