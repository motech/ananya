package org.motechproject.bbcwt.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Scope(value = "prototype")
public class RegistrationController {

    private static final String REGISTRATION_VXML = "register-flw";

    @RequestMapping(method = {RequestMethod.GET}, value = "vxml/register/")
    public ModelAndView vxmlForRegistration() {
       return new ModelAndView(REGISTRATION_VXML);
    }

    
}
