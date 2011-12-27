package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class HelpEnabledAction extends BaseAction {
    public static final String HELP_HANDLER = "/helpHandler";

    public String helpHandler(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        if(ivrRequest.hasNoData()) {
            return "forward:" + interactionWhenNoHelpIsRequested(request);
        } else {
            ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.IVR_HELP)));
            return "forward:" + postHelpInteraction(request);
        }
    }


    protected abstract String postHelpInteraction(HttpServletRequest request);

    protected abstract String interactionWhenNoHelpIsRequested(HttpServletRequest request);

    protected String helpInteractionLocation(HttpServletRequest request) {
        return servletPath(request).concat(HELP_HANDLER);
    }

    protected String servletPath(HttpServletRequest request) {
        return request.getServletPath();
    }
}