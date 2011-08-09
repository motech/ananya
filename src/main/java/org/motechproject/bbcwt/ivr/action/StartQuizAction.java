package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/startQuiz")
public class StartQuizAction extends BaseAction {
    private MilestonesRepository milestonesRepository;

    @Autowired
    public StartQuizAction(MilestonesRepository milestonesRepository, IVRMessage messages) {
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String healthWorkerCallerId = (String) request.getSession().getAttribute(IVR.Attributes.CALLER_ID);

        Milestone currentMilestone = milestonesRepository.currentMilestoneWithLinkedReferences(healthWorkerCallerId);
        Chapter currentChapter = currentMilestone.getChapter();

        boolean thereAreQuestionsInCurrentChapter = currentChapter.getQuestions().size() > 0;
        if (thereAreQuestionsInCurrentChapter) {
            int currentChapterNumber = currentChapter.getNumber();

            ivrResponseBuilder(request).addPlayText(messages.get(IVRMessage.MSG_START_OF_QUIZ));
            ivrResponseBuilder(request).addPlayText(" " + currentChapterNumber+".");

            return "forward:/chapter/" + currentChapterNumber + "/question/1";
        } else {
            return "forward:/startNextChapter";
        }
    }
}