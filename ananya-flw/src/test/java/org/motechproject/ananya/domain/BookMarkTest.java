package org.motechproject.ananya.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

public class BookMarkTest {
    @Test
    public void shouldNotAddLessonIndexToBookmarkJsonWhenNotPresent() {
        BookMark bookMark = new BookMark("lesson", 0, null);
        String bookMarkJsonString = bookMark.asJson();
        final JsonElement bookMarkElement = new JsonParser().parse(bookMarkJsonString);
        final JsonObject bookMarkJson = bookMarkElement.getAsJsonObject();
        assertThat(bookMarkJson.get("type").getAsString(), is("lesson"));
        assertThat(bookMarkJson.get("chapterIndex").getAsInt(), is(0));
        assertThat(bookMarkJson.get("lessonIndex"), is(nullValue()));
    }

    @Test
    public void shouldNotAddChapterIndexToBookmarkJsonWhenNotPresent() {
        BookMark bookMark = new BookMark("welcome", null, null);
        String bookMarkJsonString = bookMark.asJson();
        final JsonElement bookMarkElement = new JsonParser().parse(bookMarkJsonString);
        final JsonObject bookMarkJson = bookMarkElement.getAsJsonObject();
        assertThat(bookMarkJson.get("type").getAsString(), is("welcome"));
        assertThat(bookMarkJson.get("lessonIndex"), is(nullValue()));
        assertThat(bookMarkJson.get("chapterIndex"), is(nullValue()));
    }

    @Test
    public void shouldAddLessonIndexToBookmarkJsonWhenPresent() {
        BookMark bookMark = new BookMark("lesson", 0, 2);
        String bookMarkJsonString = bookMark.asJson();
        final JsonElement bookMarkElement = new JsonParser().parse(bookMarkJsonString);
        final JsonObject bookMarkJson = bookMarkElement.getAsJsonObject();
        assertThat(bookMarkJson.get("type").getAsString(), is("lesson"));
        assertThat(bookMarkJson.get("lessonIndex").getAsInt(), is(2));
        assertThat(bookMarkJson.get("chapterIndex").getAsInt(), is(0));
    }

    @Test
    public void shouldReturnTrueWhenCurrentInteractionKeyIsNotPlayCourseResult() {
        BookMark bookMark = new BookMark("playCourseResult", 9, 0);

        boolean isNotAtPlayThanks = bookMark.notAtPlayThanks();

        assertTrue(isNotAtPlayThanks);
    }

    @Test
    public void shouldReturnFalseWhenCurrentInteractionKeyIsAtPlayCourseResult() {
        BookMark bookMark = new BookMark(Interaction.PlayThanks, 9, 0);

        boolean isNotAtPlayThanks = bookMark.notAtPlayThanks();

        assertFalse(isNotAtPlayThanks);
    }

    @Test
    public void shouldReturnIfBookmarkIsEmpty() {
        assertFalse(new BookMark("some", null, null).isEmptyBookmark());
        assertTrue(new EmptyBookmark().isEmptyBookmark());
    }

    @Test
    public void testComparison() {
        BookMark bookMark1 = new BookMark();
        BookMark bookMark2 = new BookMark();
        assertEquals(0, bookMark2.compareTo(bookMark1));
        assertEquals(0, bookMark1.compareTo(bookMark2));
        bookMark1 = new BookMark();
        bookMark2 = new BookMark(null, 1, 1);
        assertEquals(bookMark2, ObjectUtils.max(bookMark1, bookMark2));
        bookMark1 = new BookMark(null, 1, 1);
        assertEquals(0, bookMark2.compareTo(bookMark1));
        assertEquals(0, bookMark1.compareTo(bookMark2));
        bookMark1 = new BookMark(null, 1, 2);
        assertEquals(bookMark1, ObjectUtils.max(bookMark1, bookMark2));
        bookMark2 = new BookMark(null, 2, 1);
        assertEquals(bookMark2, ObjectUtils.max(bookMark1, bookMark2));
    }

    @Test
    public void shouldDoNullSafeComparison() {
        BookMark bookMark1 = new BookMark(null, null, null);
        BookMark bookMark2 = new BookMark(null, null, 1);
        assertEquals(bookMark2, ObjectUtils.max(bookMark1, bookMark2));
        bookMark1 = new BookMark(null, null, null);
        bookMark2 = new BookMark(null, 1, null);
        assertEquals(bookMark2, ObjectUtils.max(bookMark1, bookMark2));
    }
}
