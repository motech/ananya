package org.motechproject.bbcwt.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Controller
@Scope(value = "prototype")
public class RegistrationController {

    private static final String REGISTRATION_VXML = "register-flw";
    public static final String XML = "text/xml";

    @RequestMapping(method = {RequestMethod.GET}, value = "/flw/register/vxml/")
    public ModelAndView forNameAndLocation(HttpServletResponse response) {
        response.setContentType(XML);
        return new ModelAndView(REGISTRATION_VXML);
    }

    @RequestMapping(method = {RequestMethod.POST}, value = "/flw/register/")
    public void createNew() {
    }


}
