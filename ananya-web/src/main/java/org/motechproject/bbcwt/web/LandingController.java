package org.motechproject.bbcwt.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LandingController {

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/jobaid/landing/")
    public ModelAndView forJobAid(HttpServletRequest request) {
        return modelAndView(
                request,
                "landing-jobaid",
                "/vxml/jobaid.vxml",
                "/vxml/jobaid/register");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/certificatecourse/landing/")
    public ModelAndView forCertificateCourse(HttpServletRequest request) {
        return modelAndView(
                request,
                "landing-certificate-course",
                "/vxml/certificatecourse.vxml",
                "/vxml/certificatecourse/register");
    }

    private ModelAndView modelAndView(HttpServletRequest request, String view, String nextFlow, String regFlow) {
        String contextPath = request.getContextPath();
        return new ModelAndView(view)
                .addObject("nextFlow", contextPath + nextFlow)
                .addObject("registerFlow", contextPath + regFlow)
                .addObject("callerData", "'" + contextPath + "/dynamic/js/caller_data.js?callerId=' + session.connection.remote.uri");
    }

}
