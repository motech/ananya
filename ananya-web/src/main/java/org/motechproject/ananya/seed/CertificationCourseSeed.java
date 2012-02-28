package org.motechproject.ananya.seed;

import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CertificationCourseSeed {
    public static final int NUMBER_OF_LESSONS_IN_A_CHAPTER = 4;
    public static final int NUMBER_OF_QUESTIONS_IN_A_CHAPTER = 4;
    public static final int NUMBER_OF_CHAPTERS_IN_COURSE = 9;
    @Autowired
    private AllNodes allNodes;

    @Seed(priority = 0)
    public void loadSeed(){
        Node certificateCourse = createNodeForCourse();
        allNodes.addNodeWithDescendants(certificateCourse);
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

        String endOptionName = String.format("ch%d_end_op",number);
        StringContent chapterMenu = new StringContent("hindi", "menu", findFileNameContaining(endOptionName));

        String quizHeaderName = String.format("ch%d_qp", number);
        StringContent quizHeader = new StringContent("hindi", "quizHeader", findFileNameContaining(quizHeaderName));

        chapterNode.addContent(chapterMenu);
        chapterNode.addContent(quizHeader);

        for(int score = 0; score<=NUMBER_OF_QUESTIONS_IN_A_CHAPTER; score++) {
            String scoreName = String.format("ch%d_%d_ca.wav", number, score);
            final String scoreKey = String.format("score %d", score);

            StringContent scoreReport = new StringContent("hindi", scoreKey, findFileNameContaining(scoreName));
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
        lessonNode.addData("type","lesson");

        String lessonName = String.format("ch%d_l%d.wav",chapterNumber, lessonNumber);
        StringContent lesson = new StringContent("hindi","lesson",findFileNameContaining(lessonName));
        String lessonMenuName = String.format("ch%d_l%d_op.wav",chapterNumber, lessonNumber);
        StringContent lessonMenu = new StringContent("hindi","menu",findFileNameContaining(lessonMenuName));

        lessonNode.addContent(lesson);
        lessonNode.addContent(lessonMenu);

        return lessonNode;
    }

    private Node createNodeForQuestion(int chapterNumber, int questionNumber){
        Node questionNode = new Node("Chapter " + chapterNumber + " Question " + questionNumber);
        questionNode.addData("type", "quiz");

        String questionKey = String.format("Chapter %d, Question %d", chapterNumber,questionNumber);
        questionNode.addData("correctAnswer", CORRECT_ANSWERS.get(questionKey));

        String questionName = String.format("ch%d_q%d.wav",chapterNumber, questionNumber);
        StringContent question = new StringContent("hindi","question",findFileNameContaining(questionName));

        String correctAnswerPromptName = String.format("ch%d_q%d_ca.wav",chapterNumber, questionNumber);
        StringContent correctAnswerPrompt = new StringContent("hindi","correct",findFileNameContaining(correctAnswerPromptName));

        String wrongAnswerPromptName = String.format("ch%d_q%d_wa.wav",chapterNumber, questionNumber);
        StringContent wrongAnswerPrompt = new StringContent("hindi","incorrect",findFileNameContaining(wrongAnswerPromptName));

        questionNode.addContent(question);
        questionNode.addContent(correctAnswerPrompt);
        questionNode.addContent(wrongAnswerPrompt);

        return questionNode;
    }

    private static String findFileNameContaining(String endOptionName) {
        for(String fileName: ALL_AUDIOS) {
            if(fileName.contains(endOptionName)) {
                return fileName;
            }
        }
        return null;
    }

    private static final Map<String, String> CORRECT_ANSWERS = new HashMap<String, String>();
    
    static {
        CORRECT_ANSWERS.put("Chapter 1, Question 1","1");
        CORRECT_ANSWERS.put("Chapter 1, Question 2","2");
        CORRECT_ANSWERS.put("Chapter 1, Question 3","1");
        CORRECT_ANSWERS.put("Chapter 1, Question 4","2");
        
        CORRECT_ANSWERS.put("Chapter 2, Question 1","1");
        CORRECT_ANSWERS.put("Chapter 2, Question 2","2");
        CORRECT_ANSWERS.put("Chapter 2, Question 3","1");
        CORRECT_ANSWERS.put("Chapter 2, Question 4","2");
        
        CORRECT_ANSWERS.put("Chapter 3, Question 1","1");
        CORRECT_ANSWERS.put("Chapter 3, Question 2","2");
        CORRECT_ANSWERS.put("Chapter 3, Question 3","1");
        CORRECT_ANSWERS.put("Chapter 3, Question 4","2");
        
        CORRECT_ANSWERS.put("Chapter 4, Question 1","1");
        CORRECT_ANSWERS.put("Chapter 4, Question 2","2");
        CORRECT_ANSWERS.put("Chapter 4, Question 3","1");
        CORRECT_ANSWERS.put("Chapter 4, Question 4","2");
        
        CORRECT_ANSWERS.put("Chapter 5, Question 1","1");
        CORRECT_ANSWERS.put("Chapter 5, Question 2","2");
        CORRECT_ANSWERS.put("Chapter 5, Question 3","1");
        CORRECT_ANSWERS.put("Chapter 5, Question 4","2");
        
        CORRECT_ANSWERS.put("Chapter 6, Question 1","1");
        CORRECT_ANSWERS.put("Chapter 6, Question 2","2");
        CORRECT_ANSWERS.put("Chapter 6, Question 3","1");
        CORRECT_ANSWERS.put("Chapter 6, Question 4","2");
        
        CORRECT_ANSWERS.put("Chapter 7, Question 1","1");
        CORRECT_ANSWERS.put("Chapter 7, Question 2","2");
        CORRECT_ANSWERS.put("Chapter 7, Question 3","1");
        CORRECT_ANSWERS.put("Chapter 7, Question 4","2");
        
        CORRECT_ANSWERS.put("Chapter 8, Question 1","1");
        CORRECT_ANSWERS.put("Chapter 8, Question 2","2");
        CORRECT_ANSWERS.put("Chapter 8, Question 3","1");
        CORRECT_ANSWERS.put("Chapter 8, Question 4","2");
        
        CORRECT_ANSWERS.put("Chapter 9, Question 1","1");
        CORRECT_ANSWERS.put("Chapter 9, Question 2","2");
        CORRECT_ANSWERS.put("Chapter 9, Question 3","1");
        CORRECT_ANSWERS.put("Chapter 9, Question 4","2");
        
    }
    private static final String[] ALL_AUDIOS = {
            "0000_b_dead_space_1pt5_sec.wav",
            "0001_wp_ma_mohnish.wav",
            "0002_start_course_op.wav",
            "0002_wp_ama_ar.wav",
            "0003_ch1_l1.wav",
            "0003_ch1_l1_not_reg.wav",
            "0004_ch1_l1_op.wav",
            "0005_ch1_l2.wav",
            "0005_register1.wav",
            "0006_alreadyreg.wav",
            "0006_ch1_l2_op.wav",
            "0007_ch1_l3.wav",
            "0007_notreg.wav",
            "0008_ch1_l3_op.wav",
            "0009_ch1_l4.wav",
            "0010_ch1_l4_op.wav",
            "0011_ch1_qp.wav",
            "0012_ch1_q1.wav",
            "0013_ch1_q1_ca.wav",
            "0014_ch1_q1_wa.wav",
            "0015_ch1_q2.wav",
            "0016_ch1_q2_ca.wav",
            "0017_ch1_q2_wa.wav",
            "0018_ch1_q3.wav",
            "0019_ch1_q3_ca.wav",
            "0020_ch1_q3_wa.wav",
            "0021_ch1_q4.wav",
            "0022_ch1_q4_ca.wav",
            "0023_ch1_q4_wa.wav",
            "0024_ch1_4_ca.wav",
            "0025_ch1_3_ca.wav",
            "0026_ch1_2_ca.wav",
            "0027_ch1_1_ca.wav",
            "0028_ch1_0_ca.wav",
            "0029_ch1_end_op.wav",
            "0030_ch2_l1.wav",
            "0031_ch2_l1_op.wav",
            "0032_ch2_l2.wav",
            "0033_ch2_l2_op.wav",
            "0033_welcome_back_generic.wav",
            "0034_ch2_l3.wav",
            "0034_ch_5_summary.wav",
            "0035_ch2_l3_op.wav",
            "0035_ch_5_mini_certificate_and_summary.wav",
            "0036_ch2_l4.wav",
            "0037_ch2_l4_op.wav",
            "0038_ch2_qp.wav",
            "0039_ch2_q1.wav",
            "0040_ch2_q1_ca.wav",
            "0041_ch2_q1_wa.wav",
            "0042_ch2_q2.wav",
            "0043_ch2_q2_ca.wav",
            "0044_ch2_q2_wa.wav",
            "0045_ch2_q3.wav",
            "0046_ch2_q3_ca.wav",
            "0047_ch2_q3_wa.wav",
            "0048_ch2_q4.wav",
            "0049_ch2_q4_ca.wav",
            "0050_ch2_q4_wa.wav",
            "0051_ch2_4_ca.wav",
            "0052_ch2_3_ca.wav",
            "0053_ch2_2_ca.wav",
            "0054_ch2_1_ca.wav",
            "0055_ch2_0_ca.wav",
            "0056_ch2_end_op.wav",
            "0057_ch3_l1.wav",
            "0058_ch3_l1_op.wav",
            "0059_ch3_l2.wav",
            "0060_ch3_l2_op.wav",
            "0061_ch3_l3.wav",
            "0062_ch3_l3_op.wav",
            "0063_ch3_l4.wav",
            "0064_ch3_l4_op.wav",
            "0065_ch3_qp.wav",
            "0066_ch3_q1.wav",
            "0067_ch3_q1_ca.wav",
            "0068_ch3_q1_wa.wav",
            "0069_ch3_q2.wav",
            "0070_ch3_q2_ca.wav",
            "0071_ch3_q2_wa.wav",
            "0072_ch3_q3.wav",
            "0073_ch3_q3_ca.wav",
            "0074_ch3_q3_wa.wav",
            "0075_ch3_q4.wav",
            "0076_ch3_q4_ca.wav",
            "0077_ch3_q4_wa.wav",
            "0078_ch3_4_ca.wav",
            "0079_ch3_3_ca.wav",
            "0080_ch3_2_ca.wav",
            "0081_ch3_1_ca.wav",
            "0082_ch3_0_ca.wav",
            "0083_ch3_end_op.wav",
            "0084_ch4_l1.wav",
            "0085_ch4_l1_op.wav",
            "0086_ch4_l2.wav",
            "0087_ch4_l2_op.wav",
            "0088_ch4_l3.wav",
            "0089_ch4_l3_op.wav",
            "0090_ch4_l4.wav",
            "0091_ch4_l4_op.wav",
            "0092_ch4_qp.wav",
            "0093_ch4_q1.wav",
            "0094_ch4_q1_ca.wav",
            "0095_ch4_q1_wa.wav",
            "0096_ch4_q2.wav",
            "0097_ch4_q2_ca.wav",
            "0098_ch4_q2_wa.wav",
            "0099_ch4_q3.wav",
            "0100_ch4_q3_ca.wav",
            "0101_ch4_q3_wa.wav",
            "0102_ch4_q4.wav",
            "0103_ch4_q4_ca.wav",
            "0104_ch4_q4_wa.wav",
            "0105_ch4_4_ca.wav",
            "0106_ch4_3_ca.wav",
            "0107_ch4_2_ca.wav",
            "0108_ch4_1_ca.wav",
            "0109_ch4_0_ca.wav",
            "0110_ch4_end_op.wav",
            "0111_ch5_l1.wav",
            "0112_ch5_l1_op.wav",
            "0113_ch5_l2.wav",
            "0114_ch5_l2_op.wav",
            "0115_ch5_l3.wav",
            "0116_ch5_l3_op.wav",
            "0117_ch5_l4.wav",
            "0118_ch5_l4_op.wav",
            "0119_ch5_qp.wav",
            "0120_ch5_q1.wav",
            "0121_ch5_q1_ca.wav",
            "0122_ch5_q1_wa.wav",
            "0123_ch5_q2.wav",
            "0124_ch5_q2_ca.wav",
            "0125_ch5_q2_wa.wav",
            "0126_ch5_q3.wav",
            "0127_ch5_q3_ca.wav",
            "0128_ch5_q3_wa.wav",
            "0129_ch5_q4.wav",
            "0130_ch5_q4_ca.wav",
            "0131_ch5_q4_wa.wav",
            "0132_ch5_4_ca.wav",
            "0133_ch5_3_ca.wav",
            "0134_ch5_2_ca.wav",
            "0135_ch5_1_ca.wav",
            "0136_ch5_0_ca.wav",
            "0137_ch5_end_op.wav",
            "0138_ch6_l1.wav",
            "0139_ch6_l1_op.wav",
            "0140_ch6_l2.wav",
            "0141_ch6_l2_op.wav",
            "0142_ch6_l3.wav",
            "0143_ch6_l3_op.wav",
            "0144_ch6_l4.wav",
            "0145_ch6_l4_op.wav",
            "0146_ch6_qp.wav",
            "0147_ch6_q1.wav",
            "0148_ch6_q1_ca.wav",
            "0149_ch6_q1_wa.wav",
            "0150_ch6_q2.wav",
            "0151_ch6_q2_ca.wav",
            "0152_ch6_q2_wa.wav",
            "0153_ch6_q3.wav",
            "0154_ch6_q3_ca.wav",
            "0155_ch6_q3_Wa.wav",
            "0156_ch6_q4.wav",
            "0157_ch6_q4_ca.wav",
            "0158_ch6_q4_wa.wav",
            "0159_ch6_4_ca.wav",
            "0160_ch6_3_ca.wav",
            "0161_ch6_2_ca.wav",
            "0162_ch6_1_ca.wav",
            "0163_ch6_0_ca.wav",
            "0164_ch6_end_op.wav",
            "0165_ch7_l1.wav",
            "0166_ch7_l1_op.wav",
            "0167_ch7_l2.wav",
            "0168_ch7_l2_op.wav",
            "0169_ch7_l3.wav",
            "0170_ch7_l3_op.wav",
            "0171_ch7_l4.wav",
            "0172_ch7_l4_op.wav",
            "0173_ch7_qp.wav",
            "0174_ch7_q1.wav",
            "0175_ch7_q1_ca.wav",
            "0176_ch7_q1_wa.wav",
            "0177_ch7_q2.wav",
            "0178_ch7_q2_ca.wav",
            "0179_ch7_q2_wa.wav",
            "0180_ch7_q3.wav",
            "0181_ch7_q3_ca.wav",
            "0182_ch7_q3_wa.wav",
            "0183_ch7_q4.wav",
            "0184_ch7_q4_ca.wav",
            "0185_ch7_q4_wa.wav",
            "0186_ch7_4_ca.wav",
            "0187_ch7_3_ca.wav",
            "0188_ch7_2_ca.wav",
            "0189_ch7_1_ca.wav",
            "0190_ch7_0_ca.wav",
            "0191_ch7_end_op.wav",
            "0192_ch8_l1.wav",
            "0193_ch8_l1_op.wav",
            "0194_ch8_l2.wav",
            "0195_ch8_l2_op.wav",
            "0196_ch8_l3.wav",
            "0197_ch8_l3_op.wav",
            "0198_ch8_l4.wav",
            "0199_ch8_l4_op.wav",
            "0200_ch8_qp.wav",
            "0201_ch8_q1.wav",
            "0202_ch8_q1_ca.wav",
            "0203_ch8_q1_wa.wav",
            "0204_ch8_q2.wav",
            "0205_ch8_q2_ca.wav",
            "0206_ch8_q2_wa.wav",
            "0207_ch8_q3.wav",
            "0208_ch8_q3_ca.wav",
            "0209_ch8_q3_wa.wav",
            "0210_ch8_q4.wav",
            "0211_ch8_q4_ca.wav",
            "0212_ch8_q4_wa.wav",
            "0213_ch8_4_ca.wav",
            "0214_ch8_3_ca.wav",
            "0215_ch8_2_ca.wav",
            "0216_ch8_1_ca.wav",
            "0217_ch8_0_ca.wav",
            "0218_ch8_end_op.wav",
            "0219_ch9_l1.wav",
            "0220_ch9_l1_op.wav",
            "0221_ch9_l2.wav",
            "0222_ch9_l2_op.wav",
            "0223_ch9_l3.wav",
            "0224_ch9_l3_op.wav",
            "0225_ch9_l4.wav",
            "0226_ch9_l4_op.wav",
            "0227_ch9_qp.wav",
            "0228_ch9_q1.wav",
            "0229_ch9_q1_ca.wav",
            "0230_ch9_q1_wa.wav",
            "0231_ch9_q2.wav",
            "0232_ch9_q2_ca.wav",
            "0233_ch9_q2_wa.wav",
            "0234_ch9_q3.wav",
            "0235_ch9_q3_ca.wav",
            "0236_ch9_q3_wa.wav",
            "0237_ch9_q4.wav",
            "0238_ch9_q4_ca.wav",
            "0239_ch9_q4_wa.wav",
            "0240_ch9_4_ca.wav",
            "0241_ch9_3_ca.wav",
            "0242_ch9_2_ca.wav",
            "0243_ch9_1_ca.wav",
            "0244_ch9_0_ca.wav",
            "0245_ch9_end_op.wav",
            "0246_thanks_p.wav",
            "0247_error_p.wav",
            "0248_final_score_0.wav",
            "0249_final_score_1.wav",
            "0250_final_score_2.wav",
            "0251_final_score_3.wav",
            "0252_final_score_4.wav",
            "0253_final_score_5.wav",
            "0254_final_score_6.wav",
            "0255_final_score_7.wav",
            "0256_final_score_8.wav",
            "0257_final_score_9.wav",
            "0258_final_score_10.wav",
            "0259_final_score_11.wav",
            "0260_final_score_12.wav",
            "0261_final_score_13.wav",
            "0262_final_score_14.wav",
            "0263_final_score_15.wav",
            "0264_final_score_16.wav",
            "0265_final_score_17.wav",
            "0266_final_score_18.wav",
            "0267_final_score_19.wav",
            "0268_final_score_20.wav",
            "0269_final_score_21.wav",
            "0270_final_score_22.wav",
            "0271_final_score_23.wav",
            "0272_final_score_24.wav",
            "0273_final_score_25.wav",
            "0274_final_score_26.wav",
            "0275_final_score_27.wav",
            "0276_final_score_28.wav",
            "0277_final_score_29.wav",
            "0278_final_score_30.wav",
            "0279_final_score_31.wav",
            "0280_final_score_32.wav",
            "0281_final_score_33.wav",
            "0282_final_score_34.wav",
            "0283_final_score_35.wav",
            "0284_final_score_36.wav",
            "0285_score_less_than_18.wav",
            "0286_score_18_or_more.wav"
    };
}