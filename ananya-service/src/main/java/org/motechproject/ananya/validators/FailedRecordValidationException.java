package org.motechproject.ananya.validators;

public class FailedRecordValidationException extends RuntimeException {

    public FailedRecordValidationException(String error) {
        super(error);
    }
}
