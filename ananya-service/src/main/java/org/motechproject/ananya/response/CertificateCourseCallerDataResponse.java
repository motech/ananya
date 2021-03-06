package org.motechproject.ananya.response;

import org.motechproject.ananya.domain.FrontLineWorker;

import java.util.HashMap;
import java.util.Map;

public class CertificateCourseCallerDataResponse {
    private String bookmark;
    private boolean isCallerRegistered;
    private String language;
    private Map<String, Integer> scoresByChapter;

    public CertificateCourseCallerDataResponse(String bookmark, boolean callerRegistered, String language, Map<String, Integer> scoresByChapter) {
        this.bookmark = bookmark;
        this.language =language;
        isCallerRegistered = callerRegistered;
        this.scoresByChapter = scoresByChapter;
    }

    public CertificateCourseCallerDataResponse(FrontLineWorker frontLineWorker) {
        this.bookmark = frontLineWorker.bookMark().asJson();
        this.language= frontLineWorker.getLanguage();
        this.isCallerRegistered = frontLineWorker.getStatus().isRegistered();
        this.scoresByChapter = frontLineWorker.reportCard().scoresByChapterIndex();
    }

    public static CertificateCourseCallerDataResponse forNewUser() {
        return new CertificateCourseCallerDataResponse("{}", false, null, new HashMap<String, Integer>());
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
}
