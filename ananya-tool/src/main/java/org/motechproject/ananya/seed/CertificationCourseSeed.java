package org.motechproject.ananya.seed;

import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CertificationCourseSeed {

    public static final int NUMBER_OF_LESSONS_IN_A_CHAPTER = 4;
    public static final int NUMBER_OF_QUESTIONS_IN_A_CHAPTER = 4;
    public static final int NUMBER_OF_CHAPTERS_IN_COURSE = 9;

    @Autowired
    private AllNodes allNodes;
    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;

    @Seed(priority = 0, version = "1.0")
    public void loadSeed() {
        Node certificateCourse = createNodeForCourse();
        allNodes.addNodeWithDescendants(certificateCourse);
        recursivelySaveContentsInPostgres(certificateCourse);
    }

    private void recursivelySaveContentsInPostgres(Node node) {
        CourseItemType type = CourseItemType.valueOf(node.data().get("type").toUpperCase());
        CourseItemDimension courseItemDimension = new CourseItemDimension(node.getName(), node.getId(), type, null);
        List<Node> children = node.children();
        allCourseItemDimensions.add(courseItemDimension);
        if (children.isEmpty()) return;

        if (type.equals(CourseItemType.CHAPTER)) {
            CourseItemDimension quizCourseItemDimension = new CourseItemDimension(node.getName(), node.getId(), CourseItemType.QUIZ, null);
            allCourseItemDimensions.add(quizCourseItemDimension);
        }

        for (Node child : children) {
            recursivelySaveContentsInPostgres(child);
        }
    }

    private Node createNodeForCourse() {
        Node courseNode = new Node("CertificationCourse");
        courseNode.addData("type", "course");
        for (int i = 1; i <= NUMBER_OF_CHAPTERS_IN_COURSE; i++) {
            courseNode.addChild(createNodeForChapter(i));
        }
        return courseNode;
    }

    private Node createNodeForChapter(int number) {
        Node chapterNode = new Node("Chapter " + number);
        chapterNode.addData("type", "chapter");

        String endOptionName = String.format("ch%d_end_op", number);
        StringContent chapterMenu = new StringContent("hindi", "menu", findAudioContentContainingFilename(endOptionName).fileName);

        String quizHeaderName = String.format("ch%d_qp", number);
        StringContent quizHeader = new StringContent("hindi", "quizHeader", findAudioContentContainingFilename(quizHeaderName).fileName);

        chapterNode.addContent(chapterMenu);
        chapterNode.addContent(quizHeader);

        for (int score = 0; score <= NUMBER_OF_QUESTIONS_IN_A_CHAPTER; score++) {
            String scoreName = String.format("ch%d_%d_ca.wav", number, score);
            final String scoreKey = String.format("score %d", score);

            StringContent scoreReport = new StringContent("hindi", scoreKey, findAudioContentContainingFilename(scoreName).fileName);
            chapterNode.addContent(scoreReport);
        }

        for (int i = 1; i <= NUMBER_OF_LESSONS_IN_A_CHAPTER; i++) {
            chapterNode.addChild(createNodeForLesson(number, i));
        }

        for (int i = 1; i <= NUMBER_OF_QUESTIONS_IN_A_CHAPTER; i++) {
            chapterNode.addChild(createNodeForQuestion(number, i));
        }

        return chapterNode;
    }

    private Node createNodeForLesson(int chapterNumber, int lessonNumber) {
        Node lessonNode = new Node("Chapter " + chapterNumber + " Lesson " + lessonNumber);
        lessonNode.addData("type", "lesson");

        String lessonName = String.format("ch%d_l%d.wav", chapterNumber, lessonNumber);
        StringContent lesson = new StringContent("hindi", "lesson", findAudioContentContainingFilename(lessonName).fileName);
        String lessonMenuName = String.format("ch%d_l%d_op.wav", chapterNumber, lessonNumber);
        StringContent lessonMenu = new StringContent("hindi", "menu", findAudioContentContainingFilename(lessonMenuName).fileName);

        lessonNode.addContent(lesson);
        lessonNode.addContent(lessonMenu);

        return lessonNode;
    }

    private Node createNodeForQuestion(int chapterNumber, int questionNumber) {
        Node questionNode = new Node("Chapter " + chapterNumber + " Question " + questionNumber);
        questionNode.addData("type", "quiz");

        String questionKey = String.format("Chapter %d, Question %d", chapterNumber, questionNumber);
        questionNode.addData("correctAnswer", CORRECT_ANSWERS.get(questionKey));

        String questionName = String.format("ch%d_q%d.wav", chapterNumber, questionNumber);
        StringContent question = new StringContent("hindi", "question", findAudioContentContainingFilename(questionName).fileName);

        String correctAnswerPromptName = String.format("ch%d_q%d_ca.wav", chapterNumber, questionNumber);
        StringContent correctAnswerPrompt = new StringContent("hindi", "correct", findAudioContentContainingFilename(correctAnswerPromptName).fileName);

        String wrongAnswerPromptName = String.format("ch%d_q%d_wa.wav", chapterNumber, questionNumber);
        StringContent wrongAnswerPrompt = new StringContent("hindi", "incorrect", findAudioContentContainingFilename(wrongAnswerPromptName).fileName);

        questionNode.addContent(question);
        questionNode.addContent(correctAnswerPrompt);
        questionNode.addContent(wrongAnswerPrompt);

        return questionNode;
    }

    private static AudioContent findAudioContentContainingFilename(String endOptionName) {
        for (AudioContent audioContent : AudioContentList.ALL_AUDIOS) {
            if (audioContent.fileName.contains(endOptionName)) {
                return audioContent;
            }
        }
        throw new RuntimeException("Could not find any file with the name: " + endOptionName);
    }

    private static final Map<String, String> CORRECT_ANSWERS = new HashMap<String, String>();

    static {
        CORRECT_ANSWERS.put("Chapter 1, Question 1", "1");
        CORRECT_ANSWERS.put("Chapter 1, Question 2", "2");
        CORRECT_ANSWERS.put("Chapter 1, Question 3", "1");
        CORRECT_ANSWERS.put("Chapter 1, Question 4", "2");

        CORRECT_ANSWERS.put("Chapter 2, Question 1", "1");
        CORRECT_ANSWERS.put("Chapter 2, Question 2", "2");
        CORRECT_ANSWERS.put("Chapter 2, Question 3", "1");
        CORRECT_ANSWERS.put("Chapter 2, Question 4", "2");

        CORRECT_ANSWERS.put("Chapter 3, Question 1", "1");
        CORRECT_ANSWERS.put("Chapter 3, Question 2", "2");
        CORRECT_ANSWERS.put("Chapter 3, Question 3", "1");
        CORRECT_ANSWERS.put("Chapter 3, Question 4", "2");

        CORRECT_ANSWERS.put("Chapter 4, Question 1", "1");
        CORRECT_ANSWERS.put("Chapter 4, Question 2", "2");
        CORRECT_ANSWERS.put("Chapter 4, Question 3", "1");
        CORRECT_ANSWERS.put("Chapter 4, Question 4", "2");

        CORRECT_ANSWERS.put("Chapter 5, Question 1", "1");
        CORRECT_ANSWERS.put("Chapter 5, Question 2", "2");
        CORRECT_ANSWERS.put("Chapter 5, Question 3", "1");
        CORRECT_ANSWERS.put("Chapter 5, Question 4", "2");

        CORRECT_ANSWERS.put("Chapter 6, Question 1", "1");
        CORRECT_ANSWERS.put("Chapter 6, Question 2", "2");
        CORRECT_ANSWERS.put("Chapter 6, Question 3", "1");
        CORRECT_ANSWERS.put("Chapter 6, Question 4", "2");

        CORRECT_ANSWERS.put("Chapter 7, Question 1", "1");
        CORRECT_ANSWERS.put("Chapter 7, Question 2", "2");
        CORRECT_ANSWERS.put("Chapter 7, Question 3", "1");
        CORRECT_ANSWERS.put("Chapter 7, Question 4", "2");

        CORRECT_ANSWERS.put("Chapter 8, Question 1", "1");
        CORRECT_ANSWERS.put("Chapter 8, Question 2", "2");
        CORRECT_ANSWERS.put("Chapter 8, Question 3", "1");
        CORRECT_ANSWERS.put("Chapter 8, Question 4", "2");

        CORRECT_ANSWERS.put("Chapter 9, Question 1", "1");
        CORRECT_ANSWERS.put("Chapter 9, Question 2", "2");
        CORRECT_ANSWERS.put("Chapter 9, Question 3", "1");
        CORRECT_ANSWERS.put("Chapter 9, Question 4", "2");
    }
}