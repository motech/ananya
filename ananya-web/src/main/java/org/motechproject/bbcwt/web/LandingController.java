package org.motechproject.bbcwt.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LandingController {

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/{entry}/landing/")
    public ModelAndView getLandingPage(@PathVariable String entry) {
        String nextFlow = entry.equals("jobaid") ? "/vxml/jobaid.vxml" : "/vxml/certificationCourse.vxml";
        return new ModelAndView("landing").addObject("nextFlow", nextFlow).addObject("entry", entry);
    }

}
    