package org.motechproject.bbcwt.web;

import org.motechproject.bbcwt.util.SessionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LandingController {
    private static final String LANDING_VXML = "landing";

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/{callFlow}/landing/")
    public ModelAndView getLandingPage(HttpServletRequest request, @PathVariable String callFlow) {
        String renderingPage = callFlow.equals("jobaid")?"/vxml/jobaid.vxml":"/vxml/certificationCourse.vxml";
        return new ModelAndView(LANDING_VXML).addObject("renderingPage", renderingPage);
    }

}
