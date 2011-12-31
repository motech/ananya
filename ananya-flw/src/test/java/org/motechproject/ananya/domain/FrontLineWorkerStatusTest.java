package org.motechproject.ananya.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FrontLineWorkerStatusTest {

    @Test
    public void shouldReturnTrueIfRegistered(){
        assertTrue(FrontLineWorkerStatus.REGISTERED.isRegistered());
        assertFalse(FrontLineWorkerStatus.PENDING_REGISTRATION.isRegistered());
    }
}
