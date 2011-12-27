package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(IntroductionAction.LOCATION)
public class IntroductionAction extends BaseAction {
    public static final String LOCATION = "/introduction";

    @Autowired
    public IntroductionAction(IVRMessage messages) {
        this.messages = messages;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRResponseBuilder responseBuilder= ivrResponseBuilder(request);
        responseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.BLANK_AUDIO_FILE)));
        responseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_WC_MESSAGE)));
        return "forward:" + PostIntroductionMenuAction.LOCATION;
    }
}