package org.motechproject.bbcwt.service;

import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.domain.tree.Node;
import org.motechproject.bbcwt.repository.tree.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobAidContentService {
    private JobAidCourseToTree courseToTree;
    private TreeToJobAidCourse treeToJobAidCourse;
    private NodeRepository nodeRepository;
    private JobAidCourse courseCache;

    @Autowired
    public JobAidContentService(JobAidCourseToTree courseToTree, TreeToJobAidCourse treeToJobAidCourse, NodeRepository nodeRepository) {
        this.courseToTree = courseToTree;
        this.treeToJobAidCourse = treeToJobAidCourse;
        this.nodeRepository = nodeRepository;
    }

    public void addCourse(JobAidCourse course) {
        courseCache = course;
        Node courseAsTree = courseToTree.transform(course);
        nodeRepository.add(courseAsTree);
    }

    public JobAidCourse getCourse(String courseName) {
        if(courseCache!=null) {
            return courseCache;
        }
        Node courseAsTree = nodeRepository.findByName(courseName);
        return courseCache = treeToJobAidCourse.transform(courseAsTree);
    }
}