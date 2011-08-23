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
import java.util.HashSet;
import java.util.Set;

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

    private static final Set<Character> VALID_ANSWERS = new HashSet<Character>();

    static {
        VALID_ANSWERS.add('1');
        VALID_ANSWERS.add('2');
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        char chosenOption = ivrInput(ivrRequest);

        String callerId = (String) request.getSession().getAttribute(IVR.Attributes.CALLER_ID);
        Milestone milestone = milestonesRepository.markLastMilestoneFinish(callerId);

        Chapter currentChapter = chaptersRespository.get(milestone.getChapterId());
        Question lastQuestion = currentChapter.getQuestionById(milestone.getQuestionId());

        if (!dtmfInputIsValid(chosenOption)) {
            if (chosenOption != NO_INPUT) {
                ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));
            }
            return forwardToQuestion(currentChapter.getNumber(), lastQuestion.getNumber());
        }

        ReportCard.HealthWorkerResponseToQuestion healthWorkerResponseToQuestion = reportCardsRepository.addUserResponse(callerId, currentChapter.getNumber(), lastQuestion.getNumber(), Character.getNumericValue(chosenOption));


        if(healthWorkerResponseToQuestion.isCorrect()) {
            ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(lastQuestion.getCorrectAnswerExplanationLocation()));
        }
        else {
            ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(lastQuestion.getIncorrectAnswerExplanationLocation()));
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
        return VALID_ANSWERS.contains(chosenOption);
    }

}
