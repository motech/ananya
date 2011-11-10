package org.motechproject.bbcwt.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JobAidCourse {
    private String name;
    private String introduction;
    private String menu;
    //TODO: Is this required to be a list? can it be a set?
    private List<Level>  levels = new ArrayList(10);

    public JobAidCourse(String name, String introduction, String menu) {
        this.name = name;
        this.introduction = introduction;
        this.menu = menu;
    }

    public String name() {
        return this.name;
    }

    public String introduction() {
        return this.introduction;
    }

    public String menu() {
        return this.menu;
    }

    public List<Level> levels() {
        return Collections.unmodifiableList(levels);
    }

    public JobAidCourse addLevel(Level level) {
        this.levels.add(level);
        return this;
    }
}