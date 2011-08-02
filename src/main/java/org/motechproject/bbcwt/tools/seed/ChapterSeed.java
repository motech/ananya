package org.motechproject.bbcwt.tools.seed;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChapterSeed extends Seed {
    @Autowired
    private ChaptersRespository chapters;


    @Override
    protected void load() {
        Chapter chapter1 = new Chapter(1);
        Lesson lesson1Ch1 = new Lesson(1, "You are hearing to lesson 1 in chapter 1.");
        Lesson lesson2Ch1 = new Lesson(2, "You are hearing to lesson 2 in chapter 1.");
        Lesson lesson3Ch1 = new Lesson(3, "You are hearing to lesson 3 in chapter 1.");
        Lesson lesson4Ch1 = new Lesson(4, "You are hearing to lesson 4 in chapter 1.");

        chapter1.addLesson(lesson1Ch1);
        chapter1.addLesson(lesson2Ch1);
        chapter1.addLesson(lesson3Ch1);
        chapter1.addLesson(lesson4Ch1);

        chapters.add(chapter1);

        Chapter chapter2 = new Chapter(2);
        Lesson lesson1Ch2 = new Lesson(1, "You are hearing to lesson 1 in chapter 2.");
        Lesson lesson2Ch2 = new Lesson(2, "You are hearing to lesson 2 in chapter 2.");

        chapter2.addLesson(lesson1Ch2);
        chapter2.addLesson(lesson2Ch2);

        chapters.add(chapter2);
    }
}