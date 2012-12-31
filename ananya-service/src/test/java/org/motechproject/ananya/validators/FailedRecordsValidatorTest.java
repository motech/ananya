package org.motechproject.ananya.validators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.FailedRecordCSVRequestBuilder;
import org.motechproject.importer.domain.ValidationResponse;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FailedRecordsValidatorTest {
    @Mock
    private FailedJobAidRecordValidator failedJARecordValidator;
    @Mock
    private FailedCertificateCourseRecordValidator failedCCRecordValidator;

    @Test
    public void shouldValidateFailedRecords() {
        FailedRecordsValidator failedRecordsValidator = new FailedRecordsValidator(failedCCRecordValidator, failedJARecordValidator);
        ArrayList<FailedRecordCSVRequest> failedRecordCSVRequests = new ArrayList<>();
        FailedRecordCSVRequest ccInvalidRecord = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":1,\"data\":{\"time\":1350379378663},\"type\":\"callDuration\"}]")
                .withCalledNumber("1234567")
                .build();
        FailedRecordCSVRequest jaInvalidRecord = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[{\"token\":1,\"data\":{\"time\":1350379378663},\"type\":\"callDuration\"}]")
                .withCalledNumber("1234567")
                .build();
        failedRecordCSVRequests.add(ccInvalidRecord);
        failedRecordCSVRequests.add(jaInvalidRecord);
        String ccErrorMsg = "some cc error";
        String jaErrorMsg = "some ja error";
        doThrow(new FailedRecordValidationException(ccErrorMsg)).when(failedCCRecordValidator).validate(ccInvalidRecord);
        doThrow(new FailedRecordValidationException(jaErrorMsg)).when(failedJARecordValidator).validate(jaInvalidRecord);

        ValidationResponse validationResponse = failedRecordsValidator.validate(failedRecordCSVRequests);

        verify(failedJARecordValidator).validate(jaInvalidRecord);
        verify(failedCCRecordValidator).validate(ccInvalidRecord);
        assertEquals(3, validationResponse.getErrors().size());
        assertEquals(FailedRecordCSVRequest.csvHeader(), validationResponse.getErrors().get(0).getMessage());
        assertEquals(ccInvalidRecord.toCSVRecord() + ",\"" + ccErrorMsg + "\"", validationResponse.getErrors().get(1).getMessage());
        assertEquals(jaInvalidRecord.toCSVRecord() + ",\"" + jaErrorMsg + "\"", validationResponse.getErrors().get(2).getMessage());
    }
}
