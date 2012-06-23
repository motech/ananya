package org.motechproject.ananya.web;

import org.motechproject.ananya.domain.CallerIdParam;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.JobAidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/generated/js")
public class CallerDataController {

    private static Logger log = LoggerFactory.getLogger(CallerDataController.class);

    private JobAidService jobAidService;
    private CertificateCourseService certificateCourseService;

    @Autowired
    public CallerDataController(JobAidService jobAidService, CertificateCourseService certificateCourseService) {
        this.certificateCourseService = certificateCourseService;
        this.jobAidService = jobAidService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/dynamic/jobaid/caller_data.js")
    public ModelAndView getCallerDataForJobAid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String msisdn = new CallerIdParam(request.getParameter("callerId")).getValue();
        String operator = request.getParameter("operator");
        String circle = request.getParameter("circle");
        String callId = request.getParameter("callId");
        response.setContentType("application/javascript");

        log.info(callId + "- fetching caller data for jobaid");
        JobAidCallerDataResponse callerData = jobAidService.createCallerData(callId, msisdn, operator, circle);

        return new ModelAndView("job_aid_caller_data")
                .addObject("isCallerRegistered", callerData.isCallerRegistered())
                .addObject("currentJobAidUsage", callerData.getCurrentJobAidUsage())
                .addObject("maxAllowedUsageForOperator", callerData.getMaxAllowedUsageForOperator())
                .addObject("promptsHeard", callerData.getPromptsHeard());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/dynamic/caller_data.js")
    public ModelAndView getCallerData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String msisdn = new CallerIdParam(request.getParameter("callerId")).getValue();
        String operator = request.getParameter("operator");
        String circle = request.getParameter("circle");
        String callId = request.getParameter("callId");
        response.setContentType("application/javascript");

        log.info(callId + "- fetching caller data for course");
        CertificateCourseCallerDataResponse callerData = certificateCourseService.createCallerData(callId, msisdn, operator, circle);

        return new ModelAndView("caller_data")
                .addObject("bookmark", callerData.getBookmark())
                .addObject("isCallerRegistered", callerData.isCallerRegistered())
                .addObject("scoresByChapter", callerData.getScoresByChapter());
    }
}