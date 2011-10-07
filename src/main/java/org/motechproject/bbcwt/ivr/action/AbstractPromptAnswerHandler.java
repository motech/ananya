package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.action.inputhandler.KeyPressHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPromptAnswerHandler extends BaseAction {
    private KeyPressHandler invalidKeyPressResponseAction;
    private Map<Character, KeyPressHandler> keyPressHandlerMap = new HashMap<Character, KeyPressHandler>(4);

    public AbstractPromptAnswerHandler(IVRMessage messages) {
        this.messages = messages;
        this.invalidKeyPressResponseAction = invalidInputHandler();
        this.intializeKeyPressHandlerMap(keyPressHandlerMap);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        IVRContext.SessionAndIVRContextSynchronizer synchronizer = new IVRContext.SessionAndIVRContextSynchronizer();
        IVRContext ivrContext = synchronizer.buildIVRContext(session);

        char chosenOption = ivrInput(ivrRequest);
        KeyPressHandler keyPressResponseAction = determineActionToExecute(chosenOption);

        String forward = keyPressResponseAction.execute(chosenOption, ivrContext, ivrResponseBuilder(request), ivrDtmfBuilder(request));

        synchronizer.synchronizeSessionWithIVRContext(session, ivrContext);

        return forward;
    }

    private KeyPressHandler determineActionToExecute(char chosenOption) {
        KeyPressHandler responseAction = keyPressHandlerMap.get(chosenOption);
        return responseAction!=null?responseAction: invalidKeyPressResponseAction;
    }

    protected abstract KeyPressHandler invalidInputHandler();
    protected abstract void intializeKeyPressHandlerMap(Map<Character, KeyPressHandler> keyPressHandlerMap);
}