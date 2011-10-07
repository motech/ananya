package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.action.inputhandler.KeyPressHandler;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/endOfQuizMenuAnswer")
public class EndOfQuizMenuAnswerAction extends AbstractPromptAnswerHandler {

    @Autowired
    public EndOfQuizMenuAnswerAction(IVRMessage messages) {
        super(messages);
    }

    protected void intializeKeyPressHandlerMap(final Map<Character, KeyPressHandler> keyPressHandlerMap) {
        keyPressHandlerMap.put('1', new StartQuizHandler());
        keyPressHandlerMap.put('2', new StartNextChapterHandler());
        keyPressHandlerMap.put('3', new RepeatLastChapterHandler());
        keyPressHandlerMap.put(NO_INPUT, new NoInputHandler());
    }

    @Override
    protected KeyPressHandler invalidInputHandler() {
        return new InvalidInputHandler();
    }

    private class StartQuizHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();
            return "forward:/startQuiz";
        }
    }

    private class StartNextChapterHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();
            return "forward:/startNextChapter";
        }
    }

    private class RepeatLastChapterHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();
            return "forward:/repeatLastChapter";
        }
    }

    private class NoInputHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.incrementNoInputCount();
            int allowedNumberOfNoInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_NO_INPUTS));

            if(ivrContext.getNoInputCount() > allowedNumberOfNoInputs) {
                ivrContext.resetNoInputCount();
                ivrContext.resetInvalidInputCount();
                return "forward:/startNextChapter";
            }

            return "forward:/endOfQuizMenu";
        }
    }

    private class InvalidInputHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.incrementInvalidInputCount();
            int allowedNumberOfInvalidInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_INVALID_INPUTS));

            if(ivrContext.getInvalidInputCount() > allowedNumberOfInvalidInputs) {
                ivrContext.resetNoInputCount();
                ivrContext.resetInvalidInputCount();
                return "forward:/startNextChapter";
            }

            ivrResponseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));
            return "forward:/endOfQuizMenu";
        }
    }
}
