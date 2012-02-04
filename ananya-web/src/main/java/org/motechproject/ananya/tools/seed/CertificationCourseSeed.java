package org.motechproject.ananya.tools.seed;

import org.motechproject.ananya.domain.tree.Node;
import org.motechproject.ananya.repository.tree.AllNodes;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificationCourseSeed {
    @Autowired
    private AllNodes allNodes;

    @Seed(priority = 0)
    public void loadSeed(){
        MotechJsonReader jsonReader = new MotechJsonReader();
        Node jobAid = (Node)jsonReader.readFromFile("/seed/certificationCourse.json", Node.class);
        allNodes.addNodeWithDescendants(jobAid);
    }
}