package org.motechproject.bbcwt.tools.seed;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Level;
import org.motechproject.bbcwt.service.JobAidContentService;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobAidSeed{
    @Autowired
    private JobAidContentService jobAidContentService;

    @Seed(priority = 0)
    public void load() {
        Lesson lvl1ch1lsn1 = new Lesson().setNumber(1).setFileName("jobAid/level_1_chapter_1_lesson_1.wav");
        Lesson lvl1ch1lsn2 = new Lesson().setNumber(2).setFileName("jobAid/level_1_chapter_1_lesson_2.wav");
        Lesson lvl1ch2lsn1 = new Lesson().setNumber(1).setFileName("jobAid/level_1_chapter_2_lesson_1.wav");
        Lesson lvl1ch2lsn2 = new Lesson().setNumber(2).setFileName("jobAid/level_1_chapter_2_lesson_2.wav");

        Chapter lvl1ch1 = new Chapter().setNumber(1).
                                        setTitle("jobAid/level_1_chapter_1_intro.wav").
                                        setMenu("jobAid/level_1_chapter_1_menu.wav").
                                        addLesson(lvl1ch1lsn1).
                                        addLesson(lvl1ch1lsn2);

        Chapter lvl1ch2 = new Chapter().setNumber(1).
                                        setTitle("jobAid/level_1_chapter_2_intro.wav").
                                        setMenu("jobAid/level_1_chapter_2_menu.wav").
                                        addLesson(lvl1ch2lsn1).
                                        addLesson(lvl1ch2lsn2);

        Lesson lvl2ch5lsn1 = new Lesson().setNumber(1).setFileName("jobAid/chapter_5_lesson_1.wav");
        Lesson lvl2ch5lsn2 = new Lesson().setNumber(2).setFileName("jobAid/chapter_5_lesson_2.wav");
        Lesson lvl2ch5lsn3 = new Lesson().setNumber(3).setFileName("jobAid/chapter_5_lesson_3.wav");
        Lesson lvl2ch5lsn4 = new Lesson().setNumber(4).setFileName("jobAid/chapter_5_lesson_4.wav");

        Lesson lvl2ch6lsn1 = new Lesson().setNumber(1).setFileName("jobAid/chapter_6_lesson_1.wav");
        Lesson lvl2ch6lsn2 = new Lesson().setNumber(2).setFileName("jobAid/chapter_6_lesson_2.wav");
        Lesson lvl2ch6lsn3 = new Lesson().setNumber(3).setFileName("jobAid/chapter_6_lesson_3.wav");
        Lesson lvl2ch6lsn4 = new Lesson().setNumber(4).setFileName("jobAid/chapter_6_lesson_4.wav");

        Chapter lvl2ch5 = new Chapter().setNumber(1).
                                        setTitle("jobAid/nav_0005_chapter_01_title.wav").
                                        setMenu("jobAid/nav_0006_chapter_01_lesson_select.wav").
                                        addLesson(lvl2ch5lsn1).
                                        addLesson(lvl2ch5lsn2).
                                        addLesson(lvl2ch5lsn3).
                                        addLesson(lvl2ch5lsn4);

        Chapter lvl2ch6 = new Chapter().setNumber(2).
                                        setTitle("jobAid/nav_0007_chapter_02_title.wav").
                                        setMenu("jobAid/nav_0008_chapter_02_lesson_select.wav").
                                        addLesson(lvl2ch6lsn1).
                                        addLesson(lvl2ch6lsn2).
                                        addLesson(lvl2ch6lsn3).
                                        addLesson(lvl2ch6lsn4);

        Level lvl1 = new Level(1, "jobAid/level_1_menu.wav").setIntroduction("jobAid/level_1_intro.wav").
                        addChapter(lvl1ch1).
                        addChapter(lvl1ch2);

        Level lvl2 = new Level(2, "jobAid/nav_0004_chapter_select.wav").setIntroduction("jobAid/nav_0003_level_content.wav").
                        addChapter(lvl2ch5).
                        addChapter(lvl2ch6);
        
        JobAidCourse course = new JobAidCourse("JobAidCourse", "jobAid/nav_0001_welcome.wav", "jobAid/nav_0002_level_select.wav").
                                    addLevel(lvl1).
                                    addLevel(lvl2);

        jobAidContentService.addCourse(course);
    }
}
