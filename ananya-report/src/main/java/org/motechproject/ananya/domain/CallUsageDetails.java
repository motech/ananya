package org.motechproject.ananya.domain;

public class CallUsageDetails {
    private final Long jobAidDurationInSec;
    private final Long certificateCourseDurationInSec;
    private final Integer year;
    private final Integer month;

    public CallUsageDetails(Long jobAidDurationInSec, Long certificateCourseDurationInSec, Integer year, Integer month) {
        this.jobAidDurationInSec = jobAidDurationInSec;
        this.certificateCourseDurationInSec = certificateCourseDurationInSec;
        this.year = year;
        this.month = month;
    }

    public Long getJobAidDurationInSec() {
        return jobAidDurationInSec;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Long getCertificateCourseDurationInSec() {
        return certificateCourseDurationInSec;
    }
}
