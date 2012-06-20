package org.motechproject.ananya.seed.service;

import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobAidSeedService {

    @Autowired
    private AllJobAidContentDimensions allJobAidContentDimensions;
    @Autowired
    private AllNodes allNodes;
    @Autowired
    private AllStringContents allStringContents;

    public void saveTreeInCouchDB(Node courseNode) {
        allNodes.addNodeWithDescendants(courseNode);
    }

    public void saveJobAidTreeToReportDb() {
        Node jobAidCourse = allNodes.findByName("JobAidCourse");
        recursivelyAddNodesToReportDB(jobAidCourse, null);
    }

    private void recursivelyAddNodesToReportDB(Node node, JobAidContentDimension parentDimension) {
        String nodeType = node.data().get("type");
        String prodShortCode = "57711";
        Long shortCode = -1L;

        JobAidContentDimension jobAidContentDimension = new JobAidContentDimension(node.getId(), parentDimension,
                node.getName(), null, node.data().get("type"), null);
        if (nodeType.equalsIgnoreCase("Lesson")) {
            shortCode = Long.valueOf(prodShortCode + node.data().get("shortcode"));
            jobAidContentDimension.setShortCode(shortCode);
        }
        allJobAidContentDimensions.add(jobAidContentDimension);

        for (StringContent content : node.contents()) {
            JobAidContentDimension audioContentDimension = new JobAidContentDimension(content.getId(), jobAidContentDimension, content.getName(),
                    content.getValue(), "Audio", Integer.valueOf(content.getMetadata().get("duration")));
            if (nodeType.equalsIgnoreCase("Lesson"))
                audioContentDimension.setShortCode(shortCode);
            allJobAidContentDimensions.add(audioContentDimension);
        }

        List<Node> children = node.children();
        if (children.isEmpty()) return;
        for (Node child : children)
            recursivelyAddNodesToReportDB(child, jobAidContentDimension);

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
        jobAidContentDimension.setDuration(Integer.parseInt(duration));
        allJobAidContentDimensions.update(jobAidContentDimension);
    }


}
