package org.motechproject.bbcwt.tools.seed;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Level;
import org.motechproject.bbcwt.service.JobAidContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobAidSeed extends Seed {
    @Autowired
    private JobAidContentService jobAidContentService;

    @Override
    protected void load() {
        Lesson lvl2ch5lsn1 = new Lesson().setNumber(1).setFileName("chapter_5_lesson_1.wav");
        Lesson lvl2ch5lsn2 = new Lesson().setNumber(2).setFileName("chapter_5_lesson_2.wav");
        Lesson lvl2ch5lsn3 = new Lesson().setNumber(3).setFileName("chapter_5_lesson_3.wav");
        Lesson lvl2ch5lsn4 = new Lesson().setNumber(4).setFileName("chapter_5_lesson_4.wav");

        Lesson lvl2ch6lsn1 = new Lesson().setNumber(1).setFileName("chapter_6_lesson_1.wav");
        Lesson lvl2ch6lsn2 = new Lesson().setNumber(2).setFileName("chapter_6_lesson_2.wav");
        Lesson lvl2ch6lsn3 = new Lesson().setNumber(3).setFileName("chapter_6_lesson_3.wav");
        Lesson lvl2ch6lsn4 = new Lesson().setNumber(4).setFileName("chapter_6_lesson_4.wav");

        Chapter lvl2ch5 = new Chapter().setNumber(1).
                                        setTitle("nav_0005_chapter_01_title.wav").
                                        setMenu("nav_0006_chapter_01_lesson_select.wav").
                                        addLesson(lvl2ch5lsn1).
                                        addLesson(lvl2ch5lsn2).
                                        addLesson(lvl2ch5lsn3).
                                        addLesson(lvl2ch5lsn4);

        Chapter lvl2ch6 = new Chapter().setNumber(2).
                                        setTitle("nav_0007_chapter_02_title.wav").
                                        setMenu("nav_0008_chapter_02_lesson_select.wav").
                                        addLesson(lvl2ch6lsn1).
                                        addLesson(lvl2ch6lsn2).
                                        addLesson(lvl2ch6lsn3).
                                        addLesson(lvl2ch6lsn4);

        Chapter lvl2ch7 = new Chapter().setNumber(3);

        Level lvl1 = new Level(1, null);
        Level lvl2 = new Level(2, "nav_0004_chapter_select.wav").setIntroduction("nav_0003_level_content.wav").
                        addChapter(lvl2ch5).
                        addChapter(lvl2ch6).
                        addChapter(lvl2ch7);
        Level lvl3 = new Level(3, null);

        JobAidCourse course = new JobAidCourse("JobAidCourse", "nav_0001_welcome.wav", "nav_0002_level_select.wav").
                                    addLevel(lvl1).
                                    addLevel(lvl2).
                                    addLevel(lvl3);

        jobAidContentService.addCourse(course);
    }
}