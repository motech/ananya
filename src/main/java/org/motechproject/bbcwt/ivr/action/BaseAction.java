package org.motechproject.bbcwt.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseAction implements IVRAction {
    @Autowired
    protected IVRMessage messages;

    protected String responseWith(IVRRequest ivrRequest, String key) {
        String playText = messages.get(key);
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withPlayText(playText).create();
        return ivrResponse.getXML();
    }

    protected String hangUpResponseWith(IVRRequest ivrRequest, String key) {
        String playText = messages.get(key);
        Response ivrResponse = new IVRResponseBuilder().withSid(ivrRequest.getSid()).withPlayText(playText).withHangUp().create();
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
}
