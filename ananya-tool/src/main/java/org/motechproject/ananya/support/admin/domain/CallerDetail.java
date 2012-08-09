package org.motechproject.ananya.support.admin.domain;

import org.apache.commons.lang.StringUtils;

public class CallerDetail {
    private String msisdn;
    private String name;

    public CallerDetail(String msisdn, String name) {
        this.msisdn = msisdn;
        this.name = name;
    }

    public CallerDetail() {
        msisdn = StringUtils.EMPTY;
        name = StringUtils.EMPTY;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public String getName() {
        return name;
    }
}
