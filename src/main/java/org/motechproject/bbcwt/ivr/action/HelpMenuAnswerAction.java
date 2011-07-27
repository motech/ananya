package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/helpMenuAnswer")
public class HelpMenuAnswerAction extends BaseAction {

    @Autowired
    public HelpMenuAnswerAction(IVRMessage messages) {
        this.messages = messages;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String input = ivrRequest.getData();
        if(input.charAt(0) == '1') {
            return responseWith(ivrRequest, "msg.help");
        }
        return responseWith(ivrRequest, "content.chapter1");
    }

}
