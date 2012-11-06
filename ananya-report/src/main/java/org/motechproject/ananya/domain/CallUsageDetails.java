package org.motechproject.ananya.domain;

public class CallUsageDetails {
    private final Long jobAidDurationInPulse;
    private final Long certificateCourseDurationInPulse;
    private final Integer year;
    private final Integer month;

    public CallUsageDetails(Long jobAidDurationInPulse, Long certificateCourseDurationInPulse, Integer year, Integer month) {
        this.jobAidDurationInPulse = jobAidDurationInPulse;
        this.certificateCourseDurationInPulse = certificateCourseDurationInPulse;
        this.year = year;
        this.month = month;
    }

    public Long getJobAidDurationInPulse() {
        return jobAidDurationInPulse;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Long getCertificateCourseDurationInPulse() {
        return certificateCourseDurationInPulse;
    }
}
