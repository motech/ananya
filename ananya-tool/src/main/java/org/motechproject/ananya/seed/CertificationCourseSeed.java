package org.motechproject.ananya.seed;

import org.motechproject.ananya.action.CourseItemActions;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.motechproject.ananya.seed.CertificateCourseDetails.getCorrectAnswerFor;

@Component
public class CertificationCourseSeed {


    @Autowired
    private AllNodes allNodes;
    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;
    @Autowired
    private AllStringContents allStringContents;

    @Seed(priority = 0, version = "1.0")
    public void loadSeed() {
        Node certificateCourse = createNodeForCourse();
        allNodes.addNodeWithDescendants(certificateCourse);
        recursivelySaveContentsInPostgres(certificateCourse);
    }

    @Seed(priority = 3, version = "1.1")
    public void loadAudioContentDetails() {
        Node certificateCourse = allNodes.findByName("CertificationCourse");
        recursivelySaveAudioContentAndParentDetails(certificateCourse, null);
    }

    private void recursivelySaveAudioContentAndParentDetails(Node node, CourseItemDimension parentDimension) {
        CourseItemType type = CourseItemType.valueOf(node.data().get("type").toUpperCase());
        CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(node.getName(), type);
        courseItemDimension.setParentDimension(parentDimension);
        allCourseItemDimensions.update(courseItemDimension);

        if (type.equals(CourseItemType.CHAPTER)) {
            courseItemDimension = allCourseItemDimensions.getFor(node.getName(), CourseItemType.QUIZ);
            courseItemDimension.setParentDimension(parentDimension);
            allCourseItemDimensions.update(courseItemDimension);
        }

        CourseItemActions.init(allNodes, allStringContents);
        CourseItemActions courseItemAction = CourseItemActions.findFor(type);
        node = courseItemAction.updateContents(node);

        /*
        * Since Certificate Course structure has no specific node for QUIZ, the chapter level node is to denote the
        * quiz. This places a unique constraint requirement on the combination of (name, type).
        * While adding audio files, this creates a problem since audio files typically have similar names such as
        * "lesson", "introduction", "menu" etc. To overcome this issue, we concatenate the name with the filename.
        *      eg., audio -> "lesson", "0024_ch1_4_ca.wav"
        *           name -> "lesson_0024_ch1_4_ca.wav"
        */
        for (StringContent content : node.contents()) {
            CourseItemDimension audioContentDimension = new CourseItemDimension(
                    content.getName() + ":" + content.getValue(),
                    content.getId(),
                    CourseItemType.AUDIO,
                    courseItemDimension,
                    content.getValue(),
                    Integer.valueOf(content.getMetadata() == null ? "0" : content.getMetadata().get("duration"))
            );
            allCourseItemDimensions.add(audioContentDimension);
        }

        List<Node> children = node.children();
        if (children.isEmpty()) return;
        for (Node child : children) {
            recursivelySaveAudioContentAndParentDetails(child, courseItemDimension);
        }
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
        for (int i = 1; i <= CertificateCourseDetails.NUMBER_OF_CHAPTERS_IN_COURSE; i++) {
            courseNode.addChild(createNodeForChapter(i));
        }
        return courseNode;
    }

    private Node createNodeForChapter(int number) {
        Node chapterNode = new Node("Chapter " + number);
        chapterNode.addData("type", "chapter");

        String endOptionName = String.format("ch%d_end_op", number);
        StringContent chapterMenu = new StringContent("hindi", "menu", CertificateCourseDetails.findAudioContentContainingFilename(endOptionName).fileName);

        String quizHeaderName = String.format("ch%d_qp", number);
        StringContent quizHeader = new StringContent("hindi", "quizHeader", CertificateCourseDetails.findAudioContentContainingFilename(quizHeaderName).fileName);

        chapterNode.addContent(chapterMenu);
        chapterNode.addContent(quizHeader);

        for (int score = 0; score <= CertificateCourseDetails.NUMBER_OF_QUESTIONS_IN_A_CHAPTER; score++) {
            String scoreName = String.format("ch%d_%d_ca.wav", number, score);
            final String scoreKey = String.format("score %d", score);

            StringContent scoreReport = new StringContent("hindi", scoreKey, CertificateCourseDetails.findAudioContentContainingFilename(scoreName).fileName);
            chapterNode.addContent(scoreReport);
        }

        for (int i = 1; i <= CertificateCourseDetails.NUMBER_OF_LESSONS_IN_A_CHAPTER; i++) {
            chapterNode.addChild(createNodeForLesson(number, i));
        }

        for (int i = 1; i <= CertificateCourseDetails.NUMBER_OF_QUESTIONS_IN_A_CHAPTER; i++) {
            chapterNode.addChild(createNodeForQuestion(number, i));
        }

        return chapterNode;
    }

    private Node createNodeForLesson(int chapterNumber, int lessonNumber) {
        Node lessonNode = new Node("Chapter " + chapterNumber + " Lesson " + lessonNumber);
        lessonNode.addData("type", "lesson");

        String lessonName = String.format("ch%d_l%d.wav", chapterNumber, lessonNumber);
        StringContent lesson = new StringContent("hindi", "lesson", CertificateCourseDetails.findAudioContentContainingFilename(lessonName).fileName);
        String lessonMenuName = String.format("ch%d_l%d_op.wav", chapterNumber, lessonNumber);
        StringContent lessonMenu = new StringContent("hindi", "menu", CertificateCourseDetails.findAudioContentContainingFilename(lessonMenuName).fileName);

        lessonNode.addContent(lesson);
        lessonNode.addContent(lessonMenu);

        return lessonNode;
    }

    private Node createNodeForQuestion(int chapterNumber, int questionNumber) {
        Node questionNode = new Node("Chapter " + chapterNumber + " Question " + questionNumber);
        questionNode.addData("type", "quiz");

        String questionKey = String.format("Chapter %d, Question %d", chapterNumber, questionNumber);
        questionNode.addData("correctAnswer", getCorrectAnswerFor(questionKey));

        String questionName = String.format("ch%d_q%d.wav", chapterNumber, questionNumber);
        StringContent question = new StringContent("hindi", "question", CertificateCourseDetails.findAudioContentContainingFilename(questionName).fileName);

        String correctAnswerPromptName = String.format("ch%d_q%d_ca.wav", chapterNumber, questionNumber);
        StringContent correctAnswerPrompt = new StringContent("hindi", "correct", CertificateCourseDetails.findAudioContentContainingFilename(correctAnswerPromptName).fileName);

        String wrongAnswerPromptName = String.format("ch%d_q%d_wa.wav", chapterNumber, questionNumber);
        StringContent wrongAnswerPrompt = new StringContent("hindi", "incorrect", CertificateCourseDetails.findAudioContentContainingFilename(wrongAnswerPromptName).fileName);

        questionNode.addContent(question);
        questionNode.addContent(correctAnswerPrompt);
        questionNode.addContent(wrongAnswerPrompt);

        return questionNode;
    }
}