package org.motechproject.ananya.domain;

import org.junit.Assert;
import org.junit.Test;

public class BookMarkTest {
    @Test
    public void shouldNotAddLessonIndexToBookmarkJsonWhenNotPresent() {
        BookMark bookMark = new BookMark("lesson", "0", null);
        String bookmarkJson = bookMark.asJson();
        String expectedBookmark = "{\"type\" : \"lesson\" , \"chapterIndex\" : \"0\"}";
        Assert.assertEquals(expectedBookmark, bookmarkJson);
    }

    @Test
    public void shouldAddLessonIndexToBookmarkJsonWhenPresent() {
        BookMark bookMark = new BookMark("lesson", "0", "2");
        String bookmarkJson = bookMark.asJson();
        String expectedBookmark = "{\"type\" : \"lesson\" , \"chapterIndex\" : \"0\" , \"lessonIndex\" : \"2\"}";
        Assert.assertEquals(expectedBookmark, bookmarkJson);
    }

}
