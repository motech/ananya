package org.motechproject.ananya.seed.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.domain.dimension.JobAidContentDetailsDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LanguageDimension;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllLanguageDimension;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobAidSeedService {

    @Autowired
    private AllJobAidContentDimensions allJobAidContentDimensions;
	@Autowired
	private AllJobAidContentDetailsDimensions allJobAidContentDetailsDimensions;
	@Autowired
	private AllLanguageDimension allLanguageDimension;
    @Autowired
    private AllNodes allNodes;
    @Autowired
    private AllStringContents allStringContents;

    public void saveTreeInCouchDB(Node courseNode) {
        allNodes.addNodeWithDescendants(courseNode);
    }

    public void saveJobAidTreeToReportDb(String language) {
		LanguageDimension languageDimension = new LanguageDimension();
		languageDimension = allLanguageDimension.getFor(language);	
		Node jobAidCourse = allNodes.findByName("JobAidCourse");
        recursivelyAddNodesToReportDB(jobAidCourse, null, languageDimension.getId());
    }

    private void recursivelyAddNodesToReportDB(Node node, JobAidContentDimension parentDimension, int languageId) {
        String nodeType = node.data().get("type");
        String prodShortCode = "57711";
        Long shortCode = -1L;

        JobAidContentDimension jobAidContentDimension = new JobAidContentDimension(node.getId(), parentDimension,
                node.getName(), node.data().get("type"));

        if (nodeType.equalsIgnoreCase("Lesson")) {
            shortCode = Long.valueOf(prodShortCode + node.data().get("shortcode"));
            jobAidContentDimension.setShortCode(shortCode);
        }
        allJobAidContentDimensions.add(jobAidContentDimension);

        for (StringContent content : node.contents()) {
            JobAidContentDimension audioContentDimension = new JobAidContentDimension(content.getId(), jobAidContentDimension, content.getName(), "Audio");
            if (nodeType.equalsIgnoreCase("Lesson"))
                audioContentDimension.setShortCode(shortCode);
            allJobAidContentDimensions.add(audioContentDimension);
			int duration = Integer.valueOf(content.getMetadata().get("duration"))!=null?Integer.valueOf(content.getMetadata().get("duration")):0;	
			JobAidContentDetailsDimension jobAidContentDetailsDimension = new JobAidContentDetailsDimension(languageId, content.getId(), content.getValue(), duration);
			allJobAidContentDetailsDimensions.add(jobAidContentDetailsDimension);
        }

        List<Node> children = node.children();
        if (children.isEmpty()) return;
        for (Node child : children)
            recursivelyAddNodesToReportDB(child, jobAidContentDimension, languageId);

    }

    public void updateNonChapterNodeDuration(String nodeName, String duration) {
        Node node = allNodes.findByName(nodeName);
        List<StringContent> contents = node.contents();
        if (contents == null || contents.isEmpty())
            return;
        StringContent stringContent = contents.get(0);
        stringContent.getMetadata().put("duration", duration);
        allStringContents.update(stringContent);
        allNodes.update(node);

        JobAidContentDimension jobAidContentDimension = allJobAidContentDimensions.findByContentId(stringContent.getId());
        allJobAidContentDimensions.update(jobAidContentDimension);
    }

	public void updatelessonNode(Node parentNode, String[] lesson, int lessonIndex) {
		String childName = parentNode.getName() + " Lesson" + lessonIndex;
		String number = lessonIndex + "";
		String shortcode = lesson[0];

		Node node = allNodes.findByName(childName);
		node.put("shortcode", shortcode);
		node.put("number", number);
		List<StringContent> contents = node.contents();
		if (contents == null || contents.isEmpty())
			return;
		StringContent stringContent = contents.get(0);
		allStringContents.update(stringContent);
		allNodes.update(node);
	}	

	public void saveNodesInCouchDBWithAdditionalDescents(Map<String, List<Node>> hmParentNodeNameChildNodes) {
		for(String parentNodeName: hmParentNodeNameChildNodes.keySet()){
			Node parentNode = allNodes.findByName(parentNodeName);
			List<Node> childNodes = new ArrayList<Node>(); 
			childNodes = hmParentNodeNameChildNodes.get(parentNodeName);
			for(Node node:childNodes){
				node.setParentId(parentNode.getId());
				allNodes.addNodeWithDescendants(node);
			}
		}
	}

	public void removeNode(String nodeName) {
		allNodes.remove(allNodes.findByName(nodeName));
	}

	public void updateLessonNodeForMK(){
		Node level4Chap4 = allNodes.findByName("Level 4 Chapter 4");
		updatelessonNode(level4Chap4, new String[]{"93", "0028_when_and_why_to_wash_hands", "106275"}, 3);
		updatelessonNode(level4Chap4, new String[]{"85", "0018_open_defecation_1", "124719"} , 4);
	}


	public void addAdditionalNodesToReportDbForMK(String language) {
		Node level1Chap5 = allNodes.findByName("Level 1 Chapter 5");
		Node level1Chap6 = allNodes.findByName("Level 1 Chapter 6");
		Node level2Chap5 = allNodes.findByName("Level 2 Chapter 5");
		Node level2Chap6 = allNodes.findByName("Level 2 Chapter 6");
		Node level3Chap5 = allNodes.findByName("Level 3 Chapter 5");
		Node level4Chap5 = allNodes.findByName("Level 4 Chapter 5");
		Node level3Chap4Le5 = allNodes.findByName("Level 3 Chapter 4 Lesson5");
		Node level3Chap4Le6 = allNodes.findByName("Level 3 Chapter 4 Lesson6");
		Node level4Chap4Le5= allNodes.findByName("Level 4 Chapter 4 Lesson5");
		
		LanguageDimension languageDimension = new LanguageDimension();
		languageDimension = allLanguageDimension.getFor(language);	
		for (Node level : Arrays.asList(level1Chap5, level1Chap6))
			recursivelyAddNodesToReportDB(level, allJobAidContentDimensions.findByContentId(allNodes.findByName("level 1").getId()), languageDimension.getId());
		for (Node level : Arrays.asList( level2Chap5, level2Chap6))
			recursivelyAddNodesToReportDB(level, allJobAidContentDimensions.findByContentId(allNodes.findByName("level 2").getId()), languageDimension.getId());
		recursivelyAddNodesToReportDB(level3Chap5, allJobAidContentDimensions.findByContentId(allNodes.findByName("level 3").getId()), languageDimension.getId());
		recursivelyAddNodesToReportDB(level4Chap5, allJobAidContentDimensions.findByContentId(allNodes.findByName("level 4").getId()), languageDimension.getId());
		for (Node level : Arrays.asList( level3Chap4Le5, level3Chap4Le6))
			recursivelyAddNodesToReportDB(level, allJobAidContentDimensions.findByContentId(allNodes.findByName("Level 3 Chapter 4").getId()), languageDimension.getId());
		recursivelyAddNodesToReportDB(level4Chap4Le5, allJobAidContentDimensions.findByContentId(allNodes.findByName("Level 4 Chapter 4").getId()), languageDimension.getId());
	}
	
	public void addAdditionalNodesToReportDbForMK(String language, String nodeName, String parentName) {
		Node node = allNodes.findByName(nodeName);	
		LanguageDimension languageDimension = new LanguageDimension();
		languageDimension = allLanguageDimension.getFor(language);	
		recursivelyAddNodesToReportDB(node, allJobAidContentDimensions.findByContentId(allNodes.findByName(parentName).getId()), languageDimension.getId());
		}
}
