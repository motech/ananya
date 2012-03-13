package org.motechproject.ananya.response;

import java.util.Map;

public class JobAidCallerDataResponse {
    private boolean isCallerRegistered;
    private Integer currentJobAidUsage;
    private Integer maxAllowedUsageForOperator;
    private Map<String, Integer> promptsHeard;

    public JobAidCallerDataResponse(boolean callerRegistered, Integer currentJobAidUsage, Integer maxAllowedUsageForOperator, Map<String, Integer> promptsHeard) {
        isCallerRegistered = callerRegistered;
        this.currentJobAidUsage = currentJobAidUsage;
        this.maxAllowedUsageForOperator = maxAllowedUsageForOperator;
        this.promptsHeard = promptsHeard;
    }

    public boolean isCallerRegistered() {
        return isCallerRegistered;
    }

    public Integer getCurrentJobAidUsage() {
        return currentJobAidUsage;
    }

    public Integer getMaxAllowedUsageForOperator() {
        return maxAllowedUsageForOperator;
    }

    public Map<String, Integer> getPromptsHeard() {
        return promptsHeard;
    }
}
