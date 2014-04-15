package org.motechproject.ananya.seed.domain;

import org.motechproject.ananya.domain.Node;
import org.motechproject.cmslite.api.model.StringContent;

public class CertificateCourseTree {

    public static final int NUMBER_OF_LESSONS_IN_A_CHAPTER = 4;
    public static final int NUMBER_OF_QUESTIONS_IN_A_CHAPTER = 4;
    public static final int NUMBER_OF_CHAPTERS_IN_COURSE = 9;

    private CertificateCourseContent courseContent = new CertificateCourseContent();

    public Node build() {
        Node courseNode = new Node("CertificationCourse");
        courseNode.addData("type", "course");
        for (int i = 1; i <= NUMBER_OF_CHAPTERS_IN_COURSE; i++)
            courseNode.addChild(createNodeForChapter(i));
        return courseNode;
    }

    private Node createNodeForChapter(int number) {
        Node chapterNode = new Node("Chapter " + number);
        chapterNode.addData("type", "chapter");

        String endOptionName = String.format("ch%d_end_op", number);
        StringContent chapterMenu = new StringContent("hindi", "menu", courseContent.getFor(endOptionName).fileName);

        String quizHeaderName = String.format("ch%d_qp", number);
        StringContent quizHeader = new StringContent("hindi", "quizHeader", courseContent.getFor(quizHeaderName).fileName);

        chapterNode.addContent(chapterMenu);
        chapterNode.addContent(quizHeader);

        for (int score = 0; score <= NUMBER_OF_QUESTIONS_IN_A_CHAPTER; score++) {
            String scoreName = String.format("ch%d_%d_ca.wav", number, score);
            final String scoreKey = String.format("score %d", score);

            StringContent scoreReport = new StringContent("hindi", scoreKey, courseContent.getFor(scoreName).fileName);
            chapterNode.addContent(scoreReport);
        }
        for (int i = 1; i <= NUMBER_OF_LESSONS_IN_A_CHAPTER; i++)
            chapterNode.addChild(createNodeForLesson(number, i));
        for (int i = 1; i <= NUMBER_OF_QUESTIONS_IN_A_CHAPTER; i++)
            chapterNode.addChild(createNodeForQuestion(number, i));

        return chapterNode;
    }

    private Node createNodeForLesson(int chapterNumber, int lessonNumber) {
        Node lessonNode = new Node("Chapter " + chapterNumber + " Lesson " + lessonNumber);
        lessonNode.addData("type", "lesson");

        String lessonName = String.format("ch%d_l%d.wav", chapterNumber, lessonNumber);
        StringContent lesson = new StringContent("hindi", "lesson", courseContent.getFor(lessonName).fileName);
        String lessonMenuName = String.format("ch%d_l%d_op.wav", chapterNumber, lessonNumber);
        StringContent lessonMenu = new StringContent("hindi", "menu", courseContent.getFor(lessonMenuName).fileName);

        lessonNode.addContent(lesson);
        lessonNode.addContent(lessonMenu);

        return lessonNode;
    }

    private Node createNodeForQuestion(int chapterNumber, int questionNumber) {
        Node questionNode = new Node("Chapter " + chapterNumber + " Question " + questionNumber);
        questionNode.addData("type", "quiz");

        String questionKey = String.format("Chapter %d, Question %d", chapterNumber, questionNumber);
        questionNode.addData("correctAnswer", courseContent.getCorrectAnswerFor(questionKey));

        String questionName = String.format("ch%d_q%d.wav", chapterNumber, questionNumber);
        StringContent question = new StringContent("hindi", "question", courseContent.getFor(questionName).fileName);

        String correctAnswerPromptName = String.format("ch%d_q%d_ca.wav", chapterNumber, questionNumber);
        StringContent correctAnswerPrompt = new StringContent("hindi", "correct", courseContent.getFor(correctAnswerPromptName).fileName);

        String wrongAnswerPromptName = String.format("ch%d_q%d_wa.wav", chapterNumber, questionNumber);
        StringContent wrongAnswerPrompt = new StringContent("hindi", "incorrect", courseContent.getFor(wrongAnswerPromptName).fileName);

        questionNode.addContent(question);
        questionNode.addContent(correctAnswerPrompt);
        questionNode.addContent(wrongAnswerPrompt);

        return questionNode;
    }

}
