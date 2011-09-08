package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.action.inputhandler.KeyPressHandler;
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
@RequestMapping("/endOfQuizMenuAnswer")
public class EndOfQuizMenuAnswerAction extends BaseAction {
    private Map<Character, KeyPressHandler> keyPressHandlerMap;
    private KeyPressHandler invalidKeyPressHandler;

    @Autowired
    public EndOfQuizMenuAnswerAction(IVRMessage messages) {
        this.messages = messages;
        intializeKeyPressHandlerMap();
        invalidKeyPressHandler = new InvalidInputHandler();
    }

    private void intializeKeyPressHandlerMap() {
        keyPressHandlerMap = new HashMap<Character, KeyPressHandler>();
        keyPressHandlerMap.put('1', new StartQuizHandler());
        keyPressHandlerMap.put('2', new StartNextChapterHandler());
        keyPressHandlerMap.put('3', new RepeatLastChapterHandler());
        keyPressHandlerMap.put(NO_INPUT, new NoInputHandler());
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        IVRContext.SessionAndIVRContextSynchronizer synchronizer = new IVRContext.SessionAndIVRContextSynchronizer();
        IVRContext ivrContext = synchronizer.buildIVRContext(session);

        char chosenOption = ivrInput(ivrRequest);
        KeyPressHandler keyPressResponseAction = determineActionToExecute(chosenOption);

        String forward = keyPressResponseAction.execute(chosenOption, ivrContext, ivrResponseBuilder(request));

        synchronizer.synchronizeSessionWithIVRContext(session, ivrContext);

        return forward;
    }

    private KeyPressHandler determineActionToExecute(char chosenOption) {
        KeyPressHandler responseAction = keyPressHandlerMap.get(chosenOption);
        return responseAction!=null?responseAction: invalidKeyPressHandler;
    }

    private class StartQuizHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            return "forward:/startQuiz";
        }
    }

    private class StartNextChapterHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            return "forward:/startNextChapter";
        }
    }

    private class RepeatLastChapterHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            return "forward:/repeatLastChapter";
        }
    }

    private class NoInputHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            return "forward:/endOfQuizMenu";
        }
    }

    private class InvalidInputHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            ivrResponseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));
            return "forward:/endOfQuizMenu";
        }
    }
}
