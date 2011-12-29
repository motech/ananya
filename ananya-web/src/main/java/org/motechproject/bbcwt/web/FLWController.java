package org.motechproject.bbcwt.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Scope(value = "prototype")
public class FLWController {

    @RequestMapping(method = {RequestMethod.GET}, value = "/status")
    @ResponseBody
    public String callFlow() {
        return "Hey hey";
    }


}
