package org.motechproject.ananya.response;

import org.motechproject.ananya.domain.FrontLineWorker;

import java.util.HashMap;
import java.util.Map;

public class JobAidCallerDataResponse {
    private boolean isCallerRegistered;
    private String language;
    private Integer currentJobAidUsage;
    private Integer currentCertificatCourseUsage;
    private Integer maxAllowedUsageForOperator;
    private Map<String, Integer> promptsHeard;
    private Map<String, Integer> promptsHeardForMA;

    public JobAidCallerDataResponse(FrontLineWorker frontLineWorker, Integer maxUsage) {
        this.isCallerRegistered = frontLineWorker.getStatus().isRegistered();
        this.currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage();
        this.promptsHeard = frontLineWorker.getPromptsHeard();
        this.promptsHeardForMA = frontLineWorker.getPromptsHeardForMA();
        this.maxAllowedUsageForOperator = maxUsage;
        this.language = frontLineWorker.getLanguage();
        this.currentCertificatCourseUsage = frontLineWorker.getCurrentCourseUsage();
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
        jobAidCallerDataResponse.currentCertificatCourseUsage = 0;
        return jobAidCallerDataResponse;
    }
    
    public JobAidCallerDataResponse(FrontLineWorker frontLineWorker) {
        this.isCallerRegistered = frontLineWorker.getStatus().isRegistered();
        this.language = frontLineWorker.getLanguage();
        this.promptsHeard = frontLineWorker.getPromptsHeard();
    }

    public JobAidCallerDataResponse(FrontLineWorker frontLineWorker,
			int maxUsage, boolean enquirey) {
    	this.isCallerRegistered = frontLineWorker.getStatus().isRegistered();
        this.currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage();
        this.promptsHeard = frontLineWorker.getPromptsHeard();
        this.maxAllowedUsageForOperator = maxUsage;
        this.language = frontLineWorker.getLanguage();
	}



	public static JobAidCallerDataResponse forNewUser() {
        JobAidCallerDataResponse jobAidCallerDataResponse = new JobAidCallerDataResponse();
        jobAidCallerDataResponse.isCallerRegistered = false;
        jobAidCallerDataResponse.language = null;
        return jobAidCallerDataResponse;
    }
    
    public boolean isCallerRegistered() {
        return isCallerRegistered;
    }

    public Integer getCurrentJobAidUsage() {
        return currentJobAidUsage;
    }

    public Integer getCurrentCertificatCourseUsage() {
		return currentCertificatCourseUsage;
	}
    
    public Integer getMaxAllowedUsageForOperator() {
        return maxAllowedUsageForOperator;
    }

    public Map<String, Integer> getPromptsHeard() {
        return promptsHeard;
    }
    
    /*
	 * Objective of this method is to get the max usage prompts heard across ma and mk and add it to prompts heard before sending to front end
	 */
    public Map<String, Integer> getPromptsHeardForCombinedCapping() {
    	if(promptsHeardForMA!=null && promptsHeardForMA.get("max_usage")!=null && promptsHeardForMA.get("max_usage")!=0){
    	   int max_usage_heard_number = promptsHeardForMA.get("max_usage")+(promptsHeard.get("max_usage")!=null?promptsHeard.get("max_usage"):0);
 		   promptsHeard.put("max_usage", max_usage_heard_number);
    	}
        return promptsHeard;
    }

	public String getLanguage() {
		return language;
	}
}
