package org.motechproject.bbcwt.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.apache.log4j.Logger;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseAction implements IVRAction {
    protected static final Logger LOG = Logger.getLogger(BaseAction.class);
    @Autowired
    protected IVRMessage messages;

    protected String responseWith(IVRRequest ivrRequest, String key) {
        String playText = messages.get(key);
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).addPlayText(playText).create();
        return ivrResponse.getXML();
    }

    protected String hangUpResponseWith(IVRRequest ivrRequest, String key) {
        String playText = messages.get(key);
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).addPlayText(playText).withHangUp().create();
        return ivrResponse.getXML();
    }

    protected String dtmfResponseWith(IVRRequest ivrRequest, String key) {
        String playText = messages.get(key);
        CollectDtmf collectDtmf = new IVRDtmfBuilder().withPlayText(playText).create();
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withCollectDtmf(collectDtmf).create();
        return ivrResponse.getXML();
    }

    protected String dtmfResponseWithWav(IVRRequest ivrRequest, String key) {
        String playAudio = messages.get(key);
        CollectDtmf collectDtmf = new IVRDtmfBuilder().withPlayAudio(playAudio).create();
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withCollectDtmf(collectDtmf).create();
        return ivrResponse.getXML();
    }

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
            dtmfBuilder.withTimeOutInMillis(6000000);
            request.setAttribute(IVR.Attributes.DTMF_BUILDER, dtmfBuilder);
        }
        return dtmfBuilder;
    }
}
