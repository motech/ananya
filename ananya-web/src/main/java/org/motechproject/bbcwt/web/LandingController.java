package org.motechproject.bbcwt.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LandingController {

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/{entry}/landing/")
    public ModelAndView getLandingPage(HttpServletRequest request, @PathVariable String entry) {
        String contextPath = request.getContextPath();
        String nextFlow = entry.equals("jobaid") ? contextPath + "/vxml/jobaid.vxml" : contextPath + "/vxml/certificationCourse.vxml";
        String registerFlow = contextPath + "/vxml/" + entry + "/register";
        return new ModelAndView("landing").addObject("nextFlow", nextFlow).addObject("registerFlow", registerFlow);
    }

}
    