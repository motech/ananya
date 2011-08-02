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

    public Lesson getLessonByNumber(int number) {
        for(Lesson lesson:this.getLessons()) {
            if(lesson.getNumber() == number) {
                return lesson;
            }
        }
        return null;
    }

    public Lesson getLessonById(final String lessonId) {
        return (Lesson)CollectionUtils.find(lessons, new Predicate() {
            @Override
            public boolean evaluate(Object lesson) {
                return StringUtils.equals(lessonId, ((Lesson)lesson).getId());
            }
        });
    }
}