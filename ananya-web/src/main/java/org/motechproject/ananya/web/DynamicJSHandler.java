package org.motechproject.ananya.web;

import org.motechproject.ananya.repository.tree.AllNodes;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/dynamic/js")
public class DynamicJSHandler {

    private AllNodes allNodes;
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public DynamicJSHandler(AllNodes allNodes, FrontLineWorkerService frontLineWorkerService) {
        this.allNodes = allNodes;
        this.frontLineWorkerService = frontLineWorkerService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/metadata.js")
    public ModelAndView serveMetaData(HttpServletResponse response) throws Exception {
        response.setContentType("application/javascript");
        return new ModelAndView("metadata");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/jobaid_course_data.js")
    @ResponseBody
    public String serveJobAidCourseData(HttpServletResponse response) throws Exception {
        response.setContentType("application/javascript");
        return String.format("var courseData = %s;", allNodes.nodeAsJson("JobAidCourse"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/certification_course_data.js")
    @ResponseBody
    public String serveCertificationCourseData(HttpServletResponse response) throws Exception {
        response.setContentType("application/javascript");
        return String.format("var courseData = %s;", allNodes.nodeAsJson("CertificationCourse"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/caller_data.js")
    public ModelAndView getCallerData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String msisdn = request.getParameter("callerId");
        response.setContentType("application/javascript");
        return new ModelAndView("caller_data")
                .addObject("bookmark", frontLineWorkerService.getBookmark(msisdn).asJson())
                .addObject("isCallerRegistered", frontLineWorkerService.isCallerRegistered(msisdn))
                .addObject("scoresByChapter", frontLineWorkerService.scoresByChapter(msisdn));
    }
}