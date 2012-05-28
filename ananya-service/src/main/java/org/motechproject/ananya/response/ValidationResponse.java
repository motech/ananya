package org.motechproject.ananya.response;

public class ValidationResponse {
    private String message;
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

    public ValidationResponse forInvalidMsisdn() {
        message = "Invalid msisdn";
        isValid = false;
        return this;
    }

    public ValidationResponse forInvalidName() {
        message = "Invalid name";
        isValid = false;
        return this;
    }

    public ValidationResponse forInvalidLocation() {
        message = "Invalid location";
        isValid = false;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "message=\"" + message + '"' +
                '}';
    }
}
