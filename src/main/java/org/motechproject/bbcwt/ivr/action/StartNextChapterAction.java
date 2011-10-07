package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/startNextChapter")
public class StartNextChapterAction extends BaseAction {
    private MilestonesRepository milestonesRepository;
    private ChaptersRespository chaptersRespository;

    @Autowired
    public StartNextChapterAction(MilestonesRepository milestonesRepository, ChaptersRespository chaptersRespository, IVRMessage messages) {
        this.milestonesRepository = milestonesRepository;
        this.chaptersRespository = chaptersRespository;
        this.messages = messages;
    }

    @Override
    @RequestMapping(method= RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String healthWorkerCallerId = healthWorkerCallerIdFromSession(request.getSession());

        Milestone currentMilestone = milestonesRepository.currentMilestoneWithLinkedReferences(healthWorkerCallerId);
        Chapter currentChapter = currentMilestone.getChapter();
        int currentChapterNumber = currentChapter.getNumber();

        int nextChapterNumber = currentChapterNumber + 1;
        Chapter nextChapter = chaptersRespository.findByNumber(nextChapterNumber);

        if(nextChapter == null) {
            return "forward:/endOfCourse";
        }

        return "forward:/chapter/"+ nextChapterNumber +"/lesson/1";
    }
}