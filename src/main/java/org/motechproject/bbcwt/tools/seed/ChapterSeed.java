package org.motechproject.bbcwt.tools.seed;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Question;
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

        Lesson lesson1Ch1 = new Lesson(1, "You are hearing to lesson 1 in chapter 1. This is just a placeholder for lesson 1 in chapter 1.");
        Lesson lesson2Ch1 = new Lesson(2, "You are hearing to lesson 2 in chapter 1. This is just a placeholder for lesson 2 in chapter 1.");
        Lesson lesson3Ch1 = new Lesson(3, "You are hearing to lesson 3 in chapter 1. This is just a placeholder for lesson 3 in chapter 1.");
        Lesson lesson4Ch1 = new Lesson(4, "You are hearing to lesson 4 in chapter 1. This is just a placeholder for lesson 4 in chapter 1.");

        chapter1.addLesson(lesson1Ch1);
        chapter1.addLesson(lesson2Ch1);
        chapter1.addLesson(lesson3Ch1);
        chapter1.addLesson(lesson4Ch1);

        Question question1 = new Question(1, "Is this the first question?", "Press 1 if your answer yes, 2 if no.", 1, "You answer is correct. This is indeed the first question.", "Incorrect answer. Since this is the first question, you should have answered yes.");
        Question question2 = new Question(2, "Is this the second question?", "Press 1 if your answer yes, 2 if no.", 1, "You answer is correct. This is the second question.", "Incorrect answer. Since this is the second question, you should have answered yes.");
        Question question3 = new Question(3, "Is this the third question?", "Press 1 if your answer yes, 2 if no.", 1, "You answer is correct. This is the third question.", "Incorrect answer. Since this is third question, you should have answered yes.");

        chapter1.addQuestion(question1);
        chapter1.addQuestion(question2);
        chapter1.addQuestion(question3);

        chapters.add(chapter1);

        Chapter chapter2 = new Chapter(2);
        Lesson lesson1Ch2 = new Lesson(1, "You are hearing to lesson 1 in chapter 2. This is just a placeholder for lesson 1 in chapter 2.");
        Lesson lesson2Ch2 = new Lesson(2, "You are hearing to lesson 2 in chapter 2. This is just a placeholder for lesson 2 in chapter 2.");

        chapter2.addLesson(lesson1Ch2);
        chapter2.addLesson(lesson2Ch2);

        chapters.add(chapter2);
    }
}