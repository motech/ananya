package org.motechproject.ananya.importer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.FailedRecordCSVRequestBuilder;
import org.motechproject.ananya.service.FailedRecordsService;
import org.motechproject.ananya.validators.FailedRecordsValidator;
import org.motechproject.importer.domain.Error;
import org.motechproject.importer.domain.ValidationResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FailedRecordsImporterTest {
    @Mock
    private FailedRecordsService failedRecordsService;

    @Mock
    private FailedRecordsValidator failedRecordsValidator;

    @Captor
    private ArgumentCaptor<ArrayList<FailedRecordCSVRequest>> failedrecordRequestsCaptor;

    private FailedRecordsImporter failedRecordsImporter;

    @Before
    public void setUp() {
        initMocks(this);
        failedRecordsImporter = new FailedRecordsImporter(failedRecordsService, failedRecordsValidator, "1");
    }

    @Test
    public void shouldProcess() {
        ArrayList<FailedRecordCSVRequest> failedRecordCSVRequests = new ArrayList<>();
        String msisdn = "1234567890";
        FailedRecordCSVRequest failedRecordCSVRequest = new FailedRecordCSVRequestBuilder().build();
        failedRecordCSVRequest.setMsisdn(msisdn);
        failedRecordCSVRequests.add(failedRecordCSVRequest);

        failedRecordsImporter.postData(failedRecordCSVRequests);

        verify(failedRecordsService).publishFailedRecordsForProcessing(failedrecordRequestsCaptor.capture());
        ArrayList<FailedRecordCSVRequest> actualFailedRecordRequests = failedrecordRequestsCaptor.getValue();
        assertEquals(1, actualFailedRecordRequests.size());
        assertEquals(msisdn, actualFailedRecordRequests.get(0).getMsisdn());
    }

    @Test
    public void shouldValidateUsingFailedRecordsValidator() {
        ValidationResponse validationResponse = new ValidationResponse(false);
        validationResponse.addError(new Error("some error"));
        List<FailedRecordCSVRequest> failedRecordCSVRequests = Arrays.asList(new FailedRecordCSVRequestBuilder().build(), new FailedRecordCSVRequestBuilder().build());
        when(failedRecordsValidator.validate(failedRecordCSVRequests)).thenReturn(validationResponse);

        ValidationResponse actualValidationResponse = failedRecordsImporter.validate(failedRecordCSVRequests);

        assertEquals(validationResponse, actualValidationResponse);
    }

    @Test
    public void shouldNotValidateIfTheFlagIsOffWhenUsingFailedRecordsValidator() {
        failedRecordsImporter = new FailedRecordsImporter(failedRecordsService, failedRecordsValidator, "0");
        List<FailedRecordCSVRequest> failedRecordCSVRequests = Arrays.asList(new FailedRecordCSVRequestBuilder().build(), new FailedRecordCSVRequestBuilder().build());

        ValidationResponse actualValidationResponse = failedRecordsImporter.validate(failedRecordCSVRequests);

        verify(failedRecordsValidator, never()).validate(any(failedRecordCSVRequests.getClass()));
        assertTrue(actualValidationResponse.isValid());
    }
}
