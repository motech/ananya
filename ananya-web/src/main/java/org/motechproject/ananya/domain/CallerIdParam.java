package org.motechproject.ananya.domain;

public class CallerIdParam {

    private String value;

    public CallerIdParam(String value) {
        this.value = value.length() == 10 ? "91" + value : value;
    }

    public String getValue() {
        return value;
    }
}
