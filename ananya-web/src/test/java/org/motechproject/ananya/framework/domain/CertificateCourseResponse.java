package org.motechproject.ananya.framework.domain;

import org.motechproject.ananya.domain.BookMark;
import org.motechproject.dao.MotechJsonReader;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.lang.StringUtils.removeEnd;
import static org.apache.commons.lang.StringUtils.removeStart;

public class CertificateCourseResponse {

    private boolean isRegistered;
    private BookMark bookmark;
    private Map<String, Integer> scoresByChapter;

    public CertificateCourseResponse confirmPartiallyRegistered() {
        assertFalse(isRegistered);
        return this;
    }

    public static CertificateCourseResponse make(String json) {
        MotechJsonReader jsonReader = new MotechJsonReader();
        String callerData = removeEnd(removeStart(json, "var callerData = "), ";");
        return (CertificateCourseResponse) jsonReader.readFromString(callerData, CertificateCourseResponse.class);
    }

    public static CertificateCourseResponse makeForNonJson(String asString) {
        if (asString == "<dummy/>")
            return new CertificateCourseResponse();
        return null;
    }

    public CertificateCourseResponse confirmEmptyBookMark() {
        assertTrue(bookmark.equals(bookmark));
        return this;
    }

    public CertificateCourseResponse confirmEmptyScores() {
        assertTrue(scoresByChapter.isEmpty());
        return this;
    }

    public CertificateCourseResponse confirmBookMarkAt(int chapIndex, int lessonIndex) {
        assertTrue(bookmark.getChapterIndex().equals(chapIndex));
        assertTrue(bookmark.getLessonIndex().equals(lessonIndex));
        return this;
    }

    public CertificateCourseResponse confirmScores(Map<String, Integer> scoresMap) {
        for (String key : scoresMap.keySet())
            assertEquals(scoresMap.get(key), scoresByChapter.get(key));
        return this;
    }
}
