package org.motechproject.ananya.contract;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class FailedRecordCSVRequestTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnQuotedCSVHeader() {
        String expected = "\"MSISDN\",\"APPLICATION_NAME\",\"CALLED_NUMBER\",\"CALL_START_TS\",\"DATA_TO_POST\",\"FIELDS_TO_POST\",\"LAST_UPDATED_TS\",\"POST_LAST_RETRY_TS\",\"DATA_POST_RESPONSE\"";

        assertEquals(expected, FailedRecordCSVRequest.csvHeader());
    }

    @Test
    public void shouldConvertToCSVWithQuotedValues() {
        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder().withMsisdn("msisdn").withApplicationName("application\"Name").withCalledNumber("calledNumber").withCallStartTimestamp("callStartTimestamp").withDataToPost("dataToPost").withFieldsToPost("fieldsToPost").withLastUpdatedTimestamp("lastUpdatedTimestamp").withPostLastRetryTimestamp("postLastRetryTimestamp").withDataPostResponse("dataPostResponse").build();

        String expected = "\"msisdn\",\"application\"\"Name\",\"calledNumber\",\"callStartTimestamp\",\"dataToPost\",\"fieldsToPost\",\"lastUpdatedTimestamp\",\"postLastRetryTimestamp\",\"dataPostResponse\"";

        assertEquals(expected, request.toCSVRecord().toString());
    }

    @Test
    public void shouldFailWhileGettingFieldsToPostMapIfAFieldHasNoValue() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Invalid fields to post: a:b;c:");
        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder().withMsisdn("msisdn").withApplicationName("applicationName").withCalledNumber("calledNumber").withCallStartTimestamp("callStartTimestamp").withDataToPost("dataToPost").withFieldsToPost("a:b;c:  ").withLastUpdatedTimestamp("lastUpdatedTimestamp").withPostLastRetryTimestamp("postLastRetryTimestamp").withDataPostResponse("dataPostResponse").build();
        request.getFieldsToPostMap();
    }

    @Test
    public void shouldFailWhileGettingFieldsToPostMapIfInvalidFormat() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Invalid fields to post: a:b;");
        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder().withMsisdn("msisdn").withApplicationName("applicationName").withCalledNumber("calledNumber").withCallStartTimestamp("callStartTimestamp").withDataToPost("dataToPost").withFieldsToPost("a:b;c").withLastUpdatedTimestamp("lastUpdatedTimestamp").withPostLastRetryTimestamp("postLastRetryTimestamp").withDataPostResponse("dataPostResponse").build();
        request.getFieldsToPostMap();
    }
}
