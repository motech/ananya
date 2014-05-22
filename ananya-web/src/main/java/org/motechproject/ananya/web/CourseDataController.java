package org.motechproject.ananya.web;

import org.motechproject.ananya.repository.AllNodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/generated/js")
public class CourseDataController extends BaseAnanyaController {

    private static Logger log = LoggerFactory.getLogger(CourseDataController.class);

    private AllNodes allNodes;

    @Autowired
    public CourseDataController(AllNodes allNodes) {
        this.allNodes = allNodes;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/jobaid_course_data_without_levels.js")
    @ResponseBody
    public String serveJobAidCourseData(HttpServletResponse response) throws Exception {
        setContentType(response);

        log.info("fetching jobaid course data");
        return String.format("var courseData = %s;", allNodes.nodeWithoutChildrenAsJson("JobAidCourse"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/jobaid_level_data.js")
    @ResponseBody
    public String serveJobAidLevelData(HttpServletResponse response,
                                       @RequestParam Integer levelNumber) throws Exception {
        setContentType(response);

        log.info("fetching jobaid level data " + levelNumber);
        return String.format("courseData.children[%s] = %s", levelNumber - 1, allNodes.nodeAsJson("level " + levelNumber));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/certification_course_data_without_chapters.js")
    @ResponseBody
    public String serveCertificationCourseData(HttpServletResponse response) throws Exception {
        setContentType(response);

        log.info("fetching certificate course data");
        return String.format("var courseData = %s;", allNodes.nodeWithoutChildrenAsJson("CertificationCourse"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/certification_course_chapter.js")
    @ResponseBody
    public String serveCertificationCourseChapter(HttpServletResponse response,
                                                  @RequestParam Integer chapterNumber) throws Exception {
        setContentType(response);

        log.info("fetching certification chapter: " + chapterNumber);
        return String.format("courseData.children[%s] = %s", chapterNumber - 1, allNodes.nodeAsJson("Chapter " + chapterNumber));
    }

    private void setContentType(HttpServletResponse response) {
        response.setContentType("application/javascript");
    }
}
