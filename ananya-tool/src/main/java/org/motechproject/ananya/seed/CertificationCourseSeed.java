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

import java.util.ArrayList;
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
        recursivelySaveContentsInPostgres(certificateCourse, null);
    }

    private void recursivelySaveContentsInPostgres(Node node, CourseItemDimension parentDimension) {
        CourseItemType type = CourseItemType.valueOf(node.data().get("type").toUpperCase());
        CourseItemDimension courseItemDimension = new CourseItemDimension(node.getName(), node.getId(), type, parentDimension);
        allCourseItemDimensions.add(courseItemDimension);

        if (type.equals(CourseItemType.CHAPTER)) {
            CourseItemDimension quizCourseItemDimension = new CourseItemDimension(
                    node.getName(), node.getId(), CourseItemType.QUIZ, parentDimension);
            allCourseItemDimensions.add(quizCourseItemDimension);
        }

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
            recursivelySaveContentsInPostgres(child, courseItemDimension);
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

        AudioContent audioContent;
        String endOptionName = String.format("ch%d_end_op", number);
        audioContent = findAudioContentContainingFilename(endOptionName);
        StringContent chapterMenu = new StringContent("hindi", "menu", audioContent.fileName, getMetaData(audioContent.duration));

        String quizHeaderName = String.format("ch%d_qp", number);
        audioContent = findAudioContentContainingFilename(quizHeaderName);
        StringContent quizHeader = new StringContent("hindi", "quizHeader", audioContent.fileName, getMetaData(audioContent.duration));

        chapterNode.addContent(chapterMenu);
        chapterNode.addContent(quizHeader);

        for (int score = 0; score <= NUMBER_OF_QUESTIONS_IN_A_CHAPTER; score++) {
            String scoreName = String.format("ch%d_%d_ca.wav", number, score);
            final String scoreKey = String.format("score %d", score);

            audioContent = findAudioContentContainingFilename(scoreName);
            StringContent scoreReport = new StringContent("hindi", scoreKey, audioContent.fileName, getMetaData(audioContent.duration));
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

        AudioContent audioContent;
        String lessonName = String.format("ch%d_l%d.wav", chapterNumber, lessonNumber);
        audioContent = findAudioContentContainingFilename(lessonName);
        StringContent lesson = new StringContent("hindi", "lesson", audioContent.fileName, getMetaData(audioContent.duration));
        String lessonMenuName = String.format("ch%d_l%d_op.wav", chapterNumber, lessonNumber);
        audioContent = findAudioContentContainingFilename(lessonMenuName);
        StringContent lessonMenu = new StringContent("hindi", "menu", audioContent.fileName, getMetaData(audioContent.duration));

        lessonNode.addContent(lesson);
        lessonNode.addContent(lessonMenu);

        return lessonNode;
    }

    private Node createNodeForQuestion(int chapterNumber, int questionNumber) {
        Node questionNode = new Node("Chapter " + chapterNumber + " Question " + questionNumber);
        questionNode.addData("type", "quiz");

        AudioContent audioContent;
        String questionKey = String.format("Chapter %d, Question %d", chapterNumber, questionNumber);
        questionNode.addData("correctAnswer", CORRECT_ANSWERS.get(questionKey));

        String questionName = String.format("ch%d_q%d.wav", chapterNumber, questionNumber);
        audioContent = findAudioContentContainingFilename(questionName);
        StringContent question = new StringContent("hindi", "question", audioContent.fileName, getMetaData(audioContent.duration));

        String correctAnswerPromptName = String.format("ch%d_q%d_ca.wav", chapterNumber, questionNumber);
        audioContent = findAudioContentContainingFilename(correctAnswerPromptName);
        StringContent correctAnswerPrompt = new StringContent("hindi", "correct", audioContent.fileName, getMetaData(audioContent.duration));

        String wrongAnswerPromptName = String.format("ch%d_q%d_wa.wav", chapterNumber, questionNumber);
        audioContent = findAudioContentContainingFilename(wrongAnswerPromptName);
        StringContent wrongAnswerPrompt = new StringContent("hindi", "incorrect", audioContent.fileName, getMetaData(audioContent.duration));

        questionNode.addContent(question);
        questionNode.addContent(correctAnswerPrompt);
        questionNode.addContent(wrongAnswerPrompt);

        return questionNode;
    }

    private static Map<String, String> getMetaData(Integer duration) {
        HashMap<String, String> metadata = new HashMap<String, String>();
        metadata.put("duration", duration.toString());
        return metadata;
    }

    private static AudioContent findAudioContentContainingFilename(String endOptionName) {
        for (AudioContent audioContent : ALL_AUDIOS) {
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

    private static class AudioContent {
        private String fileName;
        private Integer duration;

        private AudioContent(String fileName, Integer duration) {
            this.fileName = fileName;
            this.duration = duration;
        }
    }

    private static final List<AudioContent> ALL_AUDIOS = new ArrayList<AudioContent>();

    static {
        ALL_AUDIOS.add(new AudioContent("0000_b_dead_space_1pt5_sec.wav", 1502));
        ALL_AUDIOS.add(new AudioContent("0001_wp_ma_mohnish.wav", 125521));
        ALL_AUDIOS.add(new AudioContent("0002_start_course_op.wav", 24464));
        ALL_AUDIOS.add(new AudioContent("0002_wp_ama_ar.wav", 116090));
        ALL_AUDIOS.add(new AudioContent("0003_ch1_l1.wav", 197685));
        ALL_AUDIOS.add(new AudioContent("0003_ch1_l1_not_reg.wav", 192738));
        ALL_AUDIOS.add(new AudioContent("0004_ch1_l1_op.wav", 21935));
        ALL_AUDIOS.add(new AudioContent("0005_ch1_l2.wav", 191776));
        ALL_AUDIOS.add(new AudioContent("0005_register1.wav", 17252));
        ALL_AUDIOS.add(new AudioContent("0006_alreadyreg.wav", 19568));
        ALL_AUDIOS.add(new AudioContent("0006_ch1_l2_op.wav", 21190));
        ALL_AUDIOS.add(new AudioContent("0007_ch1_l3.wav", 160491));
        ALL_AUDIOS.add(new AudioContent("0007_notreg.wav", 17582));
        ALL_AUDIOS.add(new AudioContent("0008_ch1_l3_op.wav", 19914));
        ALL_AUDIOS.add(new AudioContent("0009_ch1_l4.wav", 156892));
        ALL_AUDIOS.add(new AudioContent("0010_ch1_l4_op.wav", 58201));
        ALL_AUDIOS.add(new AudioContent("0011_ch1_qp.wav", 72737));
        ALL_AUDIOS.add(new AudioContent("0012_ch1_q1.wav", 39079));
        ALL_AUDIOS.add(new AudioContent("0013_ch1_q1_ca.wav", 17906));
        ALL_AUDIOS.add(new AudioContent("0014_ch1_q1_wa.wav", 20596));
        ALL_AUDIOS.add(new AudioContent("0015_ch1_q2.wav", 35596));
        ALL_AUDIOS.add(new AudioContent("0016_ch1_q2_ca.wav", 23624));
        ALL_AUDIOS.add(new AudioContent("0017_ch1_q2_wa.wav", 21631));
        ALL_AUDIOS.add(new AudioContent("0018_ch1_q3.wav", 36360));
        ALL_AUDIOS.add(new AudioContent("0019_ch1_q3_ca.wav", 19012));
        ALL_AUDIOS.add(new AudioContent("0020_ch1_q3_wa.wav", 20951));
        ALL_AUDIOS.add(new AudioContent("0021_ch1_q4.wav", 43188));
        ALL_AUDIOS.add(new AudioContent("0022_ch1_q4_ca.wav", 22018));
        ALL_AUDIOS.add(new AudioContent("0023_ch1_q4_wa.wav", 20714));
        ALL_AUDIOS.add(new AudioContent("0024_ch1_4_ca.wav", 29437));
        ALL_AUDIOS.add(new AudioContent("0025_ch1_3_ca.wav", 28974));
        ALL_AUDIOS.add(new AudioContent("0026_ch1_2_ca.wav", 21375));
        ALL_AUDIOS.add(new AudioContent("0027_ch1_1_ca.wav", 23869));
        ALL_AUDIOS.add(new AudioContent("0028_ch1_0_ca.wav", 24819));
        ALL_AUDIOS.add(new AudioContent("0029_ch1_end_op.wav", 25399));
        ALL_AUDIOS.add(new AudioContent("0030_ch2_l1.wav", 220007));
        ALL_AUDIOS.add(new AudioContent("0031_ch2_l1_op.wav", 19992));
        ALL_AUDIOS.add(new AudioContent("0032_ch2_l2.wav", 189085));
        ALL_AUDIOS.add(new AudioContent("0033_ch2_l2_op.wav", 20893));
        ALL_AUDIOS.add(new AudioContent("0033_welcome_back_generic.wav", 9877));
        ALL_AUDIOS.add(new AudioContent("0034_ch2_l3.wav", 192135));
        ALL_AUDIOS.add(new AudioContent("0034_ch_5_summary.wav", 15681));
        ALL_AUDIOS.add(new AudioContent("0035_ch2_l3_op.wav", 19288));
        ALL_AUDIOS.add(new AudioContent("0035_ch_5_mini_certificate_and_summary.wav", 17367));
        ALL_AUDIOS.add(new AudioContent("0036_ch2_l4.wav", 178044));
        ALL_AUDIOS.add(new AudioContent("0037_ch2_l4_op.wav", 47654));
        ALL_AUDIOS.add(new AudioContent("0038_ch2_qp.wav", 16660));
        ALL_AUDIOS.add(new AudioContent("0039_ch2_q1.wav", 31222));
        ALL_AUDIOS.add(new AudioContent("0040_ch2_q1_ca.wav", 12995));
        ALL_AUDIOS.add(new AudioContent("0041_ch2_q1_wa.wav", 14627));
        ALL_AUDIOS.add(new AudioContent("0042_ch2_q2.wav", 55277));
        ALL_AUDIOS.add(new AudioContent("0043_ch2_q2_ca.wav", 22222));
        ALL_AUDIOS.add(new AudioContent("0044_ch2_q2_wa.wav", 20271));
        ALL_AUDIOS.add(new AudioContent("0045_ch2_q3.wav", 42057));
        ALL_AUDIOS.add(new AudioContent("0046_ch2_q3_ca.wav", 20601));
        ALL_AUDIOS.add(new AudioContent("0047_ch2_q3_wa.wav", 19625));
        ALL_AUDIOS.add(new AudioContent("0048_ch2_q4.wav", 43491));
        ALL_AUDIOS.add(new AudioContent("0049_ch2_q4_ca.wav", 20304));
        ALL_AUDIOS.add(new AudioContent("0050_ch2_q4_wa.wav", 19014));
        ALL_AUDIOS.add(new AudioContent("0051_ch2_4_ca.wav", 29741));
        ALL_AUDIOS.add(new AudioContent("0052_ch2_3_ca.wav", 28813));
        ALL_AUDIOS.add(new AudioContent("0053_ch2_2_ca.wav", 21255));
        ALL_AUDIOS.add(new AudioContent("0054_ch2_1_ca.wav", 25072));
        ALL_AUDIOS.add(new AudioContent("0055_ch2_0_ca.wav", 25247));
        ALL_AUDIOS.add(new AudioContent("0056_ch2_end_op.wav", 26144));
        ALL_AUDIOS.add(new AudioContent("0057_ch3_l1.wav", 221080));
        ALL_AUDIOS.add(new AudioContent("0058_ch3_l1_op.wav", 20324));
        ALL_AUDIOS.add(new AudioContent("0059_ch3_l2.wav", 193301));
        ALL_AUDIOS.add(new AudioContent("0060_ch3_l2_op.wav", 19201));
        ALL_AUDIOS.add(new AudioContent("0061_ch3_l3.wav", 175288));
        ALL_AUDIOS.add(new AudioContent("0062_ch3_l3_op.wav", 17841));
        ALL_AUDIOS.add(new AudioContent("0063_ch3_l4.wav", 176505));
        ALL_AUDIOS.add(new AudioContent("0064_ch3_l4_op.wav", 48744));
        ALL_AUDIOS.add(new AudioContent("0065_ch3_qp.wav", 16448));
        ALL_AUDIOS.add(new AudioContent("0066_ch3_q1.wav", 34394));
        ALL_AUDIOS.add(new AudioContent("0067_ch3_q1_ca.wav", 18549));
        ALL_AUDIOS.add(new AudioContent("0068_ch3_q1_wa.wav", 20761));
        ALL_AUDIOS.add(new AudioContent("0069_ch3_q2.wav", 38239));
        ALL_AUDIOS.add(new AudioContent("0070_ch3_q2_ca.wav", 23309));
        ALL_AUDIOS.add(new AudioContent("0071_ch3_q2_wa.wav", 20636));
        ALL_AUDIOS.add(new AudioContent("0072_ch3_q3.wav", 37874));
        ALL_AUDIOS.add(new AudioContent("0073_ch3_q3_ca.wav", 18430));
        ALL_AUDIOS.add(new AudioContent("0074_ch3_q3_wa.wav", 21308));
        ALL_AUDIOS.add(new AudioContent("0075_ch3_q4.wav", 34574));
        ALL_AUDIOS.add(new AudioContent("0076_ch3_q4_ca.wav", 22968));
        ALL_AUDIOS.add(new AudioContent("0077_ch3_q4_wa.wav", 20352));
        ALL_AUDIOS.add(new AudioContent("0078_ch3_4_ca.wav", 28535));
        ALL_AUDIOS.add(new AudioContent("0079_ch3_3_ca.wav", 28571));
        ALL_AUDIOS.add(new AudioContent("0080_ch3_2_ca.wav", 21260));
        ALL_AUDIOS.add(new AudioContent("0081_ch3_1_ca.wav", 23817));
        ALL_AUDIOS.add(new AudioContent("0082_ch3_0_ca.wav", 24785));
        ALL_AUDIOS.add(new AudioContent("0083_ch3_end_op.wav", 24853));
        ALL_AUDIOS.add(new AudioContent("0084_ch4_l1.wav", 232537));
        ALL_AUDIOS.add(new AudioContent("0085_ch4_l1_op.wav", 20321));
        ALL_AUDIOS.add(new AudioContent("0086_ch4_l2.wav", 183627));
        ALL_AUDIOS.add(new AudioContent("0087_ch4_l2_op.wav", 19141));
        ALL_AUDIOS.add(new AudioContent("0088_ch4_l3.wav", 181909));
        ALL_AUDIOS.add(new AudioContent("0089_ch4_l3_op.wav", 17751));
        ALL_AUDIOS.add(new AudioContent("0090_ch4_l4.wav", 165642));
        ALL_AUDIOS.add(new AudioContent("0091_ch4_l4_op.wav", 48262));
        ALL_AUDIOS.add(new AudioContent("0092_ch4_qp.wav", 16800));
        ALL_AUDIOS.add(new AudioContent("0093_ch4_q1.wav", 35006));
        ALL_AUDIOS.add(new AudioContent("0094_ch4_q1_ca.wav", 19572));
        ALL_AUDIOS.add(new AudioContent("0095_ch4_q1_wa.wav", 20879));
        ALL_AUDIOS.add(new AudioContent("0096_ch4_q2.wav", 45343));
        ALL_AUDIOS.add(new AudioContent("0097_ch4_q2_ca.wav", 23501));
        ALL_AUDIOS.add(new AudioContent("0098_ch4_q2_wa.wav", 21368));
        ALL_AUDIOS.add(new AudioContent("0099_ch4_q3.wav", 39408));
        ALL_AUDIOS.add(new AudioContent("0100_ch4_q3_ca.wav", 19825));
        ALL_AUDIOS.add(new AudioContent("0101_ch4_q3_wa.wav", 21947));
        ALL_AUDIOS.add(new AudioContent("0102_ch4_q4.wav", 34364));
        ALL_AUDIOS.add(new AudioContent("0103_ch4_q4_ca.wav", 20436));
        ALL_AUDIOS.add(new AudioContent("0104_ch4_q4_wa.wav", 18643));
        ALL_AUDIOS.add(new AudioContent("0105_ch4_4_ca.wav", 30513));
        ALL_AUDIOS.add(new AudioContent("0106_ch4_3_ca.wav", 30338));
        ALL_AUDIOS.add(new AudioContent("0107_ch4_2_ca.wav", 23234));
        ALL_AUDIOS.add(new AudioContent("0108_ch4_1_ca.wav", 24772));
        ALL_AUDIOS.add(new AudioContent("0109_ch4_0_ca.wav", 24957));
        ALL_AUDIOS.add(new AudioContent("0110_ch4_end_op.wav", 23961));
        ALL_AUDIOS.add(new AudioContent("0111_ch5_l1.wav", 233424));
        ALL_AUDIOS.add(new AudioContent("0112_ch5_l1_op.wav", 20194));
        ALL_AUDIOS.add(new AudioContent("0113_ch5_l2.wav", 200158));
        ALL_AUDIOS.add(new AudioContent("0114_ch5_l2_op.wav", 21624));
        ALL_AUDIOS.add(new AudioContent("0115_ch5_l3.wav", 185452));
        ALL_AUDIOS.add(new AudioContent("0116_ch5_l3_op.wav", 23171));
        ALL_AUDIOS.add(new AudioContent("0117_ch5_l4.wav", 185430));
        ALL_AUDIOS.add(new AudioContent("0118_ch5_l4_op.wav", 47596));
        ALL_AUDIOS.add(new AudioContent("0119_ch5_qp.wav", 16471));
        ALL_AUDIOS.add(new AudioContent("0120_ch5_q1.wav", 39382));
        ALL_AUDIOS.add(new AudioContent("0121_ch5_q1_ca.wav", 18438));
        ALL_AUDIOS.add(new AudioContent("0122_ch5_q1_wa.wav", 20182));
        ALL_AUDIOS.add(new AudioContent("0123_ch5_q2.wav", 41745));
        ALL_AUDIOS.add(new AudioContent("0124_ch5_q2_ca.wav", 22265));
        ALL_AUDIOS.add(new AudioContent("0125_ch5_q2_wa.wav", 20671));
        ALL_AUDIOS.add(new AudioContent("0126_ch5_q3.wav", 35093));
        ALL_AUDIOS.add(new AudioContent("0127_ch5_q3_ca.wav", 20637));
        ALL_AUDIOS.add(new AudioContent("0128_ch5_q3_wa.wav", 19806));
        ALL_AUDIOS.add(new AudioContent("0129_ch5_q4.wav", 43208));
        ALL_AUDIOS.add(new AudioContent("0130_ch5_q4_ca.wav", 21248));
        ALL_AUDIOS.add(new AudioContent("0131_ch5_q4_wa.wav", 21167));
        ALL_AUDIOS.add(new AudioContent("0132_ch5_4_ca.wav", 29509));
        ALL_AUDIOS.add(new AudioContent("0133_ch5_3_ca.wav", 28846));
        ALL_AUDIOS.add(new AudioContent("0134_ch5_2_ca.wav", 21673));
        ALL_AUDIOS.add(new AudioContent("0135_ch5_1_ca.wav", 24604));
        ALL_AUDIOS.add(new AudioContent("0136_ch5_0_ca.wav", 25007));
        ALL_AUDIOS.add(new AudioContent("0137_ch5_end_op.wav", 23577));
        ALL_AUDIOS.add(new AudioContent("0138_ch6_l1.wav", 243223));
        ALL_AUDIOS.add(new AudioContent("0139_ch6_l1_op.wav", 22481));
        ALL_AUDIOS.add(new AudioContent("0140_ch6_l2.wav", 201211));
        ALL_AUDIOS.add(new AudioContent("0141_ch6_l2_op.wav", 18670));
        ALL_AUDIOS.add(new AudioContent("0142_ch6_l3.wav", 193318));
        ALL_AUDIOS.add(new AudioContent("0143_ch6_l3_op.wav", 22100));
        ALL_AUDIOS.add(new AudioContent("0144_ch6_l4.wav", 186703));
        ALL_AUDIOS.add(new AudioContent("0145_ch6_l4_op.wav", 48141));
        ALL_AUDIOS.add(new AudioContent("0146_ch6_qp.wav", 16969));
        ALL_AUDIOS.add(new AudioContent("0147_ch6_q1.wav", 36123));
        ALL_AUDIOS.add(new AudioContent("0148_ch6_q1_ca.wav", 19681));
        ALL_AUDIOS.add(new AudioContent("0149_ch6_q1_wa.wav", 21139));
        ALL_AUDIOS.add(new AudioContent("0150_ch6_q2.wav", 42184));
        ALL_AUDIOS.add(new AudioContent("0151_ch6_q2_ca.wav", 21850));
        ALL_AUDIOS.add(new AudioContent("0152_ch6_q2_wa.wav", 20341));
        ALL_AUDIOS.add(new AudioContent("0153_ch6_q3.wav", 38004));
        ALL_AUDIOS.add(new AudioContent("0154_ch6_q3_ca.wav", 20214));
        ALL_AUDIOS.add(new AudioContent("0155_ch6_q3_wa.wav", 19493));
        ALL_AUDIOS.add(new AudioContent("0156_ch6_q4.wav", 39670));
        ALL_AUDIOS.add(new AudioContent("0157_ch6_q4_ca.wav", 20325));
        ALL_AUDIOS.add(new AudioContent("0158_ch6_q4_wa.wav", 19583));
        ALL_AUDIOS.add(new AudioContent("0159_ch6_4_ca.wav", 28666));
        ALL_AUDIOS.add(new AudioContent("0160_ch6_3_ca.wav", 27046));
        ALL_AUDIOS.add(new AudioContent("0161_ch6_2_ca.wav", 20782));
        ALL_AUDIOS.add(new AudioContent("0162_ch6_1_ca.wav", 23740));
        ALL_AUDIOS.add(new AudioContent("0163_ch6_0_ca.wav", 24081));
        ALL_AUDIOS.add(new AudioContent("0164_ch6_end_op.wav", 24102));
        ALL_AUDIOS.add(new AudioContent("0165_ch7_l1.wav", 222500));
        ALL_AUDIOS.add(new AudioContent("0166_ch7_l1_op.wav", 20362));
        ALL_AUDIOS.add(new AudioContent("0167_ch7_l2.wav", 229438));
        ALL_AUDIOS.add(new AudioContent("0168_ch7_l2_op.wav", 18832));
        ALL_AUDIOS.add(new AudioContent("0169_ch7_l3.wav", 183399));
        ALL_AUDIOS.add(new AudioContent("0170_ch7_l3_op.wav", 22085));
        ALL_AUDIOS.add(new AudioContent("0171_ch7_l4.wav", 171653));
        ALL_AUDIOS.add(new AudioContent("0172_ch7_l4_op.wav", 47131));
        ALL_AUDIOS.add(new AudioContent("0173_ch7_qp.wav", 16769));
        ALL_AUDIOS.add(new AudioContent("0174_ch7_q1.wav", 36899));
        ALL_AUDIOS.add(new AudioContent("0175_ch7_q1_ca.wav", 19554));
        ALL_AUDIOS.add(new AudioContent("0176_ch7_q1_wa.wav", 20686));
        ALL_AUDIOS.add(new AudioContent("0177_ch7_q2.wav", 37809));
        ALL_AUDIOS.add(new AudioContent("0178_ch7_q2_ca.wav", 23640));
        ALL_AUDIOS.add(new AudioContent("0179_ch7_q2_wa.wav", 21601));
        ALL_AUDIOS.add(new AudioContent("0180_ch7_q3.wav", 39554));
        ALL_AUDIOS.add(new AudioContent("0181_ch7_q3_ca.wav", 19850));
        ALL_AUDIOS.add(new AudioContent("0182_ch7_q3_wa.wav", 21605));
        ALL_AUDIOS.add(new AudioContent("0183_ch7_q4.wav", 35113));
        ALL_AUDIOS.add(new AudioContent("0184_ch7_q4_ca.wav", 20352));
        ALL_AUDIOS.add(new AudioContent("0185_ch7_q4_wa.wav", 18389));
        ALL_AUDIOS.add(new AudioContent("0186_ch7_4_ca.wav", 28658));
        ALL_AUDIOS.add(new AudioContent("0187_ch7_3_ca.wav", 27792));
        ALL_AUDIOS.add(new AudioContent("0188_ch7_2_ca.wav", 21427));
        ALL_AUDIOS.add(new AudioContent("0189_ch7_1_ca.wav", 24029));
        ALL_AUDIOS.add(new AudioContent("0190_ch7_0_ca.wav", 24969));
        ALL_AUDIOS.add(new AudioContent("0191_ch7_end_op.wav", 24044));
        ALL_AUDIOS.add(new AudioContent("0192_ch8_l1.wav", 241264));
        ALL_AUDIOS.add(new AudioContent("0193_ch8_l1_op.wav", 20368));
        ALL_AUDIOS.add(new AudioContent("0194_ch8_l2.wav", 190396));
        ALL_AUDIOS.add(new AudioContent("0195_ch8_l2_op.wav", 18707));
        ALL_AUDIOS.add(new AudioContent("0196_ch8_l3.wav", 201015));
        ALL_AUDIOS.add(new AudioContent("0197_ch8_l3_op.wav", 22164));
        ALL_AUDIOS.add(new AudioContent("0198_ch8_l4.wav", 171713));
        ALL_AUDIOS.add(new AudioContent("0199_ch8_l4_op.wav", 47699));
        ALL_AUDIOS.add(new AudioContent("0200_ch8_qp.wav", 16493));
        ALL_AUDIOS.add(new AudioContent("0201_ch8_q1.wav", 40916));
        ALL_AUDIOS.add(new AudioContent("0202_ch8_q1_ca.wav", 23419));
        ALL_AUDIOS.add(new AudioContent("0203_ch8_q1_wa.wav", 21284));
        ALL_AUDIOS.add(new AudioContent("0204_ch8_q2.wav", 43199));
        ALL_AUDIOS.add(new AudioContent("0205_ch8_q2_ca.wav", 20147));
        ALL_AUDIOS.add(new AudioContent("0206_ch8_q2_wa.wav", 19963));
        ALL_AUDIOS.add(new AudioContent("0207_ch8_q3.wav", 38308));
        ALL_AUDIOS.add(new AudioContent("0208_ch8_q3_ca.wav", 20400));
        ALL_AUDIOS.add(new AudioContent("0209_ch8_q3_wa.wav", 20425));
        ALL_AUDIOS.add(new AudioContent("0210_ch8_q4.wav", 38507));
        ALL_AUDIOS.add(new AudioContent("0211_ch8_q4_ca.wav", 20177));
        ALL_AUDIOS.add(new AudioContent("0212_ch8_q4_wa.wav", 19059));
        ALL_AUDIOS.add(new AudioContent("0213_ch8_4_ca.wav", 28709));
        ALL_AUDIOS.add(new AudioContent("0214_ch8_3_ca.wav", 27984));
        ALL_AUDIOS.add(new AudioContent("0215_ch8_2_ca.wav", 20069));
        ALL_AUDIOS.add(new AudioContent("0216_ch8_1_ca.wav", 23434));
        ALL_AUDIOS.add(new AudioContent("0217_ch8_0_ca.wav", 23677));
        ALL_AUDIOS.add(new AudioContent("0218_ch8_end_op.wav", 22418));
        ALL_AUDIOS.add(new AudioContent("0219_ch9_l1.wav", 229848));
        ALL_AUDIOS.add(new AudioContent("0220_ch9_l1_op.wav", 20445));
        ALL_AUDIOS.add(new AudioContent("0221_ch9_l2.wav", 201977));
        ALL_AUDIOS.add(new AudioContent("0222_ch9_l2_op.wav", 18689));
        ALL_AUDIOS.add(new AudioContent("0223_ch9_l3.wav", 192115));
        ALL_AUDIOS.add(new AudioContent("0224_ch9_l3_op.wav", 21948));
        ALL_AUDIOS.add(new AudioContent("0225_ch9_l4.wav", 176411));
        ALL_AUDIOS.add(new AudioContent("0226_ch9_l4_op.wav", 49067));
        ALL_AUDIOS.add(new AudioContent("0227_ch9_qp.wav", 16668));
        ALL_AUDIOS.add(new AudioContent("0228_ch9_q1.wav", 30631));
        ALL_AUDIOS.add(new AudioContent("0229_ch9_q1_ca.wav", 19529));
        ALL_AUDIOS.add(new AudioContent("0230_ch9_q1_wa.wav", 19360));
        ALL_AUDIOS.add(new AudioContent("0231_ch9_q2.wav", 37557));
        ALL_AUDIOS.add(new AudioContent("0232_ch9_q2_ca.wav", 19824));
        ALL_AUDIOS.add(new AudioContent("0233_ch9_q2_wa.wav", 21520));
        ALL_AUDIOS.add(new AudioContent("0234_ch9_q3.wav", 35521));
        ALL_AUDIOS.add(new AudioContent("0235_ch9_q3_ca.wav", 21341));
        ALL_AUDIOS.add(new AudioContent("0236_ch9_q3_wa.wav", 19825));
        ALL_AUDIOS.add(new AudioContent("0237_ch9_q4.wav", 41818));
        ALL_AUDIOS.add(new AudioContent("0238_ch9_q4_ca.wav", 20043));
        ALL_AUDIOS.add(new AudioContent("0239_ch9_q4_wa.wav", 19098));
        ALL_AUDIOS.add(new AudioContent("0240_ch9_4_ca.wav", 28422));
        ALL_AUDIOS.add(new AudioContent("0241_ch9_3_ca.wav", 28718));
        ALL_AUDIOS.add(new AudioContent("0242_ch9_2_ca.wav", 21575));
        ALL_AUDIOS.add(new AudioContent("0243_ch9_1_ca.wav", 24509));
        ALL_AUDIOS.add(new AudioContent("0244_ch9_0_ca.wav", 24847));
        ALL_AUDIOS.add(new AudioContent("0245_ch9_end_op.wav", 15284));
        ALL_AUDIOS.add(new AudioContent("0246_thanks_p.wav", 29058));
        ALL_AUDIOS.add(new AudioContent("0247_error_p.wav", 3386));
        ALL_AUDIOS.add(new AudioContent("0248_final_score_0.wav", 6617));
        ALL_AUDIOS.add(new AudioContent("0249_final_score_1.wav", 6868));
        ALL_AUDIOS.add(new AudioContent("0250_final_score_2.wav", 6952));
        ALL_AUDIOS.add(new AudioContent("0251_final_score_3.wav", 6317));
        ALL_AUDIOS.add(new AudioContent("0252_final_score_4.wav", 6517));
        ALL_AUDIOS.add(new AudioContent("0253_final_score_5.wav", 6639));
        ALL_AUDIOS.add(new AudioContent("0254_final_score_6.wav", 6401));
        ALL_AUDIOS.add(new AudioContent("0255_final_score_7.wav", 6950));
        ALL_AUDIOS.add(new AudioContent("0256_final_score_8.wav", 6723));
        ALL_AUDIOS.add(new AudioContent("0257_final_score_9.wav", 6320));
        ALL_AUDIOS.add(new AudioContent("0258_final_score_10.wav", 6260));
        ALL_AUDIOS.add(new AudioContent("0259_final_score_11.wav", 6980));
        ALL_AUDIOS.add(new AudioContent("0260_final_score_12.wav", 6399));
        ALL_AUDIOS.add(new AudioContent("0261_final_score_13.wav", 7010));
        ALL_AUDIOS.add(new AudioContent("0262_final_score_14.wav", 6623));
        ALL_AUDIOS.add(new AudioContent("0263_final_score_15.wav", 6603));
        ALL_AUDIOS.add(new AudioContent("0264_final_score_16.wav", 6389));
        ALL_AUDIOS.add(new AudioContent("0265_final_score_17.wav", 6508));
        ALL_AUDIOS.add(new AudioContent("0266_final_score_18.wav", 6817));
        ALL_AUDIOS.add(new AudioContent("0267_final_score_19.wav", 9906));
        ALL_AUDIOS.add(new AudioContent("0268_final_score_20.wav", 9637));
        ALL_AUDIOS.add(new AudioContent("0269_final_score_21.wav", 9872));
        ALL_AUDIOS.add(new AudioContent("0270_final_score_22.wav", 9746));
        ALL_AUDIOS.add(new AudioContent("0271_final_score_23.wav", 9782));
        ALL_AUDIOS.add(new AudioContent("0272_final_score_24.wav", 10281));
        ALL_AUDIOS.add(new AudioContent("0273_final_score_25.wav", 10080));
        ALL_AUDIOS.add(new AudioContent("0274_final_score_26.wav", 10182));
        ALL_AUDIOS.add(new AudioContent("0275_final_score_27.wav", 10281));
        ALL_AUDIOS.add(new AudioContent("0276_final_score_28.wav", 10311));
        ALL_AUDIOS.add(new AudioContent("0277_final_score_29.wav", 10214));
        ALL_AUDIOS.add(new AudioContent("0278_final_score_30.wav", 9928));
        ALL_AUDIOS.add(new AudioContent("0279_final_score_31.wav", 10227));
        ALL_AUDIOS.add(new AudioContent("0280_final_score_32.wav", 10483));
        ALL_AUDIOS.add(new AudioContent("0281_final_score_33.wav", 10281));
        ALL_AUDIOS.add(new AudioContent("0282_final_score_34.wav", 10385));
        ALL_AUDIOS.add(new AudioContent("0283_final_score_35.wav", 10460));
        ALL_AUDIOS.add(new AudioContent("0284_final_score_36.wav", 10281));
        ALL_AUDIOS.add(new AudioContent("0285_score_less_than_18.wav", 46621));
        ALL_AUDIOS.add(new AudioContent("0286_score_18_or_more.wav", 103927));
    }
}