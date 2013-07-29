package org.motechproject.ananya.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.request.LocationRequest;
import org.motechproject.ananya.response.FLWValidationResponse;

public class FrontLineWorkerValidatorTest {

    private String flwId = UUID.randomUUID().toString();

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidMsisdn() {
        String invalidMSISDN = "9876";

        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest(invalidMSISDN, null, "name", Designation.ANM.name(), new LocationRequest("state", "district", "block", "panchayat"), null, flwId, null, null));

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithMsisdnThatIsNotNumeric() {
        String invalidMSISDN = "9876O11223";

        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest(invalidMSISDN, null, "name", Designation.ANM.name(), new LocationRequest("state", "district", "block", "panchayat"), null, flwId, null, null));

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidVerifcationStatus() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("1234567890", null, "name", Designation.ANM.name(), new LocationRequest("state", "district", "block", "panchayat"), null, flwId, "InvalidVerStat", null));

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid verification status]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInvalidateFrontLineWorkerWithInvalidName() {
        String invalidName = "@ron";

        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", null, invalidName, Designation.ANM.name(), new LocationRequest("state", "district", "block", "panchayat"), null, flwId, null, null));

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid name]", flwValidationResponse.getMessage());

        flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", null, "Mr. Valid", Designation.ANM.name(), new LocationRequest("state", "district", "block", "panchayat"), null, flwId, null, null));

        assertTrue(flwValidationResponse.isValid());
    }

    @Test
    public void shouldInValidateIfLocationIsInvalid() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", null, "name", Designation.ANM.name(), new LocationRequest(null, null, "block", "panchayat"), null, flwId, null, null));

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid location]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInValidateAndHaveMultipleErrorMessagesConcatenated() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("invalidMSISDN", null, "name", Designation.ANM.name(), null, null, flwId, null, null));

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid msisdn]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldValidateAlternateContactNumber() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("1234567890", "invalidMSISDN", "name", Designation.ANM.name(), null, null, flwId, null, null));

        assertTrue(flwValidationResponse.isInValid());
        assertEquals("[Invalid alternate contact number]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldNotInValidateIfJustLocationIsBlank() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9876543210", null, "name", Designation.ANM.name(), null, null, flwId, null, null));

        assertTrue(flwValidationResponse.isValid());
        assertEquals("", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldNotInValidateIfJustLanguageIsBlank() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9876543210", null, "name", Designation.ANM.name(), new LocationRequest("S1", "D1", "B1", "P1"), null, flwId, null, null));

        assertTrue(flwValidationResponse.isValid());
        assertEquals("", flwValidationResponse.getMessage());
    }
    
    @Test
    public void shouldBulkValidateFLWsAndThrowExceptionsIfThereAreDuplicates() {
        String msisdn = "2345678901";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, null, "name", Designation.ANM.name(), new LocationRequest("S1", "D1", "B1", "P1"), null, flwId, null, null);
        Map<String, Integer> msisdnOccurrenceMap = new HashMap<String, Integer>();
        msisdnOccurrenceMap.put(msisdn, 2);
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validateWithBulkValidation(frontLineWorkerRequest, new Location(), msisdnOccurrenceMap);

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Found duplicate FLW with the same MSISDN]", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldBulkValidateFLWsAndPassValidationIfThereAreNoErrors() {
        String msisdn = "2345678901";
        FrontLineWorkerRequest frontLineWorkerRequest = new FrontLineWorkerRequest(msisdn, null, "name", Designation.ANM.name(), new LocationRequest("S1", "D1", "B1", "P1"), null, flwId, null, null);
        Map<String, Integer> msisdnOccurrenceMap = new HashMap<String, Integer>();
        msisdnOccurrenceMap.put(msisdn, 1);

        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validateWithBulkValidation(frontLineWorkerRequest, new Location(), msisdnOccurrenceMap);

        assertTrue(flwValidationResponse.isValid());
    }

    @Test
    public void shouldValidateFrontLineWorkerWithAllValidAttributes() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", null, "name", Designation.ANM.name(), new LocationRequest("state", "district", "block", "panchayat"), null, flwId, null, null));

        assertTrue(flwValidationResponse.isValid());
        assertEquals("", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldValidateFrontLineWorkerBasedOnNameOnlyIfNameIsNotNull() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", null, null, Designation.ANM.name(), new LocationRequest("state", "district", "block", "panchayat"), null, flwId, null, null));

        assertTrue(flwValidationResponse.isValid());
        assertEquals("", flwValidationResponse.getMessage());
    }

    @Test
    public void shouldInValidateFrontLineWorkerWhenFlwIdIsBlank() {
        FLWValidationResponse flwValidationResponse = FrontLineWorkerValidator.validate(new FrontLineWorkerRequest("9996664422", null, null, Designation.ANM.name(), new LocationRequest("state", "district", "block", "panchayat"), null, null, null, null));

        assertFalse(flwValidationResponse.isValid());
        assertEquals("[Invalid flwId]", flwValidationResponse.getMessage());
    }
}
