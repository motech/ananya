package org.motechproject.ananya.domain;

import org.apache.commons.lang.StringUtils;

public class PhoneNumber {
    private String msisdn;

    public PhoneNumber(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getFormattedMsisdn() {
        return isValid() ? (msisdn.length() == 10 ? "91" + msisdn : msisdn) : null;
    }

    public boolean isValid() {
        return (StringUtils.isNotBlank(msisdn) && StringUtils.isNumeric(msisdn) && (msisdn.length() == 10 || (msisdn.length() == 12 && (msisdn.startsWith("91")))));
    }
}
