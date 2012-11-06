package org.motechproject.ananya.response;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ValidationResponse {
    private List<String> errors = new ArrayList<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String message) {
        errors.add(message);
    }

    public String getErrorMessage() {
        return StringUtils.join(errors, ",");
    }
}
