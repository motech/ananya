package org.motechproject.bbcwt.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
import com.ozonetel.kookoo.Response;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.domain.Question;
import org.motechproject.bbcwt.domain.ReportCard;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.action.inputhandler.KeyPressHandler;
import org.motechproject.bbcwt.ivr.action.inputhandler.PlayHelpAction;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.repository.ReportCardsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/collectAnswer")
public class CollectAnswerAction extends AbstractPromptAnswerHandler {
    private MilestonesRepository milestonesRepository;
    private ReportCardsRepository reportCardsRepository;


    @Autowired
    public CollectAnswerAction(MilestonesRepository milestonesRepository, ReportCardsRepository reportCardsRepository, IVRMessage messages) {
        super(messages);
        this.milestonesRepository = milestonesRepository;
        this.reportCardsRepository = reportCardsRepository;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String toForwardAfterHelp = super.handle(ivrRequest, request, response);
        request.getSession().setAttribute(IVR.Attributes.NAVIGATION_POST_HELP, toForwardAfterHelp);

        CollectDtmf collectDtmf = ivrDtmfBuilder(request).withTimeOutInMillis(1).create();
        Response ivrResponse = ivrResponseBuilder(request).withCollectDtmf(collectDtmf).create();

        request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, "/collectAnswer/helpHandler");
        return ivrResponse.getXML();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/helpHandler")
    public String helpHandler(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        if(!ivrRequest.hasNoData()) {
            ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.IVR_HELP)));
        }
        return (String)request.getSession().getAttribute(IVR.Attributes.NAVIGATION_POST_HELP);
    }

    protected void intializeKeyPressHandlerMap(final Map<Character, KeyPressHandler> keyPressHandlerMap) {
        final ValidAnswerHandler validAnswerHandler = new ValidAnswerHandler();
        keyPressHandlerMap.put('1', validAnswerHandler);
        keyPressHandlerMap.put('2', validAnswerHandler);
        keyPressHandlerMap.put('*', new PlayHelpAction(messages, "forward:"+ExistingUserAction.LOCATION));
        keyPressHandlerMap.put(NO_INPUT, new NoInputHandler());
    }

    private String forwardToQuestion(int chapterNumber, int questionNumber) {
        return "forward:/chapter/"+chapterNumber+"/question/"+ questionNumber;
    }

    @Override
    protected KeyPressHandler invalidInputHandler() {
        return new InvalidInputHandler();
    }

    private class ValidAnswerHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            String callerId = ivrContext.getCallerId();

            milestonesRepository.markLastMilestoneFinish(callerId);

            Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);
            Chapter currentChapter = milestone.getChapter();
            Question lastQuestion = currentChapter.getQuestionById(milestone.getQuestionId());

            ReportCard.HealthWorkerResponseToQuestion healthWorkerResponseToQuestion = reportCardsRepository.addUserResponse(callerId, currentChapter.getNumber(), lastQuestion.getNumber(), Character.getNumericValue(keyPressed));

            if(healthWorkerResponseToQuestion.isCorrect()) {
                ivrDtmfBuilder.addPlayAudio(absoluteFileLocation(lastQuestion.getCorrectAnswerExplanationLocation()));
            }
            else {
                ivrDtmfBuilder.addPlayAudio(absoluteFileLocation(lastQuestion.getIncorrectAnswerExplanationLocation()));
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
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
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
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
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
