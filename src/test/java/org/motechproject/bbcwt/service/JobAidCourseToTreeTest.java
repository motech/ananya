package org.motechproject.bbcwt.service;

import org.junit.Test;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Level;
import org.motechproject.bbcwt.domain.tree.Node;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.bbcwt.matcher.NodeDeepMatcher.isSameAsNodeRepresentedBy;

public class JobAidCourseToTreeTest {
    @Test
    public void shouldTranslateJobAidCourseToTree() {
        JobAidCourse course = course();
        JobAidCourseToTree courseToTree = new JobAidCourseToTree();

        Node transformedCourseInTree = courseToTree.transform(course);
        Node expectedCourseInTreeFormat = tree();

        assertThat(transformedCourseInTree, isSameAsNodeRepresentedBy(expectedCourseInTreeFormat));
    }

    @Test
    public void shouldTranslateTreeToJobAidCourse() {
        Node courseInTree = tree();

        JobAidCourse courseFromTree = new TreeToJobAidCourse().transform(courseInTree);
        Node courseRepresentationAgainInTree = new JobAidCourseToTree().transform(courseFromTree);

        assertThat(courseRepresentationAgainInTree, isSameAsNodeRepresentedBy(courseInTree));
    }

    private JobAidCourse course() {
        Lesson lvl1ch1lsn1 = new Lesson().setNumber(1).setFileName("lvl1ch1lsn1.wav");
        Lesson lvl1ch1lsn2 = new Lesson().setNumber(2).setFileName("lvl1ch1lsn2.wav");
        Lesson lvl1ch1lsn3 = new Lesson().setNumber(3).setFileName("lvl1ch1lsn3.wav");

        Lesson lvl1ch2lsn1 = new Lesson().setNumber(1).setFileName("lvl1ch2lsn1.wav");
        Lesson lvl1ch2lsn2 = new Lesson().setNumber(2).setFileName("lvl1ch2lsn2.wav");

        Lesson lvl2ch3lsn1 = new Lesson().setNumber(1).setFileName("lvl2ch3lsn1.wav");

        Lesson lvl3ch4lsn1 = new Lesson().setNumber(1).setFileName("lvl3ch4lsn1.wav");

        Lesson lvl3ch5lsn1 = new Lesson().setNumber(1).setFileName("lvl3ch5lsn1.wav");
        Lesson lvl3ch5lsn2 = new Lesson().setNumber(2).setFileName("lvl3ch5lsn2.wav");


        Chapter lvl1ch1 = new Chapter().setNumber(1).setMenu("Chapter1Menu.wav")
                                .addLesson(lvl1ch1lsn1).addLesson(lvl1ch1lsn2).addLesson(lvl1ch1lsn3);

        Chapter lvl1ch2 = new Chapter().setNumber(2).setMenu("Chapter2Menu.wav")
                                .addLesson(lvl1ch2lsn1).addLesson(lvl1ch2lsn2);

        Chapter lvl2ch3 = new Chapter().setNumber(3).setMenu("Chapter3Menu.wav")
                                .addLesson(lvl2ch3lsn1);

        Chapter lvl3ch4 = new Chapter().setNumber(4).setMenu("Chapter4Menu.wav")
                                .addLesson(lvl3ch4lsn1);

        Chapter lvl3ch5 = new Chapter().setNumber(5).setMenu("Chapter5Menu.wav")
                                .addLesson(lvl3ch5lsn1).addLesson(lvl3ch5lsn2);

        Level level1 = new Level(1, "Level1Menu.wav").addChapter(lvl1ch1).addChapter(lvl1ch2);

        Level level2 = new Level(2, "Level2Menu.wav").addChapter(lvl2ch3);

        Level level3 = new Level(3, "Level3Menu.wav").addChapter(lvl3ch4).addChapter(lvl3ch5);

        JobAidCourse course = new JobAidCourse("JobAidCourse", "Welcome to Job Aid Course", "JobAidCourseMenu.wav");
        return course.addLevel(level1).addLevel(level2).addLevel(level3);
    }

    private Node tree() {
        Node lvl1ch1lsn1Node = new Node("1").put("number", 1).put("lesson", "lvl1ch1lsn1.wav");
        Node lvl1ch1lsn2Node = new Node("2").put("number", 2).put("lesson", "lvl1ch1lsn2.wav");
        Node lvl1ch1lsn3Node = new Node("3").put("number", 3).put("lesson", "lvl1ch1lsn3.wav");

        Node lvl1ch2lsn1Node = new Node("1").put("number", 1).put("lesson", "lvl1ch2lsn1.wav");
        Node lvl1ch2lsn2Node = new Node("2").put("number", 2).put("lesson", "lvl1ch2lsn2.wav");


        Node lvl2ch3lsn1Node = new Node("1").put("number", 1).put("lesson", "lvl2ch3lsn1.wav");

        Node lvl3ch4lsn1Node = new Node("1").put("number", 1).put("lesson", "lvl3ch4lsn1.wav");

        Node lvl3ch5lsn1Node = new Node("1").put("number", 1).put("lesson", "lvl3ch5lsn1.wav");
        Node lvl3ch5lsn2Node = new Node("2").put("number", 2).put("lesson", "lvl3ch5lsn2.wav");

        Node lvl1ch1Node = new Node("1").put("number", 1).put("menu", "Chapter1Menu.wav")
                                .addChild(lvl1ch1lsn1Node).addChild(lvl1ch1lsn2Node).addChild(lvl1ch1lsn3Node);

        Node lvl1ch2Node = new Node("2").put("number", 2).put("menu", "Chapter2Menu.wav")
                                .addChild(lvl1ch2lsn1Node).addChild(lvl1ch2lsn2Node);

        Node lvl2ch3Node = new Node("3").put("number", 3).put("menu", "Chapter3Menu.wav")
                                .addChild(lvl2ch3lsn1Node);

        Node lvl3ch4Node = new Node("4").put("number", 4).put("menu", "Chapter4Menu.wav")
                                .addChild(lvl3ch4lsn1Node);

        Node lvl3ch5Node = new Node("5").put("number", 5).put("menu", "Chapter5Menu.wav")
                                .addChild(lvl3ch5lsn1Node).addChild(lvl3ch5lsn2Node);

        Node lvl1Node = new Node("1").put("number", 1).put("menu", "Level1Menu.wav")
                                .addChild(lvl1ch1Node).addChild(lvl1ch2Node);
        Node lvl2Node = new Node("2").put("number", 2).put("menu", "Level2Menu.wav")
                                .addChild(lvl2ch3Node);
        Node lvl3Node = new Node("3").put("number", 3).put("menu", "Level3Menu.wav")
                                .addChild(lvl3ch4Node).addChild(lvl3ch5Node);

        Node courseNode = new Node("JobAidCourse")
                           .put("introduction", "Welcome to Job Aid Course").put("menu", "JobAidCourseMenu.wav")
                           .addChild(lvl1Node).addChild(lvl2Node).addChild(lvl3Node);

        return courseNode;
    }
}