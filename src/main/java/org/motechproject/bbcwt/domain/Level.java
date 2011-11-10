package org.motechproject.bbcwt.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {
    private int number;
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
}