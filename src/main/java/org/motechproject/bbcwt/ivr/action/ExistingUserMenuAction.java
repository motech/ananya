package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/existingUserMenu")
public class ExistingUserMenuAction extends BaseAction{

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return ivrResponseBuilder(request).addPlayText("Still building the new user flow.").withHangUp().create().getXML();
    }
}