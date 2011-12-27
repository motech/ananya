package org.motechproject.bbcwt.ivr.action;


import com.ozonetel.kookoo.CollectDtmf;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.domain.ReportCard;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
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

@Controller
@RequestMapping(value="/informScore")
public class InformScoreAction extends HelpEnabledAction {
    private MilestonesRepository milestonesRepository;
    private ReportCardsRepository reportCardsRepository;

    @Autowired
    public InformScoreAction(MilestonesRepository milestonesRepository, ReportCardsRepository reportCardsRepository, IVRMessage messages) {
        this.milestonesRepository = milestonesRepository;
        this.reportCardsRepository = reportCardsRepository;
        this.messages = messages;
    }

    @Override
    @RequestMapping(method= RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String callerId = healthWorkerCallerIdFromSession(request.getSession());

        Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);

        HealthWorker healthWorker = milestone.getHealthWorker();
        Chapter currentChapter = milestone.getChapter();

        ReportCard reportCard = reportCardsRepository.findByHealthWorker(healthWorker);
        ReportCard.ScoreSummary chapterScoreSummary = reportCard.scoreEarned(currentChapter);

        String scoreReportFileName = scoreReportFileName(currentChapter, chapterScoreSummary.getScoredMarks(), chapterScoreSummary.getMaximumMarks());

        CollectDtmf collectDtmf = ivrDtmfBuilder(request).addPlayAudio(absoluteFileLocation(scoreReportFileName)).withTimeOutInMillis(1).create();
        IVRResponseBuilder ivrResponseBuilder = ivrResponseBuilder(request).withCollectDtmf(collectDtmf);

        request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, helpInteractionLocation(request));

        return ivrResponseBuilder.create().getXML();
    }

    @Override
    @RequestMapping(method=RequestMethod.GET, value= HelpEnabledAction.HELP_HANDLER)
    public String helpHandler(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return super.helpHandler(ivrRequest, request, response);
    }

    protected String scoreReportFileName(Chapter currentChapter, int scoredMarks, int maximumMarks) {
        return scoredMarks + "_out_of_" + maximumMarks + ".wav";
    }

    @Override
    protected String postHelpInteraction(HttpServletRequest request) {
        return "/informScore";
    }

    @Override
    protected String interactionWhenNoHelpIsRequested(HttpServletRequest request) {
        return CourseCertificateAndSMSMenuAction.LOCATION;
    }
}