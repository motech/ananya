package org.motechproject.ananya.seed.domain;

import org.motechproject.ananya.domain.Node;
import org.motechproject.cmslite.api.model.StringContent;

import java.util.*;

import static java.util.Arrays.asList;

public class JobAidTree {

	public Node build() {
		Node course = courseNode("JobAidCourse", "0002_select_level.wav", "42787");
		Node level1 = levelNode("level 1", "1", "0003_select_chapter_1.1.wav", "33652");
		Node level2 = levelNode("level 2", "2", "0004_select_chapter_1.2.wav", "41841");
		Node level3 = levelNode("level 3", "3", "0005_select_chapter_1.3.wav", "36591");
		Node level4 = levelNode("level 4", "4", "0006_select_chapter_1.4.wav", "39226");
		for (Node level : asList(level1, level2, level3, level4))
			course.addChild(level);

		Node level1Chap1 = chapterNode("Level 1 Chapter 1", "1", "0007_lessons_1.1.1.wav", "32979", "0008_select_lesson_1.1.1.wav", "23671");
		Node level1Chap2 = chapterNode("Level 1 Chapter 2", "2", "0009_lessons_1.1.2.wav", "32323", "0010_select_lesson_1.1.2.wav", "22253");
		Node level1Chap3 = chapterNode("Level 1 Chapter 3", "3", "0011_lessons_1.1.3.wav", "36244", "0012_select_lesson_1.1.3.wav", "32771");
		Node level1Chap4 = chapterNode("Level 1 Chapter 4", "4", "0013_lessons_1.1.4.wav", "26718", "0014_select_lesson_1.1.4.wav", "27730");
		for (Node level : asList(level1Chap1, level1Chap2, level1Chap3, level1Chap4))
			level1.addChild(level);

		Node level2Chap1 = chapterNode("Level 2 Chapter 1", "1", "0015_lessons_1.2.1.wav", "39279", "0016_select_lesson_1.2.1.wav", "34554");
		Node level2Chap2 = chapterNode("Level 2 Chapter 2", "2", "0017_lessons_1.2.2.wav", "40193", "0018_select_lesson_1.2.2.wav", "38046");
		Node level2Chap3 = chapterNode("Level 2 Chapter 3", "3", "0019_lessons_1.2.3.wav", "36740", "0020_select_lesson_1.2.3.wav", "33398");
		Node level2Chap4 = chapterNode("Level 2 Chapter 4", "4", "0021_lessons_1.2.4.wav", "33267", "0022_select_lesson_1.2.4.wav", "28985");
		for (Node level : asList(level2Chap1, level2Chap2, level2Chap3, level2Chap4))
			level2.addChild(level);

		Node level3Chap1 = chapterNode("Level 3 Chapter 1", "1", "0023_lessons_1.3.1.wav", "31245", "0024_select_lesson_1.3.1.wav", "28405");
		Node level3Chap2 = chapterNode("Level 3 Chapter 2", "2", "0025_lessons_1.3.2.wav", "25863", "0026_select_lesson_1.3.2.wav", "22579");
		Node level3Chap3 = chapterNode("Level 3 Chapter 3", "3", "0027_lessons_1.3.3.wav", "37041", "0028_select_lesson_1.3.3.wav", "32801");
		Node level3Chap4 = chapterNode("Level 3 Chapter 4", "4", "0029_lessons_1.3.4.wav", "37428", "0030_select_lesson_1.3.4.wav", "32944");
		for (Node level : asList(level3Chap1, level3Chap2, level3Chap3, level3Chap4))
			level3.addChild(level);

		Node level4Chap1 = chapterNode("Level 4 Chapter 1", "1", "0031_lessons_1.4.1.wav", "31184", "0032_select_lesson_1.4.1.wav", "28484");
		Node level4Chap2 = chapterNode("Level 4 Chapter 2", "2", "0033_lessons_1.4.2.wav", "25821", "0034_select_lesson_1.4.2.wav", "22342");
		Node level4Chap3 = chapterNode("Level 4 Chapter 3", "3", "0035_lessons_1.4.3.wav", "37446", "0036_select_lesson_1.4.3.wav", "34845");
		Node level4Chap4 = chapterNode("Level 4 Chapter 4", "4", "0037_lessons_1.4.4.wav", "33971", "0038_select_lesson_1.4.4.wav", "32952");
		for (Node level : asList(level4Chap1, level4Chap2, level4Chap3, level4Chap4))
			level4.addChild(level);

		addLessonsToChapter(level1Chap1, new String[][]{{"11", "0001_prep_for_child_birth", "96734"}, {"12", "0002_ifa_and_tt", "120453"}, {"13", "0003_planning", "104822"}, {"14", "0004_planning_and_saving", "106859"}});
		addLessonsToChapter(level1Chap2, new String[][]{{"21", "0005_inst_delivery", "107713"}, {"22", "0006_six_cleans", "120188"}, {"23", "0007_emereg_for_mother", "110428"}});
		addLessonsToChapter(level1Chap3, new String[][]{{"24", "0008_handling_newborn", "115458"}, {"31", "0009_cord_care", "121834"}, {"32", "0010_thermal_care", "106064"}, {"33", "0011_early_initiation_bf", "120354"}});
		addLessonsToChapter(level1Chap4, new String[][]{{"51", "0017_need_fp", "117012"}, {"52", "0018_spacing_and_y", "124719"}, {"53", "0019_limiting_and_y", "105442"}, {"54", "0020_options_ppfp", "115268"}});
		addLessonsToChapter(level2Chap1, new String[][]{{"31", "0009_cord_care", "121834"}, {"32", "0010_thermal_care", "106064"}, {"33", "0011_early_initiation_bf", "120354"}, {"34", "0012_pnc_visits", "123584"}});
		addLessonsToChapter(level2Chap2, new String[][]{{"41", "0013_emerg_for_mother_after_child_birth", "118369"}, {"42", "0014_danger_signs_preterm_baby", "118361"}, {"43", "0015_kangaroo_care_preterm_baby", "115630"}, {"44", "0016_danger_signs_sepsis", "108069"}});
		addLessonsToChapter(level2Chap3, new String[][]{{"74", "0028_exclusive_bf", "106275"}, {"81", "0029_howto_bf", "118255"}, {"71", "0025_immonization_imp_and_res", "120467"}, {"72", "0026_immunization_doses", "135660"}});
		addLessonsToChapter(level2Chap4, new String[][]{{"51", "0017_need_fp", "117012"}, {"54", "0020_options_ppfp", "115268"}, {"93", "0035_hand_washing_risk_perc", "115264"}, {"94", "0036_hand_washing_when_and_how", "110353"}});
		addLessonsToChapter(level3Chap1, new String[][]{{"51", "0017_need_fp", "117012"}, {"52", "0018_spacing_and_y", "124719"}, {"53", "0019_limiting_and_y", "105442"}, {"54", "0020_options_ppfp", "115268"}});
		addLessonsToChapter(level3Chap2, new String[][]{{"61", "0021_tubal_ligation", "118300"}, {"62", "0022_iud", "119581"}, {"63", "0023_injectable", "115630"}, {"64", "0024_condoms_and_ocp", "108069"}});
		addLessonsToChapter(level3Chap3, new String[][]{{"74", "0028_exclusive_bf", "106275"}, {"81", "0029_howto_bf", "118255"}, {"82", "0030_benefits_mother_bf", "124482"}, {"83", "0031_comp_feeding", "113575"}});
		addLessonsToChapter(level3Chap4, new String[][]{{"71", "0025_immonization_imp_and_res", "120467"}, {"72", "0026_immunization_doses", "135660"}, {"73", "0027_immunization_comp", "120683"}, {"94", "0036_hand_washing_when_and_how", "106275"}});
		addLessonsToChapter(level4Chap1, new String[][]{{"51", "0017_need_fp", "117012"}, {"52", "0018_spacing_and_y", "124719"}, {"53", "0019_limiting_and_y", "105442"}, {"54", "0020_options_ppfp", "115268"}});
		addLessonsToChapter(level4Chap2, new String[][]{{"61", "0021_tubal_ligation", "118300"}, {"62", "0022_iud", "119581"}, {"63", "0023_injectable", "121198"}, {"64", "0024_condoms_and_ocp", "122935"}});
		addLessonsToChapter(level4Chap3, new String[][]{{"83", "0031_comp_feeding", "113575"}, {"84", "0032_active_feeding", "110351"}, {"91", "0033_quality_food_hand_washing", "130363"}, {"92", "0034_quantity_food_hand_washing", "131009"}});
		addLessonsToChapter(level4Chap4, new String[][]{{"71", "0025_immonization_imp_and_res", "120467"}, {"73", "0027_immunization_comp", "120683"}, {"93", "0035_hand_washing_risk_perc", "115264"}, {"94", "0036_hand_washing_when_and_how", "110353"}});

		return course;
	}




	public Map<String, List<Node>> buildAdditionalChaptersForMK(Map<String, List<Node>> hmParentNodeNameChildNodes){		//Map<String, ArrayList<Node>>

		Node chapter = null;
		List<Node> chapterNodeslevel1 = new ArrayList<Node>();
		List<Node> chapterNodeslevel2 = new ArrayList<Node>();
		List<Node> chapterNodeslevel3 = new ArrayList<Node>();
		List<Node> chapterNodeslevel4 = new ArrayList<Node>();
		
		chapter = chapterNode("Level 1 Chapter 5", "5", "0014.1_lessons_1.1.5.wav", "43054", "0014.2_select_lesson_1.1.5.wav", "27999");
		addLessonsToChapter(chapter, new String[][]{{"93", "0017_handwashing_when_and_why", "117012"}, {"85", "0018_open_defecation_1", "124719"}, {"86", "0019_open_defecation_2", "105442"}});
		chapterNodeslevel1.add(chapter);		
		chapter = chapterNode("Level 1 Chapter 6", "6", "0014.3_lessons_1.1.6.wav", "29175", "0014.4_select_lesson_1.1.6.wav", "20215");
		addLessonsToChapter(chapter, new String[][]{{"98", "0017_malaria_prevention", "117012"}, {"99", "0018_malaria_treatment_and_management", "124719"}});
		chapterNodeslevel1.add(chapter);
		hmParentNodeNameChildNodes.put("level 1",chapterNodeslevel1);
		
		chapter = chapterNode("Level 2 Chapter 5", "5", "0022.1_lessons_1.2.5.wav", "40476", "0022.2_select_lesson_1.2.5.wav", "34565");
		addLessonsToChapter(chapter, new String[][]{{"93", "0028_when_and_why_to_wash_hands", "106275"}, {"94", "0029_diarrahoea_prevention", "118255"}, {"85", "0025_open_defecation_1", "120467"}, {"86", "0026_open_defecation_2", "135660"}});
		chapterNodeslevel2.add(chapter);
		chapter = chapterNode("Level 2 Chapter 6", "6", "0022.3_lessons_1.2.6.wav", "36491", "0022.4_select_lesson_1.2.6.wav", "30099");
		addLessonsToChapter(chapter, new String[][]{{"96", "0017_pneumonia_prevention", "117012"}, {"97", "0020_pneumonia_tratment_and_management", "115268"}, {"98", "0035_malaria_prevention", "115264"}, {"99", "0036_malaria_treatment_and_management", "110353"}});
		chapterNodeslevel2.add(chapter);
		hmParentNodeNameChildNodes.put("level 2",chapterNodeslevel2);
		
		chapter = chapterNode("Level 3 Chapter 5", "5", "0030.1_lessons_1.3.5.wav", "43964", "0030.2_select_lesson_1.3.5.wav", "44169");
		addLessonsToChapter(chapter, new String[][]{{"94", "0025_diarrhea_prevention", "120467"}, {"95", "0026_management_and_treatment_of_diarrhea", "135660"}, {"96", "pneumonia_prevention", "120683"}, {"97", "0036_pneumonia_tratment_and_management", "106275"}, {"98", "0036_malaria_prevention", "106275"}, {"99", "0036_malaria_treatment_and_management", "106275"}});
		chapterNodeslevel3.add(chapter);
		hmParentNodeNameChildNodes.put("level 3",chapterNodeslevel3);
		
		chapter = chapterNode("Level 4 Chapter 5", "5", "0038.1_lessons_1.4.5.wav", "43277", "0038.2_select_lesson_1.4.5.wav", "44083");
		addLessonsToChapter(chapter, new String[][]{{"94", "0025_diarrhea_prevention", "120467"}, {"95", "0026_management_and_treatment_of_diarrhea", "135660"}, {"96", "pneumonia_prevention", "120683"}, {"97", "0036_pneumonia_tratment_and_management", "106275"}, {"98", "0036_malaria_prevention", "106275"}, {"99", "0036_malaria_treatment_and_management", "106275"}});
		chapterNodeslevel4.add(chapter);
		hmParentNodeNameChildNodes.put("level 4",chapterNodeslevel4);
		
		return hmParentNodeNameChildNodes;
	}

	public Map<String, List<Node>> buildAdditionalLessonsForMK(Map<String, List<Node>> hmParentNodeNameChildNodes) {

		Node lesson = null;
		List<Node> lessonNodeslevel3 = new ArrayList<Node>();	
		List<Node> lessonNodeslevel4 = new ArrayList<Node>();	

		lesson = makeLessonNode("Level 3 Chapter 4", new String[]{"85", "0018_open_defecation_1", "124719"}, 5);
		lessonNodeslevel3.add(lesson);
		lesson = makeLessonNode("Level 3 Chapter 4", new String[]{"86", "0019_open_defecation_2", "105442"}, 6);
		lessonNodeslevel3.add(lesson);
		hmParentNodeNameChildNodes.put("Level 3 Chapter 4", lessonNodeslevel3);

		lesson = makeLessonNode("Level 4 Chapter 4", new String[]{"86", "0019_open_defecation_2", "105442"}, 5);
		lessonNodeslevel4.add(lesson);

		hmParentNodeNameChildNodes.put("Level 4 Chapter 4", lessonNodeslevel4);
		
		return hmParentNodeNameChildNodes;
	}

	
	public Map<String, List<Node>> buildAdditionalLevelForMKICDS(Map<String, List<Node>> hmParentNodeNameChildNodes) {

		List<Node> levelNodes = new ArrayList<Node>();
		
		Node level5 = levelNode("level 5", "5", "0006.1_select_ch1.5.wav", "38640");
	   
		Node level5Chap1 = chapterNode("Level 5 Chapter 1", "1", "0038.3_Lesson_1.5.1.wav", "13820", "0038.4_select_lesons1.5.1.wav", "22220");
		Node level5Chap2 = chapterNode("Level 5 Chapter 2", "2", "0038.5_Lesson_1.5.2.wav", "14140", "0038.6_select_lesons1.5.2.wav", "13430");
		Node level5Chap3 = chapterNode("Level 5 Chapter 3", "3", "0038.7_Lesson_1.5.3.wav", "16890", "0038.8_select_lesons1.5.3.wav", "34830");
		for (Node level : asList(level5Chap1, level5Chap2, level5Chap3))
			level5.addChild(level);
		
		addLessonsToChapter(level5Chap1, new String[][]{{"15", "0044_Growth_monitoring_imp", "106750"}, {"16", "0045_Developmental_milestones", "108120"}});
		addLessonsToChapter(level5Chap2, new String[][]{{"17", "0046_Malnutrition", "104560"}});
		addLessonsToChapter(level5Chap3, new String[][]{{"18", "0047_Iron_deficiency", "99140"}, {"19", "0048_VitaminA", "107550"}, {"25", "0049_Iodine", "107590"}});
	
		levelNodes.add(level5);
		hmParentNodeNameChildNodes.put("JobAidCourse", levelNodes);
		
		return hmParentNodeNameChildNodes;
	}

	
	private Node courseNode(String name, String menu, String duration) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("type", "Level");
		List<StringContent> content = Arrays.asList(new StringContent("hindi", "menu", menu, getMetaData(duration)));
		return new Node(name, data, content, new ArrayList<Node>());
	}

	private Node levelNode(String name, String number, String menu, String duration) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("type", "Level");
		data.put("number", number);
		List<StringContent> content = Arrays.asList(new StringContent("hindi", "menu", menu, getMetaData(duration)));
		return new Node(name, data, content, new ArrayList<Node>());
	}

	private Node chapterNode(String name, String number, String intro, String introDuration, String menu, String menuDuration) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("type", "Chapter");
		data.put("number", number);
		List<StringContent> content = Arrays.asList(new StringContent("hindi", "introduction", intro, getMetaData(introDuration)), new StringContent("hindi", "menu", menu, getMetaData(menuDuration)));
		return new Node(name, data, content, new ArrayList<Node>());
	}

	private Node lessonNode(String name, String number, String shortcode, String detail, String duration) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("type", "Lesson");
		data.put("number", number);
		data.put("shortcode", shortcode);
		List<StringContent> contents = Arrays.asList(new StringContent("hindi", "lesson", detail, getMetaData(duration)));
		return new Node(name, data, contents, new ArrayList<Node>());
	}

	private void addLessonsToChapter(Node chapter, String[][] lessons) {
		int i = 1;
		for (String lesson[] : lessons) {
			String name = chapter.getName() + " Lesson" + i;
			String number = i + "";
			String shortcode = lesson[0];
			String detail = "" + lesson[1] + ".wav";
			String duration = lesson[2];
			chapter.addChild(lessonNode(name, number, shortcode, detail, duration));
			i++;
		}
	}


	public Node makeLessonNode(String parentName, String[] lesson, int latestLessonNo) {

		String name = parentName + " Lesson" + latestLessonNo;
		String number = latestLessonNo + "";
		String shortcode = lesson[0];
		String detail = "" + lesson[1] + ".wav";
		String duration = lesson[2];
		//chapter.addChild(lessonNode(name, number, shortcode, detail, duration));	
		return lessonNode(name, number, shortcode, detail, duration);

	}


	public void addAdditionalLessonsToExistingChapter(Node chapter, String[][] lessons, int latestLessonNo) {
		for (String lesson[] : lessons) {
			String name = chapter.getName() + " Lesson" + latestLessonNo;
			String number = latestLessonNo + "";
			String shortcode = lesson[0];
			String detail = "" + lesson[1] + ".wav";
			String duration = lesson[2];
			chapter.addChild(lessonNode(name, number, shortcode, detail, duration));
			latestLessonNo++;
		}
	}

	private Map<String, String> getMetaData(String duration) {
		HashMap<String, String> metadata = new HashMap<String, String>();
		metadata.put("duration", duration);
		return metadata;
	}
}
