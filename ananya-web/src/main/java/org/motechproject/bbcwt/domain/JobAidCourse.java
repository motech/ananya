package org.motechproject.bbcwt.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

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

    public int numberOfLevels() {
        return levels.size();
    }

    public Level getLevelByNumber(final int number) {
        return (Level) CollectionUtils.find(this.levels(), new Predicate() {
            @Override
            public boolean evaluate(Object level) {
                return number == ((Level) level).number();
            }
        });
    }

    public Level nextLevel(Level level) {
        if(levels().contains(level)) {
            return this.getLevelByNumber(level.number()+1);
        }
        return null;
    }
}