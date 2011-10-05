package org.motechproject.bbcwt.ivr.action.inputhandler;

import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;

public class PlayHelpAction implements KeyPressHandler {
    private String forwardTo;
    private IVRMessage messages;

    public PlayHelpAction(IVRMessage messages, String forwardTo) {
        this.forwardTo = forwardTo;
        this.messages = messages;
    }

    @Override
    public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
        ivrResponseBuilder.addPlayAudio(messages.absoluteFileLocation(messages.get(IVRMessage.IVR_HELP)));
        return forwardTo;
    }
}