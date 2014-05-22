package org.motechproject.ananya.seed;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.seed.domain.JobAidTree;
import org.motechproject.ananya.seed.service.JobAidSeedService;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobAidSeed {

    @Autowired
    private JobAidSeedService seedService;

    @Seed(priority = 0, version = "1.0", comment = "Create and persist the json-tree jobaid data structure in couchdb")
    public void createJobAidCourseStructure() {
        seedService.saveTreeInCouchDB(new JobAidTree().build());
    }

    @Seed(priority = 2, version = "1.2", comment = "Create jobaid content dimension in postgres in synch with course structure in couchdb in the bhojpuri for bihar")
    public void addJobAidContentDimensions() {
		String language = "bhojpuri";
		seedService.saveJobAidTreeToReportDb(language);
    }

    @Seed(priority = 0, version = "1.3", comment = "Corrections for BBC updating 0022_iud.wav")
    public void accommodateForUpdatedAudioFile0022_iud() {
        seedService.updateNonChapterNodeDuration("Level 3 Chapter 2 Lesson2","138742");
        seedService.updateNonChapterNodeDuration("Level 4 Chapter 2 Lesson2","138742");
    }

	@Seed(priority = 0, version = "1.15", comment = "Create and persist the json-tree jobaid data structure with additional lessons in couchdb")
	public void addAdditionalJobAidCourseStructure() {
		Map<String, List<Node>> hmParentNodeNameChildNodes = new HashMap<String, List<Node>>();
		JobAidTree jobAidTree = new JobAidTree();
		hmParentNodeNameChildNodes=jobAidTree.buildAdditionalChaptersForMK(hmParentNodeNameChildNodes);   
		hmParentNodeNameChildNodes=jobAidTree.buildAdditionalLessonsForMK(hmParentNodeNameChildNodes); 
		seedService.saveNodesInCouchDBWithAdditionalDescents(hmParentNodeNameChildNodes);
		for (String nodeName : Arrays.asList("Level 2 Chapter 4 Lesson3", "Level 2 Chapter 4 Lesson4"))
			seedService.removeNode(nodeName);
		seedService.updateLessonNodeForMK();
	}

	@Seed(priority = 0, version = "1.16", comment = "Create and persist the json-tree jobaid data structure with additional lessons in reportDb for odia langauge")
	public void addJobAidCourseStructureToReportDb() {
		String language = "odia";
		seedService.addAdditionalNodesToReportDbForMK(language);
	}
	
	@Seed(priority = 0, version = "1.17", comment = "Create and persist the json-tree jobaid data structure with additional chapter for ICDS ")
	public void addICDSJobAidCourseStructure() {
		Map<String, List<Node>> hmParentNodeNameChildNodes = new HashMap<String, List<Node>>();
		JobAidTree jobAidTree = new JobAidTree();
		hmParentNodeNameChildNodes=jobAidTree.buildAdditionalLevelForMKICDS(hmParentNodeNameChildNodes);   
		seedService.saveNodesInCouchDBWithAdditionalDescents(hmParentNodeNameChildNodes);	
	}
	
	@Seed(priority = 0, version = "1.18", comment = "Create and persist the json-tree jobaid data structure with additional lessons in reportDb for ICDS")
	public void addICDSJobAidCourseStructureToReportDb() {
		String language = "bhojpuri";
		seedService.addAdditionalNodesToReportDbForMK(language,"level 5","JobAidCourse");
	}
	
	
}
