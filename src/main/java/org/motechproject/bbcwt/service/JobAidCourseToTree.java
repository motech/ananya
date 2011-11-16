package org.motechproject.bbcwt.service;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Level;
import org.motechproject.bbcwt.domain.tree.Node;
import org.springframework.stereotype.Component;

@Component
public class JobAidCourseToTree {
    public Node transform(JobAidCourse course) {
        Node root = new Node(course.name());
        root.put("introduction", course.introduction());
        root.put("menu", course.menu());

        for(Level level : course.levels()) {
            root.addChild(transformLevel(level));
        }

        return root;
    }

    private Node transformLevel(Level level) {
        Node levelNode = new Node(level.number()+"");

        levelNode.put("introduction", level.introduction());
        levelNode.put("menu", level.menu());
        levelNode.put("number", level.number());

        for(Chapter chapter : level.chapters()) {
            levelNode.addChild(transformChapter(chapter));
        }

        return levelNode;
    }

    private Node transformChapter(Chapter chapter) {
        Node chapterNode = new Node(chapter.getNumber()+"");

        chapterNode.put("menu", chapter.menu());
        chapterNode.put("number", chapter.getNumber());
        chapterNode.put("title", chapter.title());
        for(Lesson lesson: chapter.getLessons()) {
            chapterNode.addChild(transformLesson(lesson)) ;
        }

        return chapterNode;
    }

    private Node transformLesson(Lesson lesson) {
        Node lessonNode = new Node(lesson.getNumber()+"");

        lessonNode.put("number", lesson.getNumber());
        lessonNode.put("lesson", lesson.getFileName());
        return lessonNode;
    }
}