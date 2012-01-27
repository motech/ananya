package org.motechproject.ananya.domain;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FrontLineWorkerStatusTest {

    @Test
    public void shouldReturnTrueIfRegistered(){
        assertTrue(RegistrationStatus.REGISTERED.isRegistered());
        assertTrue(RegistrationStatus.PENDING_REGISTRATION.isRegistered());
        assertFalse(RegistrationStatus.UNREGISTERED.isRegistered());
    }
}
