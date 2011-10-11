package org.motechproject.bbcwt.ivr.action;


import com.ozonetel.kookoo.CollectDtmf;
import org.motechproject.bbcwt.ivr.IVR;
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
public class EndOfQuizMenuAction extends BaseAction {

    public static final String LOCATION = "/endOfQuizMenu";

    @Autowired
    public EndOfQuizMenuAction(IVRMessage messages) {
        this.messages = messages;
    }

    @Override
    @RequestMapping(value= EndOfQuizMenuAction.LOCATION, method= RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        //TODO: what is to be played at the end of the quiz is dynamic. Will do for demo, but has to be modified.
        CollectDtmf endOfQuizDtmf = ivrDtmfBuilder(request).addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.END_OF_QUIZ_OPTIONS))).create();
        ivrResponseBuilder(request).withCollectDtmf(endOfQuizDtmf);
        request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, "/endOfQuizMenuAnswer");
        return ivrResponseBuilder(request).create().getXML();
    }
}