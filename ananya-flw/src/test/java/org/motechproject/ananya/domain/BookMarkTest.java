package org.motechproject.ananya.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        BookMark bookMark = new BookMark("playThanks", 9, 0);

        boolean isNotAtPlayCourseResult = bookMark.notAtPlayCourseResult();

        assertTrue(isNotAtPlayCourseResult);
    }

    @Test
    public void shouldReturnFalseWhenCurrentInteractionKeyIsAtPlayCourseResult() {
        BookMark bookMark = new BookMark(Interaction.PlayCourseResult, 9, 0);

        boolean isNotAtPlayCourseResult = bookMark.notAtPlayCourseResult();

        assertFalse(isNotAtPlayCourseResult);
    }
}
