package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String input = ivrRequest.getData();
        if(input.charAt(0) == '1') {
            ivrResponseBuilder(request).addPlayText(messages.get(IVRMessage.IVR_HELP));
            return "forward:/helpMenu";
        }
        return "forward:/chapter";
    }

}