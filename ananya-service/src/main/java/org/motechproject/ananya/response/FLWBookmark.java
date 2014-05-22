package org.motechproject.ananya.response;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FLWBookmark {
    @XmlElement
    private Integer chapter;
    @XmlElement
    private Integer lesson;
    @XmlElement
    private Integer quiz;

    public static final int NUMBER_OF_LESSONS_IN_A_CHAPTER = 4;

    public FLWBookmark() {
    }

    public FLWBookmark(Integer chapter, Integer lessonIndex) {
        this.chapter = convertTo1BasedIndex(chapter);
        splitToLessonAndQuizNumbers(convertTo1BasedIndex(lessonIndex));
    }

    private void splitToLessonAndQuizNumbers(Integer lessonIndex) {
        if (lessonIndex == null)
            return;

        if (lessonIndex > NUMBER_OF_LESSONS_IN_A_CHAPTER) {
            this.lesson = NUMBER_OF_LESSONS_IN_A_CHAPTER;
            this.quiz = lessonIndex - NUMBER_OF_LESSONS_IN_A_CHAPTER;
        } else {
            this.lesson = lessonIndex;
            this.quiz = 0;
        }
    }

    private Integer convertTo1BasedIndex(Integer value) {
        if (value == null) {
            return null;
        }
        return value + 1;
    }

    public Integer getChapter() {
        return chapter;
    }

    public Integer getLesson() {
        return lesson;
    }

    public Integer getQuiz() {
        return quiz;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
