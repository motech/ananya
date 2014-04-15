package org.motechproject.ananya.response;

import org.motechproject.ananya.domain.FrontLineWorker;

import java.util.HashMap;
import java.util.Map;

public class CertificateCourseCallerDataWithUsageForCappingResponse {
    private String bookmark;
    private boolean isCallerRegistered;
    private String language;
    private Integer currentCertificatCourseUsage ;
    private Integer maxAllowedUsageForOperator ;
    private Integer currentJobAidUsage;
    private Map<String, Integer> promptsHeardForMA;
    
    private Map<String, Integer> scoresByChapter;

    public CertificateCourseCallerDataWithUsageForCappingResponse(String bookmark, boolean callerRegistered, String language, Map<String, Integer> scoresByChapter) {
        this.bookmark = bookmark;
        this.language =language;
        isCallerRegistered = callerRegistered;
        this.scoresByChapter = scoresByChapter;
    }

    public CertificateCourseCallerDataWithUsageForCappingResponse(FrontLineWorker frontLineWorker, Integer maxUsage) {
        this.bookmark = frontLineWorker.bookMark().asJson();
        this.language= frontLineWorker.getLanguage();
        this.isCallerRegistered = frontLineWorker.getStatus().isRegistered();
        this.scoresByChapter = frontLineWorker.reportCard().scoresByChapterIndex();
        this.currentCertificatCourseUsage = frontLineWorker.getCurrentCourseUsage();
        this.currentJobAidUsage =frontLineWorker.getCurrentJobAidUsage();
        this.promptsHeardForMA = frontLineWorker.getPromptsHeardForMA();
        this.maxAllowedUsageForOperator = maxUsage;
    }

   /* private Integer getCombinedUsage(FrontLineWorker frontLineWorker) {	
		return frontLineWorker.getCurrentCourseUsage()+frontLineWorker.getCurrentJobAidUsage();
	}*/

	public static CertificateCourseCallerDataWithUsageForCappingResponse forNewUser(Integer maxOperatorUsage) {
		CertificateCourseCallerDataWithUsageForCappingResponse ccResponse=  new CertificateCourseCallerDataWithUsageForCappingResponse("{}", false, null, new HashMap<String, Integer>());
		ccResponse.maxAllowedUsageForOperator = maxOperatorUsage;
		ccResponse.currentCertificatCourseUsage = 0;
		ccResponse.currentJobAidUsage = 0;
		ccResponse.promptsHeardForMA = new HashMap<String, Integer>();
		return ccResponse;
	}

    public String getBookmark() {
        return bookmark;
    }

    public boolean isCallerRegistered() {
        return isCallerRegistered;
    }

    public Map<String, Integer> getScoresByChapter() {
        return scoresByChapter;
    }

	public String getLanguage() {
		return language;
	}

	public Integer getCurrentCertificatCourseUsage() {
		return currentCertificatCourseUsage;
	}

	public Integer getCombinedUsage() {
		return currentJobAidUsage;
	}

	public Integer getMaxAllowedUsageForOperator() {
		return maxAllowedUsageForOperator;
	}

	public Map<String, Integer> getPromptsHeardForMA() {
		return promptsHeardForMA;
	}
	
	
}
