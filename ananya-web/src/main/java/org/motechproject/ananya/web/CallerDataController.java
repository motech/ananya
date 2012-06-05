package org.motechproject.ananya.web;

import org.motechproject.ananya.domain.CallerIdParam;
import org.motechproject.ananya.response.CertificateCourseCallerDataResponse;
import org.motechproject.ananya.response.JobAidCallerDataResponse;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.JobAidService;
import org.motechproject.ananya.service.RegistrationLogService;
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
public class CallerDataController extends BaseAnanyaController {

    private static Logger log = LoggerFactory.getLogger(CallerDataController.class);

    private Properties properties;
    private JobAidService jobAidService;
    private CertificateCourseService certificateCourseService;
    private RegistrationLogService registrationLogService;

    @Autowired
    public CallerDataController(JobAidService jobAidService,
                                CertificateCourseService certificateCourseService,
                                RegistrationLogService registrationLogService,
                                @Qualifier("ananyaProperties") Properties properties) {
        this.certificateCourseService = certificateCourseService;
        this.properties = properties;
        this.jobAidService = jobAidService;
        this.registrationLogService = registrationLogService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/dynamic/jobaid/caller_data.js")
    public ModelAndView getCallerDataForJobAid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String msisdn = new CallerIdParam(request.getParameter("callerId")).getValue();
        String operator = request.getParameter("operator");
        String circle = request.getParameter("circle");
        response.setContentType("application/javascript");

        log.info("fetching caller data for: " + msisdn + " for operator: " + operator + " for circle: " + circle);
        JobAidCallerDataResponse callerData = jobAidService.createCallerData(msisdn, operator, circle);

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
        response.setContentType("application/javascript");

        log.info("fetching caller data for: " + msisdn + " for operator: " + operator + " for circle" + circle);
        CertificateCourseCallerDataResponse callerData = certificateCourseService.createCallerData(msisdn, operator, circle);

        return new ModelAndView("caller_data")
                .addObject("bookmark", callerData.getBookmark())
                .addObject("isCallerRegistered", callerData.isCallerRegistered())
                .addObject("scoresByChapter", callerData.getScoresByChapter());
    }

}