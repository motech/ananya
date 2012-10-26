package org.motechproject.ananya.validators;

import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.FLWValidationResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static junit.framework.Assert.*;

public class FrontLineWorkerValidatorTest {

    private String flwId = UUID.randomUUID().toString();

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidMsisdn() {
        String invalidMSISDN = "9876";

        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest(invalidMSISDN, "name", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"), null, flwId), new Location());

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithMsisdnThatIsNotNumeric() {
        String invalidMSISDN = "9876O11223";

        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest(invalidMSISDN, "name", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"), null, flwId), new Location());

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidName() {
        String invalidName = "@ron";

        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", invalidName, Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"), null, flwId), new Location());

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid name]", flwValidationResponse.getMessage());

        flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", "Mr. Valid", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"), null, flwId), new Location());

        assertTrue(flwValidationResponse.isValid());
    }

    @Test
    public void shouldInValidateIfLocationIsInvalid() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", "name", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"), null, flwId), null);

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid location]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInValidateAndHaveMultipleErrorMessagesConcatenated() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("invalidMSISDN", "name", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"), null, flwId), null);

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn][Invalid location]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldBulkValidateFLWsAndThrowExceptionsIfThereAreDuplicates() {
        String msisdn = "2345678901";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, "name", Designation.ANM.name(), new LocationRequest(), null, flwId);
        Map<String,Integer> msisdnOccurrenceMap = new HashMap<String, Integer>();
        msisdnOccurrenceMap.put(msisdn, 2);
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validateWithBulkValidation(frontLineWorkerRequest, new Location(), msisdnOccurrenceMap);

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Found duplicate FLW with the same MSISDN]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldBulkValidateFLWsAndPassValidationIfThereAreNoErrors() {
        String msisdn = "2345678901";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, "name", Designation.ANM.name(), new LocationRequest(), null, flwId);
        Map<String,Integer> msisdnOccurrenceMap = new HashMap<String, Integer>();
        msisdnOccurrenceMap.put(msisdn, 1);

        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validateWithBulkValidation(frontLineWorkerRequest, new Location(), msisdnOccurrenceMap);

        assertTrue(flwValidationResponse.isValid());
    }

    @Test
    public void shouldValidateFrontLineWorkerWithAllValidAttributes() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", "name", Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"), null, flwId), new Location());

        assertTrue(flwValidationResponse.isValid());
        assertEquals("", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldValidateFrontLineWorkerBasedOnNameOnlyIfNameIsNotNull() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", null, Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"), null, flwId), new Location());

        assertTrue(flwValidationResponse.isValid());
        assertEquals("", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInValidateFrontLineWorkerWhenFlwIdIsBlank() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", null, Designation.ANM.name(), new LocationRequest("district", "block", "panchayat"), null, null), new Location());

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid flwId]", flwValidationResponse.getMessage());
    }
}
