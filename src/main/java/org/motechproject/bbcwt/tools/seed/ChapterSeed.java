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

        Lesson lesson1Ch1 = new Lesson(1, "Lesson 1: Malaria is a very common disease, caused by mosquito bites.");
        Lesson lesson2Ch1 = new Lesson(2, "Lesson 2: Mosquitoes which causes malaria primarily reside in stagnant water.");
        Lesson lesson3Ch1 = new Lesson(3, "Lesson 3: In malaria patient first experiences a chill, followed by a very high temperature");
        Lesson lesson4Ch1 = new Lesson(4, "Lesson 4: Malaria patients should be immediately given medical care, a dose of quinine should be recommended to the patient.");

        chapter1.addLesson(lesson1Ch1);
        chapter1.addLesson(lesson2Ch1);
        chapter1.addLesson(lesson3Ch1);
        chapter1.addLesson(lesson4Ch1);

        final String commonOptionsLocation = "Press 1 if your answer is yes, 2 if the answer is no.";

        Question question1 = new Question(1, "Question 1: Is malaria caused by mosquito bites?", commonOptionsLocation, 1, "You answer is correct.", "Incorrect answer.Malaria is caused by bite of a female Anopheles mosquito.");
        Question question2 = new Question(2, "Question 2: Do malaria causing mosquitoes lay eggs in water?", commonOptionsLocation, 1, "You answer is correct.", "Incorrect answer.Stagnant water is breeding ground for mosquitoes.");
        Question question3 = new Question(3, "Question 3: Should Malaria patient be administered Quinine?", commonOptionsLocation, 1, "You answer is correct.", "Incorrect answer.Malaria patients should be given quinine.");

        chapter1.addQuestion(question1);
        chapter1.addQuestion(question2);
        chapter1.addQuestion(question3);

        chapters.add(chapter1);

        Chapter chapter2 = new Chapter(2);
        Lesson lesson1Ch2 = new Lesson(1, "You are hearing to lesson 1 in chapter 2.Here we will discuss about prevention mechanism against malaria. Keep your surroundings clean and dry so that we can prevent mosquito breeding.");
        Lesson lesson2Ch2 = new Lesson(2, "You are hearing to lesson 2 in chapter 2.Never allow water to stagnate,clean your water coolers regularly. Use mosquito nets and repellents before going to sleep.");

        chapter2.addLesson(lesson1Ch2);
        chapter2.addLesson(lesson2Ch2);

        chapters.add(chapter2);
    }
}