package org.motechproject.bbcwt.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Scope(value = "prototype")
public class FLWController {

    private static final String PAGE = "some";

    @RequestMapping(method = {RequestMethod.GET}, value = "/status")
    public ModelAndView callFlow() {
       return new ModelAndView(PAGE);
    }


}
