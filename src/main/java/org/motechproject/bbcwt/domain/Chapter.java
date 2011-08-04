package org.motechproject.bbcwt.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.ektorp.support.TypeDiscriminator;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'Chapter'")
public class Chapter extends BaseCouchEntity {
    private int number;

    private List<Lesson> lessons = new ArrayList<Lesson>();
    private List<Question> questions = new ArrayList<Question>();

    public Chapter() {
    }

    public Chapter(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public Lesson getLessonByNumber(final int number) {
        return (Lesson)CollectionUtils.find(this.getLessons(), new Predicate() {
            @Override
            public boolean evaluate(Object lesson) {
                return number == ((Lesson)lesson).getNumber();
            }
        });
    }

    public Lesson getLessonById(final String lessonId) {
        return (Lesson)CollectionUtils.find(lessons, new Predicate() {
            @Override
            public boolean evaluate(Object lesson) {
                return StringUtils.equals(lessonId, ((Lesson)lesson).getId());
            }
        });
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Question getQuestionByNumber(final int number) {
        return (Question)CollectionUtils.find(this.getQuestions(), new Predicate() {
            @Override
            public boolean evaluate(Object question) {
                return  number == ((Question)question).getNumber();
            }
        });
    }

    public Question getQuestionById(final String questionId) {
        return (Question)CollectionUtils.find(this.getQuestions(), new Predicate() {
            @Override
            public boolean evaluate(Object question) {
                return StringUtils.equals(questionId, ((Question)question).getId());
            }
        });
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }
}