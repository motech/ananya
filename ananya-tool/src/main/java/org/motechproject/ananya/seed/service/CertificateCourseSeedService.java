package org.motechproject.ananya.seed.service;

import java.util.List;

import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.seed.domain.CertificateCourseItemAction;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateCourseSeedService {

    @Autowired
    private AllNodes allNodes;
    @Autowired
    private AllCourseItemDimensions allCourseItemDimensions;
    
    @Autowired
    private AllStringContents allStringContents;

    public void saveCourseTreeInCouchDb(Node certificateCourse) {
        allNodes.addNodeWithDescendants(certificateCourse);
    }

    public void saveCourseTreeInPostgres(Node certificateCourse) {
        recursivelySaveContentsInPostgres(certificateCourse);
    }

    public void updateAudioContentAndParentDetailsInCouchDbAndPostgres() {
        Node certificateCourse = allNodes.findByName("CertificationCourse");
        recursivelySaveAudioContentAndParentDetails(certificateCourse, null);
    }

    private void recursivelySaveContentsInPostgres(Node node) {
        CourseItemType type = CourseItemType.valueOf(node.data().get("type").toUpperCase());
        CourseItemDimension courseItemDimension = new CourseItemDimension(node.getName(), node.getId(), type, null);
        allCourseItemDimensions.add(courseItemDimension);

        List<Node> children = node.children();
        if (children.isEmpty()) return;

        if (type.equals(CourseItemType.CHAPTER))
            allCourseItemDimensions.add(new CourseItemDimension(node.getName(), node.getId(), CourseItemType.QUIZ, null));
        for (Node child : children)
            recursivelySaveContentsInPostgres(child);

    }

    private void recursivelySaveAudioContentAndParentDetails(Node node, CourseItemDimension parentDimension) {
        CourseItemType type = CourseItemType.valueOf(node.data().get("type").toUpperCase());
        CourseItemDimension courseItemDimension = allCourseItemDimensions.getFor(node.getName(), type);
        courseItemDimension.setParentDimension(parentDimension);
        allCourseItemDimensions.update(courseItemDimension);

        if (type.equals(CourseItemType.CHAPTER)) {
            courseItemDimension = allCourseItemDimensions.getFor(node.getName(), CourseItemType.QUIZ);
            courseItemDimension.setParentDimension(parentDimension);
            allCourseItemDimensions.update(courseItemDimension);
        }

        CertificateCourseItemAction.init(allNodes, allStringContents);
        CertificateCourseItemAction courseItemAction = CertificateCourseItemAction.findFor(type);
        node = courseItemAction.updateContents(node);

        /*
        * Since Certificate Course structure has no specific node for QUIZ, the chapter level node is to denote the
        * quiz. This places a unique constraint requirement on the combination of (name, type).
        * While adding audio files, this creates a problem since audio files typically have similar names such as
        * "lesson", "introduction", "menu" etc. To overcome this issue, we concatenate the name with the filename.
        *      eg., audio -> "lesson", "0024_ch1_4_ca.wav"
        *           name -> "lesson_0024_ch1_4_ca.wav"
        */
        for (StringContent content : node.contents()) {
            CourseItemDimension audioContentDimension = new CourseItemDimension(
                    content.getName() + ":" + content.getValue(),
                    content.getId(),
                    CourseItemType.AUDIO,
                    courseItemDimension
            );
            allCourseItemDimensions.add(audioContentDimension);
        }

        List<Node> children = node.children();
        if (children.isEmpty()) return;
        for (Node child : children) {
            recursivelySaveAudioContentAndParentDetails(child, courseItemDimension);
        }
    }

}
