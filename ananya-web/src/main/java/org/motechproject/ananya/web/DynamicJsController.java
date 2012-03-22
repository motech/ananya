package org.motechproject.ananya.web;

import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.JobAidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

@Controller
@RequestMapping(value = "/generated/js")
public class DynamicJsController {

    private static Logger log = LoggerFactory.getLogger(DynamicJsController.class);

    private Properties properties;
    private JobAidService jobAidService;
    private CertificateCourseService certificateCourseService;

    @Autowired
    public DynamicJsController(JobAidService jobAidService,
                               CertificateCourseService certificateCourseService,
                               @Qualifier("ananyaProperties") Properties properties) {
        this.certificateCourseService = certificateCourseService;
        this.properties = properties;
        this.jobAidService = jobAidService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/dynamic/jobaid/caller_data.js")
    public ModelAndView getCallerDataForJobAid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String msisdn = request.getParameter("callerId");
        String operator = request.getParameter("operator");
        response.setContentType("application/javascript");

        log.info("fetching caller data for: " + msisdn + " for operator: " + operator);

        JobAidCallerDataResponse callerData = jobAidService.createCallerData(msisdn, operator);

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

        log.info("fetching caller data for: " + msisdn + " for operator: " + operator);

        CertificateCourseCallerDataResponse callerData = certificateCourseService.createCallerData(msisdn, operator);

        return new ModelAndView("caller_data")
                .addObject("bookmark", callerData.getBookmark())
                .addObject("isCallerRegistered", callerData.isCallerRegistered())
                .addObject("scoresByChapter", callerData.getScoresByChapter());
    }
}