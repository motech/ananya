package org.motechproject.bbcwt.domain;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JobAidCourse {
    private String name;
    private String courseIntroduction;
    private String menu;
    //TODO: Is this required to be a list? can it be a set?
    private List<Level>  levels = new ArrayList(10);

    public JobAidCourse(String name, String courseIntroduction, String menu, List<Level> levels) {
        this.name = name;
        this.courseIntroduction = courseIntroduction;
        this.menu = menu;
        this.levels = levels;
    }

    public String name() {
        return this.name;
    }

    public String courseIntroduction() {
        return this.courseIntroduction;
    }

    public String menu() {
        return this.menu;
    }

    public List<Level> levels() {
        return Collections.unmodifiableList(levels);
    }

    public void addLevel(Level level) {
        this.levels.add(level);
    }
}