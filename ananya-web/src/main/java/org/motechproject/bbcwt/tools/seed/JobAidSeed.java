package org.motechproject.bbcwt.tools.seed;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Level;
import org.motechproject.bbcwt.domain.tree.Node;
import org.motechproject.bbcwt.repository.tree.AllNodes;
import org.motechproject.bbcwt.service.JobAidContentService;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.deliverytools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class JobAidSeed{
    @Autowired
    private AllNodes allNodes;
    @Seed(priority = 0)

    public void load() {
        MotechJsonReader jsonReader = new MotechJsonReader();
        Node jobAid = (Node)jsonReader.readFromFile("/seed/jobaid.json", Node.class);
        allNodes.addNodeWithDescendants(jobAid);
    }
}
