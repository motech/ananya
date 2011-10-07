package org.motechproject.bbcwt.ivr.action;

import org.apache.log4j.Logger;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class BaseAction implements IVRAction {
    protected static final Logger LOG = Logger.getLogger(BaseAction.class);
    public static final Character NO_INPUT = ' ';
    @Autowired
    protected IVRMessage messages;

    protected IVRResponseBuilder ivrResponseBuilder(HttpServletRequest request) {
        IVRResponseBuilder ivrResponseBuilder = (IVRResponseBuilder)request.getAttribute(IVR.Attributes.RESPONSE_BUILDER);
        if(ivrResponseBuilder == null) {
            ivrResponseBuilder = new IVRResponseBuilder();
            request.setAttribute(IVR.Attributes.RESPONSE_BUILDER, ivrResponseBuilder);
        }
        return ivrResponseBuilder;
    }

    protected IVRDtmfBuilder ivrDtmfBuilder(HttpServletRequest request) {
        IVRDtmfBuilder dtmfBuilder = (IVRDtmfBuilder)request.getAttribute(IVR.Attributes.DTMF_BUILDER);
        if(dtmfBuilder == null) {
            dtmfBuilder = new IVRDtmfBuilder();
            dtmfBuilder.withMaximumLengthOfResponse(1);
            dtmfBuilder.withTimeOutInMillis(ivrTimeout());
            request.setAttribute(IVR.Attributes.DTMF_BUILDER, dtmfBuilder);
        }
        return dtmfBuilder;
    }

    protected String absoluteFileLocation(String fileName) {
        return messages.get(IVRMessage.CONTENT_LOCATION) + fileName;
    }

    protected char ivrInput(IVRRequest ivrRequest) {
        String input = ivrRequest.getData();
        return input!=null && input.length() > 0 ? input.charAt(0) : NO_INPUT;
    }

    protected int ivrTimeout() {
        String timeout = messages.get("ivr.timeout");
        return Integer.parseInt(timeout);
    }

    protected String healthWorkerCallerIdFromSession(HttpSession session) {
        return (String) session.getAttribute(IVR.Attributes.CALLER_ID);
    }
}
