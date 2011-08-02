package org.motechproject.bbcwt.ivr.action;


import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ChapterAction extends BaseAction {
    private ChaptersRespository chaptersRespository;
    private MilestonesRepository milestonesRepository;
    private HealthWorkersRepository healthWorkersRepository;
    private DateUtil dateUtil;

    @Autowired
    public ChapterAction(HealthWorkersRepository healthWorkersRepository, ChaptersRespository chaptersRespository, MilestonesRepository milestonesRepository, DateUtil dateUtil, IVRMessage messages)  {
        this.healthWorkersRepository = healthWorkersRepository;
        this.chaptersRespository = chaptersRespository;
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
        this.dateUtil = dateUtil;
    }

    @RequestMapping(value="/chapter/{chapterNumber}/lesson/{lessonNumber}", method= RequestMethod.GET)
    public String get(@PathVariable("chapterNumber") int chapterNumber, @PathVariable("lessonNumber") int lessonNumber, HttpServletRequest request, HttpServletResponse response) {
        Chapter chapter = chaptersRespository.findByNumber(chapterNumber);
        Lesson lessonToPlay = chapter.getLessonByNumber(lessonNumber);

        String callerId = (String)request.getSession().getAttribute(IVR.Attributes.CALLER_ID);
        HealthWorker healthWorker = healthWorkersRepository.findByCallerId(callerId);
        Milestone milestone = milestonesRepository.findByHealthWorker(healthWorker);

        if(milestone == null) {
            milestone = new Milestone();
        }

        milestone.setChapterId(chapter.getId());
        milestone.setLessonId(lessonToPlay.getId());
        milestone.setStartDate(dateUtil.getDate());
        milestone.setHealthWorkerId(healthWorker.getId());
        milestonesRepository.add(milestone);

        ivrResponseBuilder(request).addPlayText(lessonToPlay.getLocation());
        return "forward:/helpMenu";
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}