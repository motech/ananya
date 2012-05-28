package org.motechproject.ananya.validators;

import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.response.ValidationResponse;

import static junit.framework.Assert.*;

public class FrontLineWorkerValidatorTest {

    private FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidMsisdn() {
        String invalidMSISDN = "9876";

        ValidationResponse validationResponse = frontLineWorkerValidator.validate(new FrontLineWorker(invalidMSISDN, "name", Designation.ANGANWADI, new Location("district", "block", "panchayat", 1, 1 ,1), RegistrationStatus.REGISTERED), null);

        assertFalse(validationResponse.isValid());
        assertEquals("Invalid msisdn", validationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithMsisdnThatIsNotNumeric() {
        String invalidMSISDN = "9876O11223";

        ValidationResponse validationResponse = frontLineWorkerValidator.validate(new FrontLineWorker(invalidMSISDN, "name", Designation.ANGANWADI, new Location("district", "block", "panchayat", 1, 1 ,1), RegistrationStatus.REGISTERED), null);

        assertFalse(validationResponse.isValid());
        assertEquals("Invalid msisdn", validationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidName() {
        String invalidName = "@ron";

        ValidationResponse validationResponse = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", invalidName, Designation.ANGANWADI, new Location("district", "block", "panchayat", 1, 1 ,1), RegistrationStatus.REGISTERED), null);

        assertFalse(validationResponse.isValid());
        assertEquals("Invalid name", validationResponse.getMessage());
    }

    @Test
    public void shouldInValidateIfLocationIsInvalid() {
        ValidationResponse response = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", "name", Designation.ANGANWADI, new Location("district", "block", "panchayat", 1, 1, 1), RegistrationStatus.REGISTERED), null);

        assertFalse(response.isValid());
        assertEquals("Invalid location", response.getMessage());
    }

    @Test
    public void shouldValidateFrontLineWorkerWithAllValidAttributes() {
        ValidationResponse validationResponse = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", "name", Designation.ANGANWADI, new Location("district", "block", "panchayat", 1, 1 ,1), RegistrationStatus.REGISTERED), new Location());

        assertTrue(validationResponse.isValid());
        assertNull(validationResponse.getMessage());
    }
}
