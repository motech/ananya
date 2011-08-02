package org.motechproject.bbcwt.domain;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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

    @Test
    public void getLessonByIdShouldReturnTheLessonWithPassedId() {
        Chapter chapter = new Chapter(1);
        Lesson lesson1 = new Lesson(1, "Lesson1");
        Lesson lesson2 = new Lesson(2, "Lesson2");

        chapter.addLesson(lesson1);
        chapter.addLesson(lesson2);

        String wantedLessonId = lesson1.getId();

        Lesson obtainedLesson = chapter.getLessonById(wantedLessonId);

        assertThat(obtainedLesson, is(lesson1));
    }

    @Test
    public void getLessonByIdShouldReturnNullIfThereIsNoLessonWithPassedId() {
        Chapter chapter = new Chapter(1);
        Lesson lesson1 = new Lesson(1, "Lesson1");
        Lesson lesson2 = new Lesson(2, "Lesson2");
        Lesson lessonNotInChapter = new Lesson(3, "Lesson3");

        chapter.addLesson(lesson1);
        chapter.addLesson(lesson2);

        String wantedLessonId = lessonNotInChapter.getId();

        Lesson obtainedLesson = chapter.getLessonById(wantedLessonId);

        assertNull(obtainedLesson);
    }

    @Test
    public void getLessonByIdShouldReturnNullIfThereAreNoLessons() {
        Chapter chapter = new Chapter(1);
        Lesson lessonNotInChapter = new Lesson(3, "Lesson3");
        String wantedLessonId = lessonNotInChapter.getId();

        Lesson obtainedLesson = chapter.getLessonById(wantedLessonId);

        assertNull(obtainedLesson);
    }
}