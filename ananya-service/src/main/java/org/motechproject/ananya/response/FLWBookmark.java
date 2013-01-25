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

    public FLWBookmark() {
    }

    public FLWBookmark(Integer chapter, Integer lesson) {
        this.chapter = convertTo1BasedIndex(chapter);
        this.lesson = convertTo1BasedIndex(lesson);
    }


    private Integer convertTo1BasedIndex(Integer value) {
        if(value == null) {
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

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
