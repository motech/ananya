package org.motechproject.bbcwt.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Level;
import org.motechproject.bbcwt.repository.tree.NodeRepository;

import static org.mockito.MockitoAnnotations.initMocks;

public class JobAidContentServiceTest {
    @Mock
    private NodeRepository nodeRepository;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void addCourseShouldSaveTreeRepresentation() {
        JobAidCourse course = new JobAidCourse("JobAidCourse", "Welcome to Job Aid Course", "JobAidCourseMenu.wav");

        Lesson lvl1ch1lsn1 = new Lesson().setFileName("lvl1ch1lsn1.wav");
        Lesson lvl1ch1lsn2 = new Lesson().setFileName("lvl1ch1lsn2.wav");
        Lesson lvl1ch1lsn3 = new Lesson().setFileName("lvl1ch1lsn3.wav");

        Lesson lvl1ch2lsn1 = new Lesson().setFileName("lvl1ch2lsn1.wav");
        Lesson lvl1ch2lsn2 = new Lesson().setFileName("ch2lsn2.wav");

        Lesson lvl2ch3lsn1 = new Lesson().setFileName("lvl2ch3lsn1.wav");

        Lesson lvl3ch4lsn1 = new Lesson().setFileName("lvl3ch4lsn1.wav");

        Lesson lvl3ch5lsn1 = new Lesson().setFileName("lvl3ch5lsn1.wav");
        Lesson lvl3ch5lsn2 = new Lesson().setFileName("lvl3ch5lsn2.wav");



        Chapter lvl1ch1 = new Chapter().setNumber(1).setMenu("Chapter1Menu.wav")
                                .addLesson(lvl1ch1lsn1).addLesson(lvl1ch1lsn2).addLesson(lvl1ch1lsn3);

        Chapter lvl1ch2 = new Chapter().setNumber(2).setMenu("Chapter2Menu.wav")
                                .addLesson(lvl1ch2lsn1).addLesson(lvl1ch2lsn2);

        Chapter lvl2ch3 = new Chapter().setNumber(3).setMenu("Chapter3Menu.wav")
                                .addLesson(lvl2ch3lsn1);

        Chapter lvl3ch4 = new Chapter().setNumber(4).setMenu("Chapter4Menu.wav")
                                .addLesson(lvl3ch4lsn1);

        Chapter lvl3ch5 = new Chapter().setNumber(2).setMenu("Chapter4Menu.wav")
                                .addLesson(lvl3ch5lsn1).addLesson(lvl3ch5lsn2);

        Level level1 = new Level(1, "Level1Menu.wav").addChapter(lvl1ch1).addChapter(lvl1ch2);

        Level level2 = new Level(2, "Level2Menu.wav").addChapter(lvl2ch3);

        Level level3 = new Level(3, "Level3Menu.wav").addChapter(lvl3ch4).addChapter(lvl3ch5);

        course.addLevel(level1).addLevel(level2).addLevel(level3);

    }
}