package org.motechproject.ananya.web;

import org.motechproject.ananya.repository.AllNodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/generated/js")
public class CourseDataController {

    private static Logger log = LoggerFactory.getLogger(CourseDataController.class);

    private AllNodes allNodes;

    @Autowired
    public CourseDataController(AllNodes allNodes) {
        this.allNodes = allNodes;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/jobaid_course_data_without_levels.js")
    @ResponseBody
    public String serveJobAidCourseDataWithoutLevels(HttpServletResponse response) throws Exception {
        response.setContentType("application/javascript");

        log.info("Fetching JobAid course data");

        return String.format("var courseData = %s;", allNodes.nodeWithoutChildrenAsJson("JobAidCourse"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/jobaid_level_data.js")
    @ResponseBody
    public String serveJobAidLevelData(HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        String levelNumber = request.getParameter("levelNumber");

        log.info("Fetching JobAid level data " + levelNumber);

        response.setContentType("application/javascript");
        return String.format("courseData.children[%s] = %s", Integer.parseInt(levelNumber)-1, allNodes.nodeAsJson("level " + levelNumber));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/certification_course_data_without_chapters.js")
    @ResponseBody
    public String serveCertificationCourseData(HttpServletResponse response) throws Exception {
        response.setContentType("application/javascript");

        log.info("Fetching certificate course data");

        return String.format("var courseData = %s;", allNodes.nodeWithoutChildrenAsJson("CertificationCourse"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/certification_course_chapter.js")
    @ResponseBody
    public String serveCertificationCourseChapter(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String chapterNumber = request.getParameter("chapterNumber");

        log.info("Fetching Certification Chapter: " + chapterNumber);

        response.setContentType("application/javascript");
        return String.format("courseData.children[%s] = %s", Integer.parseInt(chapterNumber)-1, allNodes.nodeAsJson("Chapter " + chapterNumber));
    }
}
