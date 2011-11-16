package org.motechproject.bbcwt.ivr.jobaid;

public class JobAidFlowState {
    private int level;
    private int chapter;
    private int lesson;

    public int level() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int chapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int lesson() {
        return lesson;
    }

    public void setLesson(int lesson) {
        this.lesson = lesson;
    }
}