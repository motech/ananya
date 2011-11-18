package org.motechproject.bbcwt.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {
    private int number;
    private String introduction;
    private String menu;
    private List<Chapter> chapters = new ArrayList<Chapter>(10);

    public Level(int number, String menu) {
        this.number = number;
        this.menu = menu;
    }

    public int number() {
        return this.number;
    }

    public String menu() {
        return this.menu;
    }

    public List<Chapter> chapters() {
        return Collections.unmodifiableList(chapters);
    }

    public Level addChapter(Chapter chapter) {
        this.chapters.add(chapter);
        return this;
    }

    public String introduction() {
        return introduction;
    }

    public Level setIntroduction(String introduction) {
        this.introduction = introduction;
        return this;
    }

    public boolean hasChapters() {
        return chapters.size() > 0;
    }

    public boolean hasLessons() {
        for(Chapter chapter: chapters) {
            if(chapter.hasLessons()) {
                return true;
            }
        }
        return false;
    }

    public int numberOfChapters() {
        return chapters.size();
    }

    public Chapter getChapterByNumber(final int number) {
        return (Chapter) CollectionUtils.find(this.chapters(), new Predicate() {
            @Override
            public boolean evaluate(Object chapter) {
                return number == ((Chapter) chapter).getNumber();
            }
        });
    }

    public Chapter nextChapter(Chapter chapter) {
        if(chapters().contains(chapter)) {
            return this.getChapterByNumber(chapter.getNumber()+1);
        }
        return null;
    }
}