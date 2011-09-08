package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/helpMenuAnswer")
public class HelpMenuAnswerAction extends BaseAction {

    private Map<Character, KeyPressResponseAction> keyPressActionMap;
    private InvalidKeyPressResponseAction invalidKeyPressResponseAction;


    @Autowired
    public HelpMenuAnswerAction(IVRMessage messages) {
        this.messages = messages;
        this.invalidKeyPressResponseAction = new InvalidKeyPressResponseAction(messages);
        initializeKeyPressActionMap();
    }

    private void initializeKeyPressActionMap() {
        keyPressActionMap = new HashMap(3);
        keyPressActionMap.put('1', new Key1ResponseAction());
        keyPressActionMap.put('2', new Key2ResponseAction());
        keyPressActionMap.put(NO_INPUT, new NoKeyPressResponseAction(messages));
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        IVRContext.SessionAndIVRContextSynchronizer synchronizer = new IVRContext.SessionAndIVRContextSynchronizer();
        IVRContext ivrContext = synchronizer.buildIVRContext(session);

        char chosenOption = ivrInput(ivrRequest);
        KeyPressResponseAction keyPressResponseAction = determineActionToExecute(chosenOption);

        String forward = keyPressResponseAction.execute(ivrContext, ivrResponseBuilder(request), ivrDtmfBuilder(request));

        synchronizer.synchronizeSessionWithIVRContext(session, ivrContext);

        return forward;
    }

    private KeyPressResponseAction determineActionToExecute(char chosenOption) {
        KeyPressResponseAction responseAction = keyPressActionMap.get(chosenOption);
        return responseAction!=null?responseAction: invalidKeyPressResponseAction;
    }

    class Key1ResponseAction implements KeyPressResponseAction {
        @Override
        public String execute(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();
            return "forward:/chapter/1/lesson/1";
        }
    }

    class Key2ResponseAction implements KeyPressResponseAction {
        @Override
        public String execute(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();
            ivrResponseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.IVR_HELP)));
            return "forward:/helpMenu";
        }
    }

    class NoKeyPressResponseAction implements KeyPressResponseAction {
        private IVRMessage messages;
        private int allowedNumberOfNoInputs;

        public NoKeyPressResponseAction(IVRMessage messages) {
            this.messages = messages;
            this.allowedNumberOfNoInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_NO_INPUTS));
        }

        @Override
        public String execute(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.incrementNoInputCount();

            if(ivrContext.getNoInputCount() > allowedNumberOfNoInputs) {
                ivrContext.resetInvalidInputCount();
                ivrContext.resetNoInputCount();
                return "forward:/chapter/1/lesson/1";
            }

            return "forward:/helpMenu";
        }
    }

    class InvalidKeyPressResponseAction implements KeyPressResponseAction {
        private IVRMessage messages;
        private int allowedNumberOfInvalidInputs;

        public InvalidKeyPressResponseAction(IVRMessage messages) {
            this.messages = messages;
            this.allowedNumberOfInvalidInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_INVALID_INPUTS));
        }

        @Override
        public String execute(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.incrementInvalidInputCount();

            if(ivrContext.getInvalidInputCount() > allowedNumberOfInvalidInputs) {
                ivrContext.resetInvalidInputCount();
                ivrContext.resetNoInputCount();
                return "forward:/chapter/1/lesson/1";
            }

            ivrResponseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));
            return "forward:/helpMenu";
        }
    }
}

interface KeyPressResponseAction {
    String execute(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder);
}

