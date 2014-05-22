package org.motechproject.ananya.response;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FLWBookmarkTest {

    @Test
    public void shouldChangeTheChapterAndLessonTo1BasedIndex() {
        assertEquals(new Integer(1), new FLWBookmark(0, 6).getChapter());
        assertEquals(new Integer(4), new FLWBookmark(0, 6).getLesson());
        assertNull(new FLWBookmark(null, null).getChapter());
        assertNull(new FLWBookmark(null, null).getLesson());
    }

    @Test
    public void shouldSplitLessonIndexToLessonAndQuizNumbers() {
        FLWBookmark flwBookmark1 = new FLWBookmark(1, 3);
        assertEquals(4, (int) flwBookmark1.getLesson());
        assertEquals(0, (int) flwBookmark1.getQuiz());

        FLWBookmark flwBookmark2 = new FLWBookmark(1, 6);
        assertEquals(4, (int) flwBookmark2.getLesson());
        assertEquals(3, (int) flwBookmark2.getQuiz());
    }
}
