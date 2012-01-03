package org.motechproject.ananya.domain;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FrontLineWorkerStatusTest {

    @Test
    public void shouldReturnTrueIfRegistered(){
        assertTrue(FrontLineWorkerStatus.REGISTERED.isRegistered());
        assertTrue(FrontLineWorkerStatus.PENDING_REGISTRATION.isRegistered());
        assertFalse(FrontLineWorkerStatus.UNREGISTERED.isRegistered());
    }
}
