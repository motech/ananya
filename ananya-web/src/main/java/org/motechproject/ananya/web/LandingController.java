package org.motechproject.ananya.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LandingController {

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/{entry}/landing")
    public ModelAndView entryRouter(HttpServletRequest request, @PathVariable String entry) {

        String contextPath = request.getContextPath();
        String nextFlow = "jobaid".equals(entry) ?
                contextPath + "/vxml/jobaid/enter" :
                contextPath + "/vxml/certificatecourse/enter";

        return new ModelAndView("landing").addObject("nextFlow", nextFlow);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/jobaid/enter")
    public ModelAndView enterJobAid(HttpServletRequest request) {
        return modelAndView(
                request,
                "jobaid-entry",
                "/vxml/jobaid.vxml",
                "/vxml/register.vxml");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/certificatecourse/enter")
    public ModelAndView enterCertificateCourse(HttpServletRequest request) {
        return modelAndView(
                request,
                "certificate-course-entry",
                "/vxml/certificatecourse.vxml",
                "/vxml/register.vxml");
    }

    private ModelAndView modelAndView(HttpServletRequest request, String view, String nextFlow, String regFlow) {
        String contextPath = request.getContextPath();
        return new ModelAndView(view)
                .addObject("nextFlow", contextPath + nextFlow)
                .addObject("registerFlow", contextPath + regFlow)
                .addObject("callerData", "'" + contextPath + "/generated/js/dynamic/caller_data.js?callerId=' + session.connection.remote.uri")
                .addObject("entryJs", contextPath + "/js/entry/controller.js");
    }

}
