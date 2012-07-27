package org.motechproject.ananya.domain;

import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class FrontLineWorkerTest {
    @Test
    public void shouldReturnEmptyBookmarkIfThereIsNoBookmark() {
        FrontLineWorker flw = new FrontLineWorker("msisdn", "name", Designation.AWW, new Location(), RegistrationStatus.REGISTERED);
        assertNotNull(flw.bookMark());
        assertThat(flw.bookMark(), is(EmptyBookmark.class));
    }

    @Test
    public void shouldIncrementPromptHeard() {
        FrontLineWorker flw = new FrontLineWorker("msisdn", "name", Designation.AWW, new Location(), RegistrationStatus.REGISTERED);
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
        FrontLineWorker flw = new FrontLineWorker("9986554790", "name", Designation.AWW, new Location(), RegistrationStatus.REGISTERED);
        assertEquals("919986554790", flw.getMsisdn());

        FrontLineWorker flw2 = new FrontLineWorker("9986554790", "airtel", "circle");
        assertEquals("919986554790", flw2.getMsisdn());
    }

    @Test
    public void shouldDeduceCorrectFLWStatusBasedOnInformation() {
        Location completeLocation = new Location("district", "block", "panchayat", 1, 1, 1);
        Location incompleteLocation = new Location("district", "block", "", 1, 1, 0);
        Location defaultLocation = Location.getDefaultLocation();

        FrontLineWorker flwWithCompleteDetails = new FrontLineWorker(
                "1234", "name", Designation.ANM, completeLocation, RegistrationStatus.REGISTERED);
        flwWithCompleteDetails.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.REGISTERED, flwWithCompleteDetails.status());

        FrontLineWorker flwWithoutName = new FrontLineWorker(
                "1234", "", Designation.ANM, completeLocation, RegistrationStatus.REGISTERED);
        flwWithoutName.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithoutName.status());

        FrontLineWorker flwWithoutDesignation = new FrontLineWorker(
                "1234", "name", null, completeLocation, RegistrationStatus.REGISTERED);
        flwWithoutDesignation.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithoutDesignation.status());

        FrontLineWorker flwWithInvalidDesignation = new FrontLineWorker(
                "1234", "name", null, completeLocation, RegistrationStatus.REGISTERED);
        flwWithInvalidDesignation.decideRegistrationStatus(completeLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithInvalidDesignation.status());

        FrontLineWorker flwWithDefaultLocation = new FrontLineWorker(
                "1234", "name", Designation.ANM, defaultLocation, RegistrationStatus.REGISTERED);
        flwWithDefaultLocation.decideRegistrationStatus(defaultLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithDefaultLocation.status());

        FrontLineWorker flwWithIncompleteLocation = new FrontLineWorker(
                "1234", "name", Designation.ANM, incompleteLocation, RegistrationStatus.REGISTERED);
        flwWithIncompleteLocation.decideRegistrationStatus(incompleteLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithIncompleteLocation.status());

        FrontLineWorker flwWithNoDetails = new FrontLineWorker(
                "1234", "", null, defaultLocation, RegistrationStatus.REGISTERED);
        flwWithNoDetails.decideRegistrationStatus(defaultLocation);
        assertEquals(RegistrationStatus.PARTIALLY_REGISTERED, flwWithNoDetails.status());

    }
}
