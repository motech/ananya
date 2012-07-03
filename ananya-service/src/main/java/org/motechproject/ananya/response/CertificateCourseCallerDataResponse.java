package org.motechproject.ananya.response;

import org.motechproject.ananya.domain.FrontLineWorker;

import java.util.Map;

public class CertificateCourseCallerDataResponse {
    private String bookmark;
    private boolean isCallerRegistered;
    private Map<String, Integer> scoresByChapter;

    public CertificateCourseCallerDataResponse(String bookmark, boolean callerRegistered, Map<String, Integer> scoresByChapter) {
        this.bookmark = bookmark;
        isCallerRegistered = callerRegistered;
        this.scoresByChapter = scoresByChapter;
    }

    public CertificateCourseCallerDataResponse(FrontLineWorker frontLineWorker) {
        this.bookmark = frontLineWorker.bookMark().asJson();
        this.isCallerRegistered = frontLineWorker.getStatus().isRegistered();
        this.scoresByChapter = frontLineWorker.reportCard().scoresByChapterIndex();
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
}
