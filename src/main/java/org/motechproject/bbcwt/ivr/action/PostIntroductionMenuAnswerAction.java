package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.action.inputhandler.KeyPressHandler;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping(PostIntroductionMenuAnswerAction.LOCATION)
public class PostIntroductionMenuAnswerAction extends AbstractPromptAnswerHandler {
    public static final String LOCATION = "/postIntroductionMenuAnswerAction";

    @Autowired
    public PostIntroductionMenuAnswerAction(IVRMessage messages) {
        super(messages);
    }

    @Override
    protected KeyPressHandler invalidInputHandler() {
        return new InvalidKeyPressResponseAction();
    }

    @Override
    protected void intializeKeyPressHandlerMap(final Map<Character, KeyPressHandler> keyPressHandlerMap) {
        keyPressHandlerMap.put('1', new RepeatIntroduction());
        keyPressHandlerMap.put('2', new StartChapterAction());
        keyPressHandlerMap.put(NO_INPUT, new NoKeyPressResponseAction());
    }

    class StartChapterAction implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();
            return "forward:/chapter/1/lesson/1";
        }
    }

    class RepeatIntroduction implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();
            return "forward:" + IntroductionAction.LOCATION;
        }
    }

    class NoKeyPressResponseAction implements KeyPressHandler {
        private int allowedNumberOfNoInputs;

        public NoKeyPressResponseAction() {
            this.allowedNumberOfNoInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_NO_INPUTS));
        }

        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            ivrContext.incrementNoInputCount();

            if(ivrContext.getNoInputCount() > allowedNumberOfNoInputs) {
                ivrContext.resetInvalidInputCount();
                ivrContext.resetNoInputCount();
                return "forward:/chapter/1/lesson/1";
            }

            return "forward:" + PostIntroductionMenuAction.LOCATION;
        }
    }

    class InvalidKeyPressResponseAction implements KeyPressHandler {
        private int allowedNumberOfInvalidInputs;

        public InvalidKeyPressResponseAction() {
            this.allowedNumberOfInvalidInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_INVALID_INPUTS));
        }

        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            ivrContext.incrementInvalidInputCount();

            if(ivrContext.getInvalidInputCount() > allowedNumberOfInvalidInputs) {
                ivrContext.resetInvalidInputCount();
                ivrContext.resetNoInputCount();
                return "forward:/chapter/1/lesson/1";
            }

            ivrResponseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));
            return "forward:" + PostIntroductionMenuAction.LOCATION;
        }
    }
}

