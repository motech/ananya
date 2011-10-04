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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(PostIntroductionMenuAction.LOCATION)
public class PostIntroductionMenuAction extends BaseAction {
    public static final String LOCATION = "/postIntroductionMenu";

    @Autowired
    public PostIntroductionMenuAction(IVRMessage messages) {
        this.messages = messages;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRResponseBuilder responseBuilder= ivrResponseBuilder(request);
        IVRDtmfBuilder dtmfBuilder = ivrDtmfBuilder(request).withPlayAudio(absoluteFileLocation(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS)));
        responseBuilder.withCollectDtmf(dtmfBuilder.create());
        request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, PostIntroductionMenuAnswerAction.LOCATION);
        return responseBuilder.create().getXML();
    }
}