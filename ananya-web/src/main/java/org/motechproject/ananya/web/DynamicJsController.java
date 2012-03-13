package org.motechproject.ananya.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.response.CallerDataResponse;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

@Controller
@RequestMapping(value = "/generated/js")
public class DynamicJsController {

    private static Logger log = LoggerFactory.getLogger(DynamicJsController.class);

    private AllNodes allNodes;
    private FrontLineWorkerService frontLineWorkerService;
    private Properties properties;

    @Autowired
    public DynamicJsController(AllNodes allNodes, FrontLineWorkerService frontLineWorkerService, @Qualifier("ananyaProperties") Properties properties) {
        this.allNodes = allNodes;
        this.frontLineWorkerService = frontLineWorkerService;
        this.properties = properties;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/metadata.js")
    public ModelAndView serveMetaData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/javascript");
        String operator = operatorFromURL(request);

        return new ModelAndView("metadata" + operator).
                addObject("urlVersion", properties.getProperty("url.version")).
                addObject("contextPath", request.getContextPath());
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

    @RequestMapping(method = RequestMethod.GET, value = "/dynamic/jobaid/caller_data.js")
    public ModelAndView getCallerDataForJobAid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String msisdn = request.getParameter("callerId");
        String operator = request.getParameter("operator");
        response.setContentType("application/javascript");
        log.info("fetching caller data for: " + msisdn);

        JobAidCallerDataResponse callerData = frontLineWorkerService.createJobAidCallerData(msisdn, operator);
        return new ModelAndView("job_aid_caller_data")
                .addObject("isCallerRegistered", callerData.isCallerRegistered())
                .addObject("currentJobAidUsage", callerData.getCurrentJobAidUsage())
                .addObject("maxAllowedUsageForOperator", callerData.getMaxAllowedUsageForOperator())
                .addObject("promptsHeard", callerData.getPromptsHeard());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/dynamic/caller_data.js")
    public ModelAndView getCallerData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String msisdn = request.getParameter("callerId");
        String operator = request.getParameter("operator");
        response.setContentType("application/javascript");
        log.info("fetching caller data for: " + msisdn);

        CallerDataResponse callerData = frontLineWorkerService.createCallerData(msisdn, operator);
        return new ModelAndView("caller_data")
                .addObject("bookmark", callerData.getBookmark())
                .addObject("isCallerRegistered", callerData.isCallerRegistered())
                .addObject("scoresByChapter", callerData.getScoresByChapter());
    }

    private String operatorFromURL(HttpServletRequest request) {
        String urlVersion = properties.getProperty("url.version");
        String currentPath = "/" + urlVersion;
        boolean versionedUrl = request.getServletPath().contains(urlVersion);

        String operator = "";
        if (versionedUrl) { 
            operator = StringUtils.remove(request.getServletPath(), currentPath);
            if (!operator.isEmpty()) operator = operator.substring(1);
        }
        return operator;
    }
}