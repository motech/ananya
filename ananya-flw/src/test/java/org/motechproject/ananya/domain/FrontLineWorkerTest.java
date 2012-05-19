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
        FrontLineWorker flw = new FrontLineWorker("msisdn", "name", Designation.ANGANWADI, new Location(), RegistrationStatus.REGISTERED);
        assertNotNull(flw.bookMark());
        assertThat(flw.bookMark(), is(EmptyBookmark.class));
    }

    @Test
    public void shouldIncrementPromptHeard() {
        FrontLineWorker flw = new FrontLineWorker("msisdn", "name", Designation.ANGANWADI, new Location(), RegistrationStatus.REGISTERED);
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
        FrontLineWorker flw = new FrontLineWorker("9986554790", "name", Designation.ANGANWADI, new Location(), RegistrationStatus.REGISTERED);
        assertEquals("919986554790", flw.getMsisdn());
        flw.setMsisdn("9988");
        assertEquals("919988", flw.getMsisdn());

        FrontLineWorker flw2 = new FrontLineWorker("9986554790","airtel");
        assertEquals("919986554790", flw2.getMsisdn());
    }
}
