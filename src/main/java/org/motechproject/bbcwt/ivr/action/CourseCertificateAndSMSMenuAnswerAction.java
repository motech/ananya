package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.domain.ReportCard;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.action.inputhandler.KeyPressHandler;
import org.motechproject.bbcwt.ivr.action.inputhandler.PlayHelpAction;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.listeners.SendSMSHandler;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.repository.ReportCardsRepository;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(CourseCertificateAndSMSMenuAnswerAction.LOCATION)
public class CourseCertificateAndSMSMenuAnswerAction extends AbstractPromptAnswerHandler {
    public static final String LOCATION = "/certificateAndSMSMenuAnswer";
    private SendSMSHandler sendSMSHandler;
    private MilestonesRepository milestonesRepository;
    private ReportCardsRepository reportCardsRepository;

    @Autowired
    public CourseCertificateAndSMSMenuAnswerAction(MilestonesRepository milestonesRepository, ReportCardsRepository reportCardsRepository, SendSMSHandler sendSMSHandler, IVRMessage messages) {
        super(messages);
        this.milestonesRepository = milestonesRepository;
        this.reportCardsRepository = reportCardsRepository;
        this.sendSMSHandler = sendSMSHandler;
    }

    protected void intializeKeyPressHandlerMap(final Map<Character, KeyPressHandler> keyPressHandlerMap) {
        keyPressHandlerMap.put('9', new SendSMS());
        keyPressHandlerMap.put('*', new PlayHelpAction(messages, "forward:" + CourseCertificateAndSMSMenuAction.LOCATION));
        keyPressHandlerMap.put(NO_INPUT, new NoInputHandler());
    }

    @Override
    protected KeyPressHandler invalidInputHandler() {
        return new InvalidInputHandler();
    }

    private class NoInputHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.incrementNoInputCount();
            int allowedNumberOfNoInputs = 1;

            if(ivrContext.getNoInputCount() > allowedNumberOfNoInputs) {
                ivrContext.resetNoInputCount();
                ivrContext.resetInvalidInputCount();
                return "forward:" + EndOfQuizMenuAction.LOCATION;
            }

            return "forward:" + CourseCertificateAndSMSMenuAction.LOCATION;
        }
    }

    private class InvalidInputHandler implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.incrementInvalidInputCount();
            int allowedNumberOfInvalidInputs = 1;

            if(ivrContext.getInvalidInputCount() > allowedNumberOfInvalidInputs) {
                ivrContext.resetNoInputCount();
                ivrContext.resetInvalidInputCount();
                return "forward:" + EndOfQuizMenuAction.LOCATION;
            }

            ivrResponseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));
            return "forward:" + CourseCertificateAndSMSMenuAction.LOCATION;
        }
    }

    private class SendSMS implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            String callerId = ivrContext.getCallerId();

            Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);

            HealthWorker healthWorker = milestone.getHealthWorker();
            Chapter currentChapter = milestone.getChapter();

            ReportCard reportCard = reportCardsRepository.findByHealthWorker(healthWorker);
            int score = reportCard.scoreEarned(currentChapter).getScoredMarks();

            final Map parameters = new HashMap(2);
            parameters.put("number", callerId);
            parameters.put("text", currentChapter.getSummaryForScore(score));
            MotechEvent event = new MotechEvent("SendSMS", parameters);

            sendSMSHandler.sendSMS(event);

            return "forward:" + EndOfQuizMenuAction.LOCATION;
        }
    }
}
