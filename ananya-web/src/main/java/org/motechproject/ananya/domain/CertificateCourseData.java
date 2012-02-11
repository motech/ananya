package org.motechproject.ananya.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CertificateCourseData {
    private String token;
    private CertificationCourseLog data;

    public CertificateCourseData(String token, CertificationCourseLog data) {
        this.token = token;
        this.data = data;
    }

    public String token() {
        return token;
    }

    public CertificationCourseLog data() {
        return data;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}