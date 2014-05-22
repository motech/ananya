package org.motechproject.ananya.request;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FrontLineWorkerRequestTest {
    @Test
    public void shouldCheckMsisdn() {
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest();

        frontLineWorkerRequest.setMsisdn("123456789");
        assertTrue(frontLineWorkerRequest.isInvalidMsisdn());

        frontLineWorkerRequest.setMsisdn("12345678ab");
        assertTrue(frontLineWorkerRequest.isInvalidMsisdn());

        frontLineWorkerRequest.setMsisdn("1234567890");
        assertFalse(frontLineWorkerRequest.isInvalidMsisdn());
    }

    @Test
    public void shouldCheckName() {
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest();

        frontLineWorkerRequest.setName("A. b ' ");
        assertTrue(frontLineWorkerRequest.isInvalidName());

        frontLineWorkerRequest.setName(null);
        assertFalse(frontLineWorkerRequest.isInvalidName());

        frontLineWorkerRequest.setMsisdn("a. b S.  ");
        assertFalse(frontLineWorkerRequest.isInvalidName());
    }

    @Test
    public void shouldCheckAlternateContactNumber() {
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest();

        frontLineWorkerRequest.setAlternateContactNumber("");
        assertFalse(frontLineWorkerRequest.isInvalidAlternateContactNumber());

        frontLineWorkerRequest.setAlternateContactNumber(null);
        assertFalse(frontLineWorkerRequest.isInvalidAlternateContactNumber());

        frontLineWorkerRequest.setAlternateContactNumber("1234567890");
        assertFalse(frontLineWorkerRequest.isInvalidAlternateContactNumber());

        frontLineWorkerRequest.setAlternateContactNumber("1234");
        assertTrue(frontLineWorkerRequest.isInvalidAlternateContactNumber());
    }
}
