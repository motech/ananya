package org.motechproject.bbcwt.ivr.action;


import com.ozonetel.kookoo.CollectDtmf;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.domain.ReportCard;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.repository.ChaptersRespository;
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
@RequestMapping(value= CourseCertificateAndSMSMenuAction.LOCATION)
public class CourseCertificateAndSMSMenuAction extends BaseAction {

    public static final String LOCATION = "/certificateAndSMSMenu";
    private MilestonesRepository milestonesRepository;
    private ReportCardsRepository reportCardsRepository;

    @Autowired
    public CourseCertificateAndSMSMenuAction(MilestonesRepository milestonesRepository, ReportCardsRepository reportCardsRepository, IVRMessage messages) {
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
        int score = reportCard.scoreEarned(currentChapter).getScoredMarks();
        final String promptToBePlayed = currentChapter.getCourseSummaryPromptForScore(score);
        final IVRDtmfBuilder ivrDtmfBuilder = ivrDtmfBuilder(request);
        ivrDtmfBuilder.addPlayAudio(absoluteFileLocation(promptToBePlayed));
        final IVRResponseBuilder ivrResponseBuilder = ivrResponseBuilder(request);
        ivrResponseBuilder.withCollectDtmf(ivrDtmfBuilder.create());

        request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, CourseCertificateAndSMSMenuAnswerAction.LOCATION);
        return ivrResponseBuilder.create().getXML();
    }
}