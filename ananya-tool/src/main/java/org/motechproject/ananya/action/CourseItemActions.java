package org.motechproject.ananya.action;

import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.seed.AudioContent;
import org.motechproject.ananya.seed.CertificateCourseDetails;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStringContents;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ananya.seed.CertificateCourseDetails.findAudioContentContainingFilename;
import static org.motechproject.ananya.seed.CertificateCourseDetails.getCorrectAnswerFor;

public enum CourseItemActions {
    UpdateChapterAudioContent(CourseItemType.CHAPTER) {
        @Override
        public Node updateContents(Node node) {
            node.deleteAllContents();
            int number = getNumber(node.getName(), CourseItemType.CHAPTER);

            addAudioContentsToNode(node, number);
            saveStringContents(node);

            return node;
        }

        private void addAudioContentsToNode(Node node, int number) {
            AudioContent audioContent;
            String endOptionName = String.format("ch%d_end_op", number);
            audioContent = findAudioContentContainingFilename(endOptionName);
            StringContent chapterMenu = new StringContent("hindi", "menu", audioContent.fileName, getMetaData(audioContent.duration));

            String quizHeaderName = String.format("ch%d_qp", number);
            audioContent = findAudioContentContainingFilename(quizHeaderName);
            StringContent quizHeader = new StringContent("hindi", "quizHeader", audioContent.fileName, getMetaData(audioContent.duration));

            node.addContent(chapterMenu);
            node.addContent(quizHeader);

            for (int score = 0; score <= CertificateCourseDetails.NUMBER_OF_QUESTIONS_IN_A_CHAPTER; score++) {
                String scoreName = String.format("ch%d_%d_ca.wav", number, score);
                final String scoreKey = String.format("score %d", score);
                audioContent = findAudioContentContainingFilename(scoreName);
                StringContent scoreReport = new StringContent("hindi", scoreKey, audioContent.fileName, getMetaData(audioContent.duration));
                node.addContent(scoreReport);
            }
        }
    },

    UpdateLessonAudioContent(CourseItemType.LESSON) {
        @Override
        public Node updateContents(Node node) {
            node.deleteAllContents();
            int chapterNumber = getNumber(node.getName(), CourseItemType.CHAPTER);
            int lessonNumber = getNumber(node.getName(), CourseItemType.LESSON);

            addAudioContentsToNode(node, chapterNumber, lessonNumber);
            saveStringContents(node);

            return node;
        }

        private void addAudioContentsToNode(Node node, int chapterNumber, int lessonNumber) {
            AudioContent audioContent;
            String lessonName = String.format("ch%d_l%d.wav", chapterNumber, lessonNumber);
            audioContent = CertificateCourseDetails.findAudioContentContainingFilename(lessonName);
            StringContent lesson = new StringContent("hindi", "lesson", audioContent.fileName, getMetaData(audioContent.duration));
            String lessonMenuName = String.format("ch%d_l%d_op.wav", chapterNumber, lessonNumber);
            audioContent = CertificateCourseDetails.findAudioContentContainingFilename(lessonMenuName);
            StringContent lessonMenu = new StringContent("hindi", "menu", audioContent.fileName, getMetaData(audioContent.duration));

            node.addContent(lesson);
            node.addContent(lessonMenu);
        }
    },

    UpdateQuestionAudioContent(CourseItemType.QUIZ) {
        @Override
        public Node updateContents(Node node) {
            node.deleteAllContents();
            int chapterNumber = getNumber(node.getName(), CourseItemType.CHAPTER);
            int questionNumber = getNumber(node.getName(), CourseItemType.LESSON);

            addAudioContentsToNode(node, chapterNumber, questionNumber);
            saveStringContents(node);

            return node;
        }

        private void addAudioContentsToNode(Node node, int chapterNumber, int questionNumber) {
            AudioContent audioContent;
            String questionKey = String.format("Chapter %d, Question %d", chapterNumber, questionNumber);
            node.addData("correctAnswer", getCorrectAnswerFor(questionKey));

            String questionName = String.format("ch%d_q%d.wav", chapterNumber, questionNumber);
            audioContent = CertificateCourseDetails.findAudioContentContainingFilename(questionName);
            StringContent question = new StringContent("hindi", "question", audioContent.fileName, getMetaData(audioContent.duration));

            String correctAnswerPromptName = String.format("ch%d_q%d_ca.wav", chapterNumber, questionNumber);
            audioContent = CertificateCourseDetails.findAudioContentContainingFilename(correctAnswerPromptName);
            StringContent correctAnswerPrompt = new StringContent("hindi", "correct", audioContent.fileName, getMetaData(audioContent.duration));

            String wrongAnswerPromptName = String.format("ch%d_q%d_wa.wav", chapterNumber, questionNumber);
            audioContent = CertificateCourseDetails.findAudioContentContainingFilename(wrongAnswerPromptName);
            StringContent wrongAnswerPrompt = new StringContent("hindi", "incorrect", audioContent.fileName, getMetaData(audioContent.duration));

            node.addContent(question);
            node.addContent(correctAnswerPrompt);
            node.addContent(wrongAnswerPrompt);
        }
    },
    Default(null) {
        @Override
        public Node updateContents(Node node) {
            return node;
        }
    };

    private static AllNodes allNodes;


    private static AllStringContents allStringContents;
    private CourseItemType courseItemType;
    CourseItemActions(CourseItemType courseItemType) {
        this.courseItemType = courseItemType;
    }

    public abstract Node updateContents(Node node);

    public static void init(AllNodes allNodes, AllStringContents allStringContents) {
        CourseItemActions.allNodes = allNodes;

        CourseItemActions.allStringContents = allStringContents;
    }

    public static CourseItemActions findFor(CourseItemType courseItemType) {
        for (CourseItemActions courseItemActions : CourseItemActions.values()) {
            if (courseItemType.equals(courseItemActions.courseItemType))
                return courseItemActions;
        }
        return CourseItemActions.Default;
    }

    private static void saveStringContents(Node node) {
        for (StringContent stringContentToSave : node.contents()) {
            allStringContents.add(stringContentToSave);
            node.addContentId(stringContentToSave.getId());
        }

        allNodes.update(node);
    }

    private static int getNumber(String name, CourseItemType chapter) {
        if (chapter.equals(CourseItemType.CHAPTER))
            return Integer.parseInt(name.split(" ")[1]);
        if (chapter.equals(CourseItemType.LESSON) || chapter.equals(CourseItemType.QUIZ))
            return Integer.parseInt(name.split(" ")[3]);
        return 0;
    }

    private static Map<String, String> getMetaData(Integer duration) {
        HashMap<String, String> metadata = new HashMap<String, String>();
        metadata.put("duration", duration.toString());
        return metadata;
    }
}
