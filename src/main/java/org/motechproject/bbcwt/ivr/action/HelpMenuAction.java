package org.motechproject.bbcwt.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/helpMenu")
public class HelpMenuAction extends BaseAction {

    @Autowired
    public HelpMenuAction(IVRMessage messages) {
        this.messages = messages;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRDtmfBuilder dtmfBuilder = ivrDtmfBuilder(request).withPlayText(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS));
        IVRResponseBuilder responseBuilder = ivrResponseBuilder(request).withCollectDtmf(dtmfBuilder.create());
        request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, "/helpMenuAnswer");

        return responseBuilder.create().getXML();
    }
}