package org.motechproject.ananya.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.powermock.api.mockito.PowerMockito.spy;

@PrepareForTest(DateUtil.class)
@RunWith(PowerMockRunner.class)
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

        FrontLineWorker flw2 = new FrontLineWorker("9986554790", "airtel");
        assertEquals("919986554790", flw2.getMsisdn());
    }

    @Test
    public void shouldUpdateRegistrationStatusOnlyIfTheCurrentStatusIsLessThanTheNewStatus() {
        FrontLineWorker flw = new FrontLineWorker("9986554790", "name", Designation.AWW, new Location(), RegistrationStatus.PARTIALLY_REGISTERED);
        flw.update("newName", Designation.AWW, new Location(), RegistrationStatus.REGISTERED, "bihar", "airtel", null);

        assertEquals(RegistrationStatus.REGISTERED, flw.getStatus());
        assertEquals("newName", flw.getName());

        flw.update("newName", Designation.AWW, new Location(), RegistrationStatus.PARTIALLY_REGISTERED, null, null, null);

        assertEquals(RegistrationStatus.REGISTERED, flw.getStatus());
        assertEquals("newName", flw.getName());
    }

    @Test
    public void shouldUpdateLastModifiedTimeToGivenTime() {
        FrontLineWorker flw = new FrontLineWorker("9986554790", "name", Designation.AWW, new Location(), RegistrationStatus.PARTIALLY_REGISTERED);
        DateTime lastModified = new DateTime(2012, 3, 16, 8, 15, 0, 0);
        flw.update("newName", Designation.AWW, new Location(), RegistrationStatus.REGISTERED, "bihar", "airtel", lastModified);

        assertEquals(lastModified, flw.getLastModified());
    }

    @Test
    public void shouldCreateAnFlwWhenMsisdnIsNotGiven() {
        FrontLineWorker flw = new FrontLineWorker(null, "name", Designation.AWW, new Location(), RegistrationStatus.PARTIALLY_REGISTERED);
        assertNull(flw.getMsisdn());
    }

    @Test
    public void shouldAssigntheGivenDateTimeAsLastModifiedTime() {
        DateTime lastModified = DateTime.now();
        FrontLineWorker frontLineWorker = new FrontLineWorker("msisdn", "name1", Designation.ASHA, "operator1", "circle1", new Location("distrcit1", "block1", "panchayat1", 1, 2, 3), RegistrationStatus.PARTIALLY_REGISTERED, lastModified);
        assertEquals(lastModified, frontLineWorker.getLastModified());
    }
}
