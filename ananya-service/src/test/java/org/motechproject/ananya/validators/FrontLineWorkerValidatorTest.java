package org.motechproject.ananya.validators;

import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.FLWValidationResponse;

import java.util.ArrayList;

import static junit.framework.Assert.*;

public class FrontLineWorkerValidatorTest {

    private FrontLineWorkerValidator frontLineWorkerValidator = new FrontLineWorkerValidator();

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidMsisdn() {
        String invalidMSISDN = "9876";

        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker(invalidMSISDN, "name", Designation.ANM, new Location("district", "block", "panchayat", 1, 1, 1), RegistrationStatus.REGISTERED), new Location());

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithMsisdnThatIsNotNumeric() {
        String invalidMSISDN = "9876O11223";

        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker(invalidMSISDN, "name", Designation.ANM, new Location("district", "block", "panchayat", 1, 1, 1), RegistrationStatus.REGISTERED), new Location());

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidName() {
        String invalidName = "@ron";

        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", invalidName, Designation.ANM, new Location("district", "block", "panchayat", 1, 1, 1), RegistrationStatus.REGISTERED), new Location());

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid name]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInValidateIfLocationIsInvalid() {
        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", "name", Designation.ANM, new Location("district", "block", "panchayat", 1, 1, 1), RegistrationStatus.REGISTERED), null);

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid location]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInValidateAndHaveMultipleErrorMessagesConcatenated() {
        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker("invalidMSISDN", "name", Designation.ANM, new Location("district", "block", "panchayat", 1, 1, 1), RegistrationStatus.REGISTERED), null);

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn][Invalid location]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldBulkValidateFLWsAndThrowExceptionsIfThereAreDuplicates() {
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest("2345678901", "name", Designation.ANM.name(), "airtel", "bihar", new LocationRequest());
        frontLineWorkerRequests.add(frontLineWorkerRequest);
        frontLineWorkerRequests.add(frontLineWorkerRequest);
        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validateWithBulkValidation(frontLineWorkerRequest, new Location(), frontLineWorkerRequests);

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Found duplicate FLW with the same MSISDN]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldBulkValidateFLWsAndPassValidationIfThereAreNoErrors() {
        ArrayList<FrontLineWorkerRequest> frontLineWorkerRequests = new ArrayList<FrontLineWorkerRequest>();
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest("2345678901", "name", Designation.ANM.name(), "airtel", "bihar", new LocationRequest());
        frontLineWorkerRequests.add(frontLineWorkerRequest);
        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validateWithBulkValidation(frontLineWorkerRequest, new Location(), frontLineWorkerRequests);

        assertTrue(flwValidationResponse.isValid());
    }

    @Test
    public void shouldValidateFrontLineWorkerWithAllValidAttributes() {
        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", "name", Designation.ANM, new Location("district", "block", "panchayat", 1, 1, 1), RegistrationStatus.REGISTERED), new Location());

        assertTrue(flwValidationResponse.isValid());
        assertEquals("", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldValidateFrontLineWorkerBasedOnNameOnlyIfNameIsNotNull() {
        FLWValidationResponse flwValidationResponse = frontLineWorkerValidator.validate(new FrontLineWorker("9996664422", null, Designation.ANM, new Location("district", "block", "panchayat", 1, 1, 1), RegistrationStatus.REGISTERED), new Location());

        assertTrue(flwValidationResponse.isValid());
        assertEquals("", flwValidationResponse.getMessage());
    }
}
