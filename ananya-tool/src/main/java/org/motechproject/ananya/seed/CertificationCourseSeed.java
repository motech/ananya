package org.motechproject.ananya.seed;

import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.seed.domain.CertificateCourseTree;
import org.motechproject.ananya.seed.service.CertificateCourseSeedService;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificationCourseSeed {

    @Autowired
    private AllNodes allNodes;
    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;
    @Autowired
    private AllStringContents allStringContents;
    @Autowired
    private CertificateCourseSeedService seedService;

    @Seed(priority = 0, version = "1.0", comment = "Create and persist the certificate course json-tree in couchdb, add corresponding dimensions to postgres")
    public void loadSeed() {
        Node courseTree = new CertificateCourseTree().build();
        seedService.saveCourseTreeInCouchDb(courseTree);
        seedService.saveCourseTreeInPostgres(courseTree);
    }

    @Seed(priority = 3, version = "1.2", comment = "Update the audio duration and parent node details in couchdb and postgres")
    public void loadAudioContentDetails() {
        seedService.updateAudioContentAndParentDetailsInCouchDbAndPostgres();
    }
}