package org.motechproject.ananya.validators;

import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.utils.CSVRecord;
import org.motechproject.importer.domain.Error;
import org.motechproject.importer.domain.ValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class FailedRecordsValidator {

    private final FailedCertificateCourseRecordValidator failedCertificateCourseRecordValidator;
    private final FailedJobAidRecordValidator failedJobAidRecordValidator;

    private final Logger logger = LoggerFactory.getLogger(FailedRecordsValidator.class);

    @Autowired
    public FailedRecordsValidator(FailedCertificateCourseRecordValidator failedCertificateCourseRecordValidator, FailedJobAidRecordValidator failedJobAidRecordValidator) {
        this.failedCertificateCourseRecordValidator = failedCertificateCourseRecordValidator;
        this.failedJobAidRecordValidator = failedJobAidRecordValidator;
    }

    public ValidationResponse validate(List<FailedRecordCSVRequest> failedRecordCSVRequests) {
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(FailedRecordCSVRequest.csvHeader()));

        HashSet<String> validatedCallIds = new HashSet<>();
        boolean isValid = true;

        for (int index = 0; index < failedRecordCSVRequests.size(); index++) {
            FailedRecordCSVRequest failedRecordRequest = failedRecordCSVRequests.get(index);
            CSVRecord csvRecord = failedRecordRequest.toCSVRecord();

            String errorMessage = validateRecord(validatedCallIds, failedRecordRequest, index);

            isValid = isValid && errorMessage == null;
            logger.info(String.format("Validated record number %s with result: Valid: %s", index, errorMessage == null));
            errors.add(new Error(csvRecord.append(errorMessage).toString()));
        }

        return constructValidationResponse(isValid, errors);
    }

    private String validateRecord(HashSet<String> validatedCallIds, FailedRecordCSVRequest failedRecordRequest, int index) {
        CSVRecord csvRecord = failedRecordRequest.toCSVRecord();
        String errorMessage = null;
        try {
            validateCSVRecord(failedRecordRequest, validatedCallIds);
        } catch (FailedRecordValidationException ex) {
            logger.error(String.format("Validation error occurred while processing record number: %s, validation error: %s, record: %s", index, ex.getMessage(), csvRecord.toString()));
            errorMessage = ex.getMessage();
        } catch (Exception ex) {
            logger.error(String.format("Exception occurred while processing record number: %s, record: %s", index, csvRecord.toString()), ex);
            errorMessage = ex.getMessage();
        }
        return errorMessage;
    }

    private void validateCSVRecord(FailedRecordCSVRequest failedRecordRequest, HashSet<String> validatedCallIds) {
        String applicationName = failedRecordRequest.getApplicationName();
        if (applicationName.equalsIgnoreCase("CERTIFICATECOURSE")) {
            failedCertificateCourseRecordValidator.validate(failedRecordRequest);
            validateDuplicateCallId(failedRecordRequest, validatedCallIds);
            return;
        }

        if (applicationName.equalsIgnoreCase("JOBAID")) {
            failedJobAidRecordValidator.validate(failedRecordRequest);
            validateDuplicateCallId(failedRecordRequest, validatedCallIds);
            return;
        }

        throw new FailedRecordValidationException("Invalid application name " + applicationName);
    }

    private void validateDuplicateCallId(FailedRecordCSVRequest failedRecordRequest, HashSet<String> validatedCallIds) {
        Map<String, String> fieldsToPostMap = failedRecordRequest.getFieldsToPostMap();
        String callId = fieldsToPostMap.get("callId");
        if (validatedCallIds.contains(callId)) {
            throw new FailedRecordValidationException("CallId " + callId + " present more than once");
        }
        validatedCallIds.add(callId);
    }

    private ValidationResponse constructValidationResponse(boolean isValid, List<Error> errors) {
        ValidationResponse validationResponse = new ValidationResponse(isValid);
        for (Error error : errors)
            validationResponse.addError(error);
        return validationResponse;
    }
}
