package org.motechproject.bbcwt.ivr.jobaid;

import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;

public interface IVRAction {
    void processRequest(IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder);
    void playPrompt(IVRContext context, IVRRequest request, IVRDtmfBuilder dtmfBuilder);

    CallFlowExecutor.ProcessStatus validateInput(IVRContext context, IVRRequest request);

    IVRAction processAndForwardToNextState(IVRContext context, IVRRequest request);
}