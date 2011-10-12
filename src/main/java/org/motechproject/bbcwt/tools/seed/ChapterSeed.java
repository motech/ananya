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

        Lesson lesson1Ch1 = new Lesson(1, "0004_chapter_1_lesson_1.wav", "0005_chapter_1_lesson_1_option_prompt.wav");
        Lesson lesson2Ch1 = new Lesson(2, "0006_chapter_1_lesson_2.wav", "0007_chapter_1_lesson_2_option_prompt.wav");
        Lesson lesson3Ch1 = new Lesson(3, "0008_chapter_1_lesson_3.wav", "0009_chapter_1_lesson_3_option_prompt.wav");
        Lesson lesson4Ch1 = new Lesson(4, "0010_chapter_1_lesson_4.wav", "0011_chapter_1_lesson_4_option_prompt.wav");

        chapter1.addLesson(lesson1Ch1);
        chapter1.addLesson(lesson2Ch1);
        chapter1.addLesson(lesson3Ch1);
        chapter1.addLesson(lesson4Ch1);

        final String commonOptionsLocation = "Press 1 if your answer is yes, 2 if the answer is no.";

        Question question1 = new Question(1, "0012_b_chapter_1_q_1.wav", 1, "0013_chapter_1_quiz_q_1_correct_answer.wav", "0014_chapter_1_quiz_q_1_wrong_answer.wav");
        Question question2 = new Question(2, "0015_chapter_1_quiz_q_2.wav", 2, "0016_chapter_1_quiz_q_2_correct_answer.wav", "0017_chapter_1_quiz_q_2_wrong_answer.wav");
        Question question3 = new Question(3, "0018_chapter_1_quiz_q_3.wav", 1, "0019_chapter_1_quiz_q_3_correct_answer.wav", "0020_chapter_1_quiz_q_3_wrong_answer.wav");
        Question question4 = new Question(4, "0021_chapter_1_quiz_q_4.wav", 2, "0022_chapter_1-quiz_q_4_correct_answer.wav", "0023_chapter_1_quiz_q_4_wrong_answer.wav");

        chapter1.addQuestion(question1);
        chapter1.addQuestion(question2);
        chapter1.addQuestion(question3);
        chapter1.addQuestion(question4);

        chapter1.setGoodScoreSummary("Badhai ho acha khele aap. Pahila hissa Parivaar niyojan zaroori hai yaad rakhe.");
        chapter1.setBelowParScoreSummary("Pahila hissa Parivaar niyojan zaroori hai hamesha yaad rakhe.");

        chapter1.setCourseSummaryPrompt("0034_ch_5_summary.wav");
        chapter1.setCertificateAndCourseSummaryPrompt("0035_ch_5_mini_certificate_and_summary.wav");

        chapters.add(chapter1);

        Chapter chapter2 = new Chapter(2);
        Lesson lesson1Ch2 = new Lesson(1, "0030_chapter_2_lesson_1.wav", "0031_chapter_2_lesson_1_option_prompt.wav");

        chapter2.addLesson(lesson1Ch2);

        chapters.add(chapter2);
    }
}