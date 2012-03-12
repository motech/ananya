package org.motechproject.ananya.response;

public class JobAidCallerDataResponse {
    private boolean isCallerRegistered;
    private Boolean hasReachedMaxUsageForMonth;

    public JobAidCallerDataResponse(boolean callerRegistered, Boolean hasReachedMaxUsageForMonth) {
        isCallerRegistered = callerRegistered;
        this.hasReachedMaxUsageForMonth = hasReachedMaxUsageForMonth;
    }

    public boolean isCallerRegistered() {
        return isCallerRegistered;
    }

    public Boolean hasReachedMaxUsageForMonth() {
        return hasReachedMaxUsageForMonth;
    }
}
