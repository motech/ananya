package org.motechproject.ananya.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

@Controller
public class LandingController {

    private Properties properties;

    @Autowired
    public LandingController(@Qualifier("ananyaProperties") Properties properties) {
        this.properties = properties;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/vxml/{entry}/landing")
    public ModelAndView entryRouter(HttpServletRequest request, @PathVariable String entry) {

        String nextFlow = "jobaid".equals(entry) ?
                "vxml/jobaid/enter/" :
                "vxml/certificatecourse/enter/";
        return new ModelAndView("landing").addObject("nextFlow", nextFlow);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/jobaid/enter")
    public ModelAndView enterJobAid(HttpServletRequest request) {
        String urlVersion = urlVersion();
        return modelAndView(
                request,
                "jobaid-entry",
                urlVersion + "/vxml/jobaid.vxml",
                urlVersion + "/vxml/register.vxml");
    }

    private String urlVersion() {
        return properties.getProperty("url.version");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/vxml/certificatecourse/enter")
    public ModelAndView enterCertificateCourse(HttpServletRequest request) {
        String urlVersion = urlVersion();
        return modelAndView(
                request,
                "certificate-course-entry",
                urlVersion + "/vxml/certificatecourse.vxml",
                urlVersion + "/vxml/register.vxml");
    }

    private ModelAndView modelAndView(HttpServletRequest request, String view, String nextFlow, String regFlow) {
        return new ModelAndView(view)
                .addObject("nextFlow", nextFlow)
                .addObject("registerFlow", regFlow)
                .addObject("callerData", "'" + urlVersion() + "/generated/js/dynamic/caller_data.js?callerId=' + session.connection.remote.uri")
                .addObject("entryJs", urlVersion() + "/js/entry/controller.js");
    }

}
