package org.motechproject.ananya.seed;

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

    @Seed(priority = 2, version = "1.2", comment = "Create jobaid content dimension in postgres in synch with course structure in couchdb")
    public void addJobAidContentDimensions() {
        seedService.saveJobAidTreeToReportDb();
    }

    @Seed(priority = 0, version = "1.3", comment = "Corrections for BBC updating 0022_iud.wav")
    public void accommodateForUpdatedAudioFile0022_iud() {
        seedService.updateNonChapterNodeDuration("Level 3 Chapter 2 Lesson2","138742");
        seedService.updateNonChapterNodeDuration("Level 4 Chapter 2 Lesson2","138742");
    }


}
