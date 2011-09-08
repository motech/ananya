package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.domain.Question;
import org.motechproject.bbcwt.domain.ReportCard;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.action.inputhandler.KeyPressHandler;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.repository.ReportCardsRepository;
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
@RequestMapping("/collectAnswer")
public class CollectAnswerAction extends BaseAction {
    private MilestonesRepository milestonesRepository;
    private ReportCardsRepository reportCardsRepository;

    private Map<Character, KeyPressHandler> keyPressHandlerMap;
    private KeyPressHandler invalidKeyPressResponseAction;

    @Autowired
    public CollectAnswerAction(MilestonesRepository milestonesRepository, ReportCardsRepository reportCardsRepository, IVRMessage messages) {
        this.milestonesRepository = milestonesRepository;
        this.reportCardsRepository = reportCardsRepository;
        this.messages = messages;
        invalidKeyPressResponseAction = new InvalidInputHandler();
        intializeKeyPressHandlerMap();
    }

    private void intializeKeyPressHandlerMap() {
        keyPressHandlerMap = new HashMap<Character, KeyPressHandler>();
        keyPressHandlerMap.put('1', new ValidAnswerHandler());
        keyPressHandlerMap.put('2', new ValidAnswerHandler());
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
        return responseAction!=null?responseAction: invalidKeyPressResponseAction;
    }

    private String forwardToQuestion(int chapterNumber, int questionNumber) {
        return "forward:/chapter/"+chapterNumber+"/question/"+ questionNumber;
    }

    private class ValidAnswerHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            String callerId = ivrContext.getCallerId();

            milestonesRepository.markLastMilestoneFinish(callerId);

            Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);
            Chapter currentChapter = milestone.getChapter();
            Question lastQuestion = currentChapter.getQuestionById(milestone.getQuestionId());

            ReportCard.HealthWorkerResponseToQuestion healthWorkerResponseToQuestion = reportCardsRepository.addUserResponse(callerId, currentChapter.getNumber(), lastQuestion.getNumber(), Character.getNumericValue(keyPressed));

            if(healthWorkerResponseToQuestion.isCorrect()) {
                ivrResponseBuilder.addPlayAudio(absoluteFileLocation(lastQuestion.getCorrectAnswerExplanationLocation()));
            }
            else {
                ivrResponseBuilder.addPlayAudio(absoluteFileLocation(lastQuestion.getIncorrectAnswerExplanationLocation()));
            }

            int nextQuestionNumber = lastQuestion.getNumber() + 1;

            ivrContext.resetNoInputCount();
            ivrContext.resetInvalidInputCount();

            if(currentChapter.getQuestionByNumber(nextQuestionNumber) == null){
                return "forward:/informScore";
            }
            else{
                return forwardToQuestion(currentChapter.getNumber(), nextQuestionNumber);
            }
        }
    }

    private class NoInputHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            ivrContext.incrementNoInputCount();
            int allowedNumberOfNoInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_NO_INPUTS));

            if(ivrContext.getNoInputCount() > allowedNumberOfNoInputs) {
                ivrContext.resetNoInputCount();
                ivrContext.resetInvalidInputCount();
                return "forward:/startNextChapter";
            }

            String callerId = ivrContext.getCallerId();
            Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);

            Chapter currentChapter = milestone.getChapter();
            Question lastQuestion = currentChapter.getQuestionById(milestone.getQuestionId());

            return forwardToQuestion(currentChapter.getNumber(), lastQuestion.getNumber());
        }
    }

    private class InvalidInputHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            ivrContext.incrementInvalidInputCount();
            int allowedNumberOfInvalidInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_INVALID_INPUTS));

            if(ivrContext.getInvalidInputCount() > allowedNumberOfInvalidInputs) {
                ivrContext.resetNoInputCount();
                ivrContext.resetInvalidInputCount();
                return "forward:/startNextChapter";
            }

            String callerId = ivrContext.getCallerId();
            Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);

            Chapter currentChapter = milestone.getChapter();
            Question lastQuestion = currentChapter.getQuestionById(milestone.getQuestionId());

            ivrResponseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));

            return forwardToQuestion(currentChapter.getNumber(), lastQuestion.getNumber());
        }
    }
}
