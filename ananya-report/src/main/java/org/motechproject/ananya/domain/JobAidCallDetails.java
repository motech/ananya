package org.motechproject.ananya.domain;

import org.joda.time.DateTime;

public class JobAidCallDetails {
    private final DateTime startTime;
    private final DateTime endTime;
    private final Integer jobAidDurationInPulse;

    public JobAidCallDetails(DateTime startTime, DateTime endTime, Integer jobAidDurationInPulse) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.jobAidDurationInPulse = jobAidDurationInPulse;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public Integer getDurationInPulse() {
        return jobAidDurationInPulse;
    }
}
