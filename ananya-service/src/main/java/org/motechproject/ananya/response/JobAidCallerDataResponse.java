package org.motechproject.ananya.response;

import org.motechproject.ananya.domain.FrontLineWorker;

import java.util.HashMap;
import java.util.Map;

public class JobAidCallerDataResponse {
    private boolean isCallerRegistered;
    private String language;
    private Integer currentJobAidUsage;
    private Integer maxAllowedUsageForOperator;
    private Map<String, Integer> promptsHeard;

    public JobAidCallerDataResponse(FrontLineWorker frontLineWorker, Integer maxUsage) {
        this.isCallerRegistered = frontLineWorker.getStatus().isRegistered();
        this.currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage();
        this.promptsHeard = frontLineWorker.getPromptsHeard();
        this.maxAllowedUsageForOperator = maxUsage;
        this.language = frontLineWorker.getLanguage();
    }

    public JobAidCallerDataResponse() {
    }

    public static JobAidCallerDataResponse forNewUser(Integer maxOperatorUsage) {
        JobAidCallerDataResponse jobAidCallerDataResponse = new JobAidCallerDataResponse();
        jobAidCallerDataResponse.isCallerRegistered = false;
        jobAidCallerDataResponse.currentJobAidUsage = 0;
        jobAidCallerDataResponse.language = null;
        jobAidCallerDataResponse.promptsHeard = new HashMap<String, Integer>();
        jobAidCallerDataResponse.maxAllowedUsageForOperator = maxOperatorUsage;
        return jobAidCallerDataResponse;
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

	public String getLanguage() {
		return language;
	}
}
