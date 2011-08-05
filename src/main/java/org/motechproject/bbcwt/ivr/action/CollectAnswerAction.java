package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.domain.Question;
import org.motechproject.bbcwt.domain.ReportCard;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.repository.ReportCardsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/collectAnswer")
public class CollectAnswerAction extends BaseAction {
    private ChaptersRespository chaptersRespository;
    private MilestonesRepository milestonesRepository;
    private ReportCardsRepository reportCardsRepository;

    @Autowired
    public CollectAnswerAction(ChaptersRespository chaptersRespository, MilestonesRepository milestonesRepository, ReportCardsRepository reportCardsRepository, IVRMessage messages) {
        this.chaptersRespository = chaptersRespository;
        this.milestonesRepository = milestonesRepository;
        this.reportCardsRepository = reportCardsRepository;
        this.messages = messages;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String dtmfInput = ivrRequest.getData();
        char chosenOption = ' ';

        if(dtmfInput!=null && dtmfInput.length() > 0) {
            chosenOption = dtmfInput.charAt(0);
        }

        String callerId = (String) request.getSession().getAttribute(IVR.Attributes.CALLER_ID);
        Milestone milestone = milestonesRepository.markLastMilestoneFinish(callerId);

        Chapter currentChapter = chaptersRespository.get(milestone.getChapterId());
        Question lastQuestion = currentChapter.getQuestionById(milestone.getQuestionId());

        if(!dtmfInputIsValid(chosenOption)) {
           ivrResponseBuilder(request).addPlayText(messages.get(IVRMessage.INVALID_INPUT));
           return forwardToQuestion(currentChapter.getNumber(), lastQuestion.getNumber());
        }

        ReportCard.HealthWorkerResponseToQuestion healthWorkerResponseToQuestion = reportCardsRepository.addUserResponse(callerId, currentChapter.getNumber(), lastQuestion.getNumber(), Character.getNumericValue(chosenOption));


        if(healthWorkerResponseToQuestion.isCorrect()) {
            ivrResponseBuilder(request).addPlayText(lastQuestion.getCorrectAnswerExplanationLocation());
        }
        else {
            ivrResponseBuilder(request).addPlayText(lastQuestion.getIncorrectAnswerExplanationLocation());
        }

        int nextQuestionNumber = lastQuestion.getNumber() + 1;
        if(currentChapter.getQuestionByNumber(nextQuestionNumber) == null){
            return "forward:/informScore";
        }
        else{
            return forwardToQuestion(currentChapter.getNumber(), nextQuestionNumber);
        }

    }

    private String forwardToQuestion(int chapterNumber, int questionNumber) {
        return "forward:/chapter/"+chapterNumber+"/question/"+ questionNumber;
    }

    private boolean dtmfInputIsValid(char chosenOption) {
        return Character.isDigit(chosenOption);
    }

}
