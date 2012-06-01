package org.motechproject.ananya.validators;

import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.response.FLWValidationResponse;

import static junit.framework.Assert.*;

public class FrontLineWorkerValidatorTest {

    private FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidMsisdn() {
        String invalidMSISDN = "9876";

        FLWValidationResponse FLWValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker(invalidMSISDN, "name", Designation.ANM, new Location("district", "block", "panchayat", 1, 1 ,1), RegistrationStatus.REGISTERED), null);

        assertFalse(FLWValidationResponse.isValid());
        assertEquals("Invalid msisdn", FLWValidationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithMsisdnThatIsNotNumeric() {
        String invalidMSISDN = "9876O11223";

        FLWValidationResponse FLWValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker(invalidMSISDN, "name", Designation.ANM, new Location("district", "block", "panchayat", 1, 1 ,1), RegistrationStatus.REGISTERED), null);

        assertFalse(FLWValidationResponse.isValid());
        assertEquals("Invalid msisdn", FLWValidationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidName() {
        String invalidName = "@ron";

        FLWValidationResponse FLWValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", invalidName, Designation.ANM, new Location("district", "block", "panchayat", 1, 1 ,1), RegistrationStatus.REGISTERED), null);

        assertFalse(FLWValidationResponse.isValid());
        assertEquals("Invalid name", FLWValidationResponse.getMessage());
    }

    @Test
    public void shouldInValidateIfLocationIsInvalid() {
        FLWValidationResponse responseFLW = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", "name", Designation.ANM, new Location("district", "block", "panchayat", 1, 1, 1), RegistrationStatus.REGISTERED), null);

        assertFalse(responseFLW.isValid());
        assertEquals("Invalid location", responseFLW.getMessage());
    }

    @Test
    public void shouldValidateFrontLineWorkerWithAllValidAttributes() {
        FLWValidationResponse FLWValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", "name", Designation.ANM, new Location("district", "block", "panchayat", 1, 1 ,1), RegistrationStatus.REGISTERED), new Location());

        assertTrue(FLWValidationResponse.isValid());
        assertNull(FLWValidationResponse.getMessage());
    }

    @Test
    public void shouldValidateFrontLineWorkerBasedOnNameOnlyIfNameIsNotNull() {
        FLWValidationResponse FLWValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", null, Designation.ANM, new Location("district", "block", "panchayat", 1, 1 ,1), RegistrationStatus.REGISTERED), new Location());

        assertTrue(FLWValidationResponse.isValid());
        assertNull(FLWValidationResponse.getMessage());
    }
}
