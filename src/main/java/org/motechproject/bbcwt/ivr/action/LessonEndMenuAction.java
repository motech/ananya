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
@RequestMapping("/lessonEndMenu")
public class LessonEndMenuAction extends BaseAction {

    @Autowired
    public LessonEndMenuAction(IVRMessage messages) {
        this.messages = messages;
    }

    //TODO: LessonEndMenuAction should handle the last lesson in the chapter.
    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        LOG.info("In here to render end of lesson menu...");
        IVRDtmfBuilder dtmfBuilder = ivrDtmfBuilder(request).withPlayText(messages.get(IVRMessage.END_OF_LESSON_MENU));
        IVRResponseBuilder responseBuilder = ivrResponseBuilder(request).withCollectDtmf(dtmfBuilder.create());
        request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, "/lessonEndAnswer");
        LOG.info("Rendering end of lesson menu now.");
        return responseBuilder.create().getXML();
    }
}