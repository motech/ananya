package org.motechproject.ananya.response;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FLWBookmarkTest {
    
    @Test
    public void shouldChangeTheChapterAndLessonTo1BasedIndex() {
        assertEquals(new Integer(1), new FLWBookmark(0, 6).getChapter());
        assertEquals(new Integer(7), new FLWBookmark(0, 6).getLesson());
        assertNull(new FLWBookmark(null, null).getChapter());
        assertNull(new FLWBookmark(null, null).getLesson());
    }
}
