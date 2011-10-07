package org.motechproject.bbcwt.ivr.action;


import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LessonAction extends HelpEnabledAction {
    public static final String LOCATION = "/chapter/{chapterNumber}/lesson/{lessonNumber}";
    private ChaptersRespository chaptersRespository;
    private MilestonesRepository milestonesRepository;
    private HealthWorkersRepository healthWorkersRepository;
    private DateUtil dateUtil;

    @Autowired
    public LessonAction(HealthWorkersRepository healthWorkersRepository, ChaptersRespository chaptersRespository, MilestonesRepository milestonesRepository, DateUtil dateUtil, IVRMessage messages)  {
        this.healthWorkersRepository = healthWorkersRepository;
        this.chaptersRespository = chaptersRespository;
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
        this.dateUtil = dateUtil;
    }

    @RequestMapping(value= LessonAction.LOCATION, method= RequestMethod.GET)
    @ResponseBody
    public String get(@PathVariable("chapterNumber") int chapterNumber, @PathVariable("lessonNumber") int lessonNumber, HttpServletRequest request, HttpServletResponse response) {
        Chapter chapter = chaptersRespository.findByNumber(chapterNumber);
        Lesson lessonToPlay = chapter.getLessonByNumber(lessonNumber);

        final HttpSession session = request.getSession();
        String callerId = healthWorkerCallerIdFromSession(session);

        HealthWorker healthWorker = healthWorkersRepository.findByCallerId(callerId);

        if(healthWorker == null) {
            healthWorker = registerNewHealthWorker(callerId);
        }

        milestonesRepository.markNewChapterStart(callerId, chapterNumber, lessonNumber);

        final IVRDtmfBuilder collectDtmf = ivrDtmfBuilder(request);
        collectDtmf.withTimeOutInMillis(1);
        collectDtmf.withPlayAudio(absoluteFileLocation(lessonToPlay.getFileName()));
        ivrResponseBuilder(request).withCollectDtmf(collectDtmf.create());
        session.setAttribute(IVR.Attributes.PREV_INTERACTION, servletPath(request));
        session.setAttribute(IVR.Attributes.NEXT_INTERACTION, helpInteractionLocation(request));
        return ivrResponseBuilder(request).create().getXML();
    }

    private HealthWorker registerNewHealthWorker(String callerId) {
        HealthWorker healthWorker;
        healthWorker = new HealthWorker(callerId);
        healthWorkersRepository.add(healthWorker);
        return healthWorker;
    }


    @RequestMapping(value = LessonAction.LOCATION + HelpEnabledAction.HELP_HANDLER, method = RequestMethod.GET)
    public String helpHandler(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return super.helpHandler(ivrRequest, request, response);
    }

    protected String nextInteraction() {
        return "/lessonEndMenu";
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}