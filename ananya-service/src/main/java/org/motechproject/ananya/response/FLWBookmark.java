package org.motechproject.ananya.response;

public class FLWBookmark {
    private Integer chapter;
    private Integer lesson;

    public FLWBookmark(Integer chapter, Integer lesson) {
        this.chapter = chapter;
        this.lesson = lesson;
    }

    public Integer getChapter() {
        return chapter;
    }

    public Integer getLesson() {
        return lesson;
    }
}
