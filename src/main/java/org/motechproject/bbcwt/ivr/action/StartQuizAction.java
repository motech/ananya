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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/startQuiz")
public class StartQuizAction extends HelpEnabledAction {
    private MilestonesRepository milestonesRepository;

    @Autowired
    public StartQuizAction(MilestonesRepository milestonesRepository, IVRMessage messages) {
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        final HttpSession session = request.getSession();
        String healthWorkerCallerId = healthWorkerCallerIdFromSession(session);

        Chapter currentChapter = currentChapter(healthWorkerCallerId);

        if (currentChapter.hasQuestions()) {
            int currentChapterNumber = currentChapter.getNumber();
            //TODO: The following has to be figured out from DB, since every chapter will have a different QUIZ HEADER
            ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.QUIZ_HEADER)));
            session.setAttribute(IVR.Attributes.NEXT_INTERACTION, "/chapter/" + currentChapterNumber + "/question/1");
        } else {
            session.setAttribute(IVR.Attributes.NEXT_INTERACTION, "/startNextChapter");
        }
        return ivrResponseBuilder(request).create().getXML();
    }

    private Chapter currentChapter(String healthWorkerCallerId) {
        Milestone currentMilestone = milestonesRepository.currentMilestoneWithLinkedReferences(healthWorkerCallerId);
        return currentMilestone.getChapter();
    }

    @Override
    protected String nextInteraction() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}