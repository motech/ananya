package org.motechproject.bbcwt.ivr.action.inputhandler;

import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;

public interface KeyPressHandler {
    String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder);
}