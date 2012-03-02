package org.motechproject.ananya.seed;

import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Arrays.asList;

@Component
public class JobAidSeed {
    @Autowired
    private AllNodes allNodes;

    @Seed(priority = 0)
    public void load() {
        allNodes.addNodeWithDescendants(createJobAidTree());
    }

    private Node createJobAidTree() {
        Node course = courseNode("JobAidCourse", "0002_select_level.wav");
        Node level1 = levelNode("level 1", "1", "0003_select_chapter_1.1.wav");
        Node level2 = levelNode("level 2", "2", "0004_select_chapter_1.2.wav");
        Node level3 = levelNode("level 3", "3", "0005_select_chapter_1.3.wav");
        Node level4 = levelNode("level 4", "4", "0006_select_chapter_1.4.wav");
        for (Node level : asList(level1, level2, level3, level4))
            course.addChild(level);

        Node level1Chap1 = chapterNode("Level 1 Chapter 1", "1", "0007_lessons_1.1.1.wav", "0008_select_lesson_1.1.1.wav");
        Node level1Chap2 = chapterNode("Level 1 Chapter 2", "2", "0009_lessons_1.1.2.wav", "0010_select_lesson_1.1.2.wav");
        Node level1Chap3 = chapterNode("Level 1 Chapter 3", "3", "0011_lessons_1.1.3.wav", "0012_select_lesson_1.1.3.wav");
        Node level1Chap4 = chapterNode("Level 1 Chapter 4", "4", "0013_lessons_1.1.4.wav", "0014_select_lesson_1.1.4.wav");
        for (Node level : asList(level1Chap1, level1Chap2, level1Chap3, level1Chap4))
            level1.addChild(level);

        Node level2Chap1 = chapterNode("Level 2 Chapter 1", "1", "0015_lessons_1.2.1.wav", "0016_select_lesson_1.2.1.wav");
        Node level2Chap2 = chapterNode("Level 2 Chapter 2", "2", "0017_lessons_1.2.2.wav", "0018_select_lesson_1.2.2.wav");
        Node level2Chap3 = chapterNode("Level 2 Chapter 3", "3", "0019_lessons_1.2.3.wav", "0020_select_lesson_1.2.3.wav");
        Node level2Chap4 = chapterNode("Level 2 Chapter 4", "4", "0021_lessons_1.2.4.wav", "0022_select_lesson_1.2.4.wav");
        for (Node level : asList(level2Chap1, level2Chap2, level2Chap3, level2Chap4))
            level2.addChild(level);

        Node level3Chap1 = chapterNode("Level 3 Chapter 1", "1", "0023_lessons_1.3.1.wav", "0024_select_lesson_1.3.1.wav");
        Node level3Chap2 = chapterNode("Level 3 Chapter 2", "2", "0025_lessons_1.3.2.wav", "0026_select_lesson_1.3.2.wav");
        Node level3Chap3 = chapterNode("Level 3 Chapter 3", "3", "0027_lessons_1.3.3.wav", "0028_select_lesson_1.3.3.wav");
        Node level3Chap4 = chapterNode("Level 3 Chapter 4", "4", "0029_lessons_1.3.4.wav", "0030_select_lesson_1.3.4.wav");
        for (Node level : asList(level3Chap1, level3Chap2, level3Chap3, level3Chap4))
            level3.addChild(level);

        Node level4Chap1 = chapterNode("Level 4 Chapter 1", "1", "0031_lessons_1.4.1.wav", "0032_select_lesson_1.4.1.wav");
        Node level4Chap2 = chapterNode("Level 4 Chapter 2", "2", "0033_lessons_1.4.2.wav", "0034_select_lesson_1.4.2.wav");
        Node level4Chap3 = chapterNode("Level 4 Chapter 3", "3", "0035_lessons_1.4.3.wav", "0036_select_lesson_1.4.3.wav");
        Node level4Chap4 = chapterNode("Level 4 Chapter 4", "4", "0037_lessons_1.4.4.wav", "0038_select_lesson_1.4.4.wav");
        for (Node level : asList(level4Chap1, level4Chap2, level4Chap3, level4Chap4))
            level4.addChild(level);

        addLessonsToChapter(level1Chap1, new String[][]{{"5771111","0001_prep_for_child_birth"}, {"5771112","0002_ifa_and_tt"}, {"5771113","0003_planning"}, {"5771114","0004_planning_and_saving"}} );
        addLessonsToChapter(level1Chap2, new String[][]{{"5771121","0005_inst_delivery"}, {"5771122","0006_six_cleans"}, {"5771123","0007_emereg_for_mother"}} );
        addLessonsToChapter(level1Chap3, new String[][]{{"5771124","0008_handling_newborn"}, {"5771131","0009_cord_care"}, {"5771132","0010_thermal_care"}, {"5771133","0011_early_initiation_bf"}} );
        addLessonsToChapter(level1Chap4, new String[][]{{"5771151","0017_need_fp"}, {"5771152","0018_spacing_and_y"}, {"5771153","0019_limiting_and_y"}, {"5771154","0020_options_ppfp"}} );
        addLessonsToChapter(level2Chap1, new String[][]{{"5771131","0009_cord_care"}, {"5771132","0010_thermal_care"}, {"5771133","0011_early_initiation_bf"}, {"5771134","0012_pnc_visits"}} );
        addLessonsToChapter(level2Chap2, new String[][]{{"5771141","0013_emerg_for_mother_after_child_birth"}, {"5771142","0014_danger_signs_preterm_baby"}, {"5771143","0015_kangaroo_care_preterm_baby"},{"5771144","0016_danger_signs_sepsis"}});
        addLessonsToChapter(level2Chap3, new String[][]{{"5771174","0028_exclusive_bf"}, {"5771181","0029_howto_bf"}, {"5771171","0025_immonization_imp_and_res"}, {"5771172","0026_immunization_doses"}} );
        addLessonsToChapter(level2Chap4, new String[][]{{"5771151","0017_need_fp"}, {"5771154","0020_options_ppfp"}, {"5771193","0035_hand_Washing_risk_perc"},{"5771194","0036_hand_washing_when_and_how"}} );
        addLessonsToChapter(level3Chap1, new String[][]{{"5771151","0017_need_fp"}, {"5771152","0018_spacing_and_y"}, {"5771153","0019_limiting_and_y"},{"5771154","0020_options_ppfp"}} );
        addLessonsToChapter(level3Chap2, new String[][]{{"5771161","0021_tubal_ligation"}, {"5771162","0022_iud"}, {"5771163","0015_kangaroo_care_preterm_baby"},{"5771164","0016_danger_signs_sepsis"}} );
        addLessonsToChapter(level3Chap3, new String[][]{{"5771174","0028_exclusive_bf"}, {"5771181","0029_howto_bf"}, {"5771182","0030_benefits_mother_bf"},{"5771183","0031_comp_feeding"}} );
        addLessonsToChapter(level3Chap4, new String[][]{{"5771171","0025_immonization_imp_and_res"}, {"5771172","0026_immunization_doses"}, {"5771173","0027_immunization_comp"},{"5771194","0028_exclusive_bf"}} );
        addLessonsToChapter(level4Chap1, new String[][]{{"5771151","0017_need_fp"}, {"5771152","0018_spacing_and_y"}, {"5771153","0019_limiting_and_y"},{"5771154","0020_options_ppfp"}} );
        addLessonsToChapter(level4Chap2, new String[][]{{"5771161","0021_tubal_ligation"}, {"5771162","0022_iud"}, {"5771163","0023_injectable"},{"5771164","0024_condoms_and_ocp"}} );
        addLessonsToChapter(level4Chap3, new String[][]{{"5771183","0031_comp_feeding"}, {"5771184","0032_active_feeding"}, {"5771191","0033_quality_food_hand_washing"},{"5771192","0034_quantity_food_hand_washing"}} );
        addLessonsToChapter(level4Chap4, new String[][]{{"5771171","0025_immonization_imp_and_res"}, {"5771173","0027_immunization_comp"}, {"5771193","0035_hand_Washing_risk_perc"},{"5771194","0036_hand_washing_when_and_how"}} );

        return course;
    }

    private void addLessonsToChapter(Node chapter, String[][] lessons) {
        int i = 1;
        for (String lesson[] : lessons) {
            chapter.addChild(lessonNode(chapter.getName() + " Lesson" + i, i + "", lesson[0], "" + lesson[1] + ".wav"));
            i++;
        }
    }


    private Node courseNode(String name, String menu) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("type", "Level");
        List<StringContent> content = Arrays.asList(new StringContent("hindi", "menu", menu));
        return new Node(name, data, content, new ArrayList<Node>());
    }

    private Node levelNode(String name, String number, String menu) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("type", "Level");
        data.put("number", number);
        List<StringContent> content = Arrays.asList(new StringContent("hindi", "menu", menu));
        return new Node(name, data, content, new ArrayList<Node>());
    }

    private Node chapterNode(String name, String number, String intro, String menu) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("type", "Chapter");
        data.put("number", number);
        List<StringContent> content = Arrays.asList(new StringContent("hindi", "introduction", intro), new StringContent("hindi", "menu", menu));
        return new Node(name, data, content, new ArrayList<Node>());
    }

    private Node lessonNode(String name, String number, String shortcode, String detail) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("type", "Lesson");
        data.put("number", number);
        data.put("shortcode", shortcode);
        List<StringContent> contents = Arrays.asList(new StringContent("hindi", "lesson", detail));
        return new Node(name, data, contents, new ArrayList<Node>());
    }

}
