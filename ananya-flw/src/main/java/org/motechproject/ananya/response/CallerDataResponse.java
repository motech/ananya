package org.motechproject.ananya.response;

import java.util.Map;

public class CallerDataResponse {
    private String bookmark;
    private boolean isCallerRegistered;
    private Map<String,Integer> scoresByChapter;
    private Integer currentJobAidUsage;

    public CallerDataResponse(String bookmark, boolean callerRegistered, Map<String, Integer> scoresByChapter, Integer currentJobAidUsage) {
        this.bookmark = bookmark;
        isCallerRegistered = callerRegistered;
        this.scoresByChapter = scoresByChapter;
        this.currentJobAidUsage = currentJobAidUsage;
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

    public Integer getCurrentJobAidUsage() {
        return currentJobAidUsage;
    }
}
