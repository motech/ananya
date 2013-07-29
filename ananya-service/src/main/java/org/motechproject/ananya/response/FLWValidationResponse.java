package org.motechproject.ananya.response;

public class FLWValidationResponse {
    private String message = "";
    private boolean isValid = true;

    public boolean isValid() {
        return isValid;
    }

    public boolean isInValid() {
        return !isValid;
    }

    public String getMessage() {
        return message;
    }

    public FLWValidationResponse forInvalidMsisdn(final String fieldName) {
        message += "[Invalid " + fieldName + "]";
        isValid = false;
        return this;
    }

    public FLWValidationResponse forInvalidName() {
        message += "[Invalid name]";
        isValid = false;
        return this;
    }

    public FLWValidationResponse forInvalidLocation() {
        message += "[Invalid location]";
        isValid = false;
        return this;
    }

    public FLWValidationResponse forDuplicates() {
        message += "[Found duplicate FLW with the same MSISDN]";
        isValid = false;
        return this;
    }

    public FLWValidationResponse forInvalidFlwId() {
        message += "[Invalid flwId]";
        isValid = false;
        return this;
    }

    public FLWValidationResponse forInvalidVerificationStatus() {
        message += "[Invalid verification status]";
        isValid = false;
        return this;
    }
}
