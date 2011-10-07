package org.motechproject.bbcwt.ivr.action;

import com.ozonetel.kookoo.CollectDtmf;
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
@RequestMapping(StartQuizAction.LOCATION)
public class StartQuizAction extends HelpEnabledAction {
    public static final String LOCATION = "/startQuiz";
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
            CollectDtmf collectDtmf = ivrDtmfBuilder(request).addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.QUIZ_HEADER))).create();
            ivrResponseBuilder(request).withCollectDtmf(collectDtmf);
        }
        session.setAttribute(IVR.Attributes.PREV_INTERACTION, servletPath(request));
        session.setAttribute(IVR.Attributes.NEXT_INTERACTION, helpInteractionLocation(request));

        return ivrResponseBuilder(request).create().getXML();
    }

    @RequestMapping(value = HelpEnabledAction.HELP_HANDLER, method = RequestMethod.GET)
    @Override
    public String helpHandler(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return super.helpHandler(ivrRequest, request, response);
    }

    private Chapter currentChapter(String healthWorkerCallerId) {
        Milestone currentMilestone = milestonesRepository.currentMilestoneWithLinkedReferences(healthWorkerCallerId);
        return currentMilestone.getChapter();
    }

    @Override
    protected String nextInteraction(HttpServletRequest request) {
        final HttpSession session = request.getSession();
        String healthWorkerCallerId = healthWorkerCallerIdFromSession(session);

        Chapter currentChapter = currentChapter(healthWorkerCallerId);

        if (currentChapter.hasQuestions()) {
            int currentChapterNumber = currentChapter.getNumber();
            return "/chapter/" + currentChapterNumber + "/question/1";
        } else {
            return "/startNextChapter";
        }
    }
}