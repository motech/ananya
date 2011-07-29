package org.motechproject.bbcwt.domain;


import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ChapterTest {
    @Test
    public void shouldReturnLessonGivenANumber() {
        Chapter chapter = new Chapter(1);
        Lesson lesson1 = new Lesson(1, "Lesson1");
        Lesson lesson2 = new Lesson(2, "Lesson2");
        chapter.addLesson(lesson1);
        chapter.addLesson(lesson2);

        Lesson lessonWithNumber1 = chapter.getLessonByNumber(1);

        assertEquals("Should return the lesson with correct number", lessonWithNumber1, lesson1);

    }

    @Test
    public void shouldReturnNullIfNoLessonWithGivenNumberExists() {
        Chapter chapter = new Chapter(1);
        Lesson lesson1 = new Lesson(1, "Lesson1");
        Lesson lesson2 = new Lesson(2, "Lesson2");
        chapter.addLesson(lesson1);
        chapter.addLesson(lesson2);

        Lesson nonExistentLesson = chapter.getLessonByNumber(3);

        assertNull("Should return null if there is no lesson with the number.", nonExistentLesson);

        Chapter chapterWithNoLessons = new Chapter(2);
        nonExistentLesson = chapterWithNoLessons.getLessonByNumber(1);
        assertNull("Should return null if there are no lessons at all.", nonExistentLesson);
    }
}