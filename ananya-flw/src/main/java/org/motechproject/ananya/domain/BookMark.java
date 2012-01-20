package org.motechproject.ananya.domain;

import org.codehaus.jackson.annotate.JsonProperty;

public class BookMark {
    @JsonProperty
    private String type;
    @JsonProperty
    private String chapterIndex;
    @JsonProperty
    private String lessonIndex;

    public BookMark() {
    }

    public BookMark(String type, String chapterIndex, String lessonIndex) {
        this.type = type;
        this.chapterIndex = chapterIndex;
        this.lessonIndex = lessonIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookMark bookMark = (BookMark) o;

        if (chapterIndex != null ? !chapterIndex.equals(bookMark.chapterIndex) : bookMark.chapterIndex != null)
            return false;
        if (lessonIndex != null ? !lessonIndex.equals(bookMark.lessonIndex) : bookMark.lessonIndex != null)
            return false;
        if (type != null ? !type.equals(bookMark.type) : bookMark.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (chapterIndex != null ? chapterIndex.hashCode() : 0);
        result = 31 * result + (lessonIndex != null ? lessonIndex.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BookMark{type='" + type + '\'' + ", chapterIndex='" + chapterIndex + '\'' + ", lessonIndex='" + lessonIndex + '\'' + '}';
    }

    public String getType() {
        return type;
    }

    public String getChapterIndex() {
        return chapterIndex;
    }

    public String getLessonIndex() {
        return lessonIndex;
    }
}
