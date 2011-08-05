package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/lessonEndMenu")
public class LessonEndMenuAction extends BaseAction {
    private ChaptersRespository chaptersRespository;
    private MilestonesRepository milestonesRepository;
    private HealthWorkersRepository healthWorkersRepository;

    @Autowired
    public LessonEndMenuAction(HealthWorkersRepository healthWorkersRepository, ChaptersRespository chaptersRespository, MilestonesRepository milestonesRepository, IVRMessage messages) {
        this.healthWorkersRepository = healthWorkersRepository;
        this.chaptersRespository = chaptersRespository;
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {

        LOG.info("In here to render end of lesson menu...");

        String callerId = (String) request.getSession().getAttribute(IVR.Attributes.CALLER_ID);

        HealthWorker healthWorker = healthWorkersRepository.findByCallerId(callerId);
        Milestone milestone = milestonesRepository.findByHealthWorker(healthWorker);
        Chapter currentChapter = chaptersRespository.get(milestone.getChapterId());

        IVRResponseBuilder responseBuilder = ivrResponseBuilder(request);

        Lesson lastPlayedLesson = currentChapter.getLessonById(milestone.getLessonId());
        int nextLessonNumber = lastPlayedLesson.getNumber()+1;

        if(currentChapter.getLessonByNumber(nextLessonNumber) == null){
            IVRDtmfBuilder dtmfBuilder = ivrDtmfBuilder(request).withPlayText(messages.get(IVRMessage.END_OF_CHAPTER_MENU));
            responseBuilder.withCollectDtmf(dtmfBuilder.create());

            request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, "/chapterEndAnswer");
        }
        else{
            IVRDtmfBuilder dtmfBuilder = ivrDtmfBuilder(request).withPlayText(messages.get(IVRMessage.END_OF_LESSON_MENU));
            responseBuilder.withCollectDtmf(dtmfBuilder.create());

            request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, "/lessonEndAnswer");
        }
        LOG.info("Rendering end of lesson menu now.");
        return responseBuilder.create().getXML();
    }
}