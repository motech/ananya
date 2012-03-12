package org.motechproject.ananya.response;

import java.util.Map;

public class JobAidCallerDataResponse {
    private boolean isCallerRegistered;
    private Boolean hasReachedMaxUsageForMonth;
    private Map<String, Integer> promptsHeard;

    public JobAidCallerDataResponse(boolean callerRegistered, Boolean hasReachedMaxUsageForMonth,
                                    Map<String, Integer> promptsHeard) {
        isCallerRegistered = callerRegistered;
        this.hasReachedMaxUsageForMonth = hasReachedMaxUsageForMonth;
        this.promptsHeard = promptsHeard;
    }

    public boolean isCallerRegistered() {
        return isCallerRegistered;
    }

    public Boolean hasReachedMaxUsageForMonth() {
        return hasReachedMaxUsageForMonth;
    }

    public Map<String, Integer> getPromptsHeard() {
        return promptsHeard;
    }
}
