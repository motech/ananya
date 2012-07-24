package org.motechproject.ananya.web;

import org.motechproject.ananya.contract.CertificateCourseServiceRequest;
import org.motechproject.ananya.contract.JobAidServiceRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView getCallerDataForJobAid(HttpServletResponse response,
                                               @RequestParam String callId,
                                               @RequestParam String callerId,
                                               @RequestParam String operator,
                                               @RequestParam String circle) throws Exception {

        JobAidServiceRequest jobAidServiceRequest = new JobAidServiceRequest(callId, callerId).
                withCircle(circle).withOperator(operator);

        JobAidCallerDataResponse callerData = jobAidService.getCallerData(jobAidServiceRequest);
        log.info(callId + "- fetched caller data for jobaid");

        setContentType(response);
        return new ModelAndView("job_aid_caller_data")
                .addObject("isCallerRegistered", callerData.isCallerRegistered())
                .addObject("currentJobAidUsage", callerData.getCurrentJobAidUsage())
                .addObject("maxAllowedUsageForOperator", callerData.getMaxAllowedUsageForOperator())
                .addObject("promptsHeard", callerData.getPromptsHeard());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/dynamic/caller_data.js")
    public ModelAndView getCallerDataForCourse(HttpServletResponse response,
                                               @RequestParam String callId,
                                               @RequestParam String callerId,
                                               @RequestParam String operator,
                                               @RequestParam String circle) throws Exception {

        CertificateCourseServiceRequest certificateCourseServiceRequest = new CertificateCourseServiceRequest(callId, callerId)
                .withCircle(circle).withOperator(operator);

        CertificateCourseCallerDataResponse callerData = certificateCourseService.createCallerData(certificateCourseServiceRequest);
        log.info(callId + "- fetched caller data for course");

        setContentType(response);
        return new ModelAndView("caller_data")
                .addObject("bookmark", callerData.getBookmark())
                .addObject("isCallerRegistered", callerData.isCallerRegistered())
                .addObject("scoresByChapter", callerData.getScoresByChapter());
    }

    private void setContentType(HttpServletResponse response) {
        response.setContentType("application/javascript");
    }
}