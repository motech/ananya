package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class HelpEnabledAction extends BaseAction {
    public static final String HELP_HANDLER = "/helpHandler";

    public String helpHandler(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        if(ivrRequest.hasNoData()) {
            return "forward:" + nextInteraction();
        } else {
            ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.IVR_HELP)));
            return "forward:" + request.getSession().getAttribute(IVR.Attributes.PREV_INTERACTION);
        }
    }

    protected abstract String nextInteraction();

    protected String helpInteractionLocation(HttpServletRequest request) {
        return servletPath(request).concat(HELP_HANDLER);
    }

    protected String servletPath(HttpServletRequest request) {
        return request.getServletPath();
    }
}