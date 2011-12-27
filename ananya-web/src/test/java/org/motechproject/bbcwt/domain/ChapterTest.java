package org.motechproject.bbcwt.domain;


import org.junit.Test;
import org.motechproject.bbcwt.util.UUIDUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ChapterTest {

    public static final String BELOW_PAR_SCORE_SUMMARY = "This is below par score summary";
    public static final String GOOD_SCORE_SUMMARY = "This is good score summary";
    public static final String CERTIFICATE_AND_COURSE_SUMMARY_PROMPT = "Want certificate and course summary?";
    public static final String JUST_COURSE_SUMMARY_PROMPT = "No certificate, but do you want just course summary prompt ?";

    @Test
    public void shouldReturnLessonGivenANumber() {
        Chapter chapter = new Chapter(1);
        Lesson lesson1 = new Lesson(1, "Lesson1", "Lesson1EndMenu");
        Lesson lesson2 = new Lesson(2, "Lesson2", "Lesson2EndMenu");
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
        Lesson lesson1 = new Lesson(1, "Lesson1", "Lesson1EndMenu");
        Lesson lesson2 = new Lesson(2, "Lesson2", "Lesson2EndMenu");
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
        Lesson lesson1 = new Lesson(1, "Lesson1", "Lesson1EndMenu");
        Lesson lesson2 = new Lesson(2, "Lesson2", "Lesson2EndMenu");

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
        Lesson lesson1 = new Lesson(1, "Lesson1", "Lesson1EndMenu");
        Lesson lesson2 = new Lesson(2, "Lesson2", "Lesson2EndMenu");
        Lesson lessonNotInChapter = new Lesson(3, "Lesson3", "Lesson3EndMenu");

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
        question1.setId(UUIDUtil.newUUID());
        Question question2 = new Question();
        question2.setId(UUIDUtil.newUUID());
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
        Lesson lessonNotInChapter = new Lesson(3, "Lesson3", "Lesson3EndMenu");
        String wantedLessonId = lessonNotInChapter.getId();

        Lesson obtainedLesson = chapter.getLessonById(wantedLessonId);

        assertNull(obtainedLesson);
    }

    @Test
    public void shouldReturnGoodScoreSummaryWhenScoreIsGood() {
        Chapter chapter = new Chapter(1);
        chapter.setBelowParScoreSummary(BELOW_PAR_SCORE_SUMMARY);
        chapter.setGoodScoreSummary(GOOD_SCORE_SUMMARY);
        final int someGoodScore = 3;
        assertThat(chapter.getSummaryForScore(someGoodScore), is(GOOD_SCORE_SUMMARY));
    }

    @Test
    public void shouldReturnBelowParScoreSummaryWhenScoreIsNotGood() {
        Chapter chapter = new Chapter(1);
        chapter.setBelowParScoreSummary(BELOW_PAR_SCORE_SUMMARY);
        chapter.setGoodScoreSummary(GOOD_SCORE_SUMMARY);
        final int someBadScore = 2;
        assertThat(chapter.getSummaryForScore(someBadScore), is(BELOW_PAR_SCORE_SUMMARY));
    }

    @Test
    public void shouldReturnCertificateAndSummaryPromptWhenScoreIsGood() {
        Chapter chapter = new Chapter(1);
        chapter.setCertificateAndCourseSummaryPrompt(CERTIFICATE_AND_COURSE_SUMMARY_PROMPT);
        chapter.setCourseSummaryPrompt(JUST_COURSE_SUMMARY_PROMPT);
        final int someGoodScore = 3;
        assertThat(chapter.getCourseSummaryPromptForScore(someGoodScore), is(CERTIFICATE_AND_COURSE_SUMMARY_PROMPT));
    }

    @Test
    public void shouldReturnJustCourseSummaryPromptWhenScoreIsNotGood() {
        Chapter chapter = new Chapter(1);
        chapter.setCertificateAndCourseSummaryPrompt(CERTIFICATE_AND_COURSE_SUMMARY_PROMPT);
        chapter.setCourseSummaryPrompt(JUST_COURSE_SUMMARY_PROMPT);
        final int someBadScore = 2;
        assertThat(chapter.getCourseSummaryPromptForScore(someBadScore), is(JUST_COURSE_SUMMARY_PROMPT));
    }
}