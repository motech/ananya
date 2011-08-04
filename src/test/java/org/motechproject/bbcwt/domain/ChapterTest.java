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
    public void shouldReturnQuestionGivenANumber() {
        Chapter chapter = new Chapter(1);
        Question question1 = new Question();
        question1.setNumber(1);
        Question question2 = new Question();
        question2.setNumber(2);
        chapter.addQuestion(question1);
        chapter.addQuestion(question2);

        Question questionWithNumber1 = chapter.getQuestionByNumber(1);

        assertEquals("Should return the question with correct number", questionWithNumber1, question1);

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
    public void shouldReturnNullIfNoQuestionWithGivenNumberExists() {
        Chapter chapter = new Chapter(1);
        Question question1 = new Question();
        question1.setNumber(1);
        Question question2 = new Question();
        question2.setNumber(2);
        chapter.addQuestion(question1);
        chapter.addQuestion(question2);


        Question nonExistentQuestion = chapter.getQuestionByNumber(3);

        assertNull("Should return null if there is no question with the number.", nonExistentQuestion);

        Chapter chapterWithNoQuestions = new Chapter(2);
        nonExistentQuestion = chapterWithNoQuestions.getQuestionByNumber(1);
        assertNull("Should return null if there are no questions at all.", nonExistentQuestion);
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
    public void getQuestionByIdShouldReturnTheQuestionWithPassedId() {
        Chapter chapter = new Chapter(1);
        Question question1 = new Question();
        Question question2 = new Question();
        question1.setNumber(1);
        question2.setNumber(2);
        chapter.addQuestion(question1);
        chapter.addQuestion(question2);

        String wantedQuestionId = question1.getId();

        Question obtainedQuestion = chapter.getQuestionById(wantedQuestionId);

        assertThat(obtainedQuestion, is(question1));
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
    public void getQuestionByIdShouldReturnNullIfThereIsNoQuestionWithPassedId() {
        Chapter chapter = new Chapter(1);
        Question question1 = new Question();
        Question question2 = new Question();
        question1.setNumber(1);
        question2.setNumber(2);
        chapter.addQuestion(question1);
        chapter.addQuestion(question2);

        Question questionNotInChapter = new Question();
        questionNotInChapter.setNumber(3);

        String wantedQuestionId = questionNotInChapter.getId();

        Question obtainedQuestion = chapter.getQuestionById(wantedQuestionId);

        assertNull(obtainedQuestion);
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