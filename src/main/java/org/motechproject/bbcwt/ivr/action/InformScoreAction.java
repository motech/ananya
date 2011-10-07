package org.motechproject.bbcwt.ivr.action;


import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.domain.ReportCard;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.repository.ReportCardsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class InformScoreAction extends BaseAction {
    private MilestonesRepository milestonesRepository;
    private ReportCardsRepository reportCardsRepository;

    @Autowired
    public InformScoreAction(MilestonesRepository milestonesRepository, ReportCardsRepository reportCardsRepository, IVRMessage messages) {
        this.milestonesRepository = milestonesRepository;
        this.reportCardsRepository = reportCardsRepository;
        this.messages = messages;
    }

    @Override
    @RequestMapping(value="/informScore", method= RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String callerId = healthWorkerCallerIdFromSession(request.getSession());

        Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);

        HealthWorker healthWorker = milestone.getHealthWorker();
        Chapter currentChapter = milestone.getChapter();

        ReportCard reportCard = reportCardsRepository.findByHealthWorker(healthWorker);
        ReportCard.ScoreSummary chapterScoreSummary = reportCard.scoreEarned(currentChapter);

        String scoreReportFileName = scoreReportFileName(currentChapter, chapterScoreSummary.getScoredMarks(), chapterScoreSummary.getMaximumMarks());
        ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(scoreReportFileName));

        return "forward:/endOfQuizMenu";
    }

    protected String scoreReportFileName(Chapter currentChapter, int scoredMarks, int maximumMarks) {
        return scoredMarks + "_out_of_" + maximumMarks + ".wav";
    }
}