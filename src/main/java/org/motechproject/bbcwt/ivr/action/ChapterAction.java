package org.motechproject.bbcwt.ivr.action;


import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ChapterAction extends BaseAction {
    private ChaptersRespository chaptersRespository;

    @Autowired
    public ChapterAction(ChaptersRespository chaptersRespository, IVRMessage messages)  {
        this.chaptersRespository = chaptersRespository;
        this.messages = messages;
    }

    @RequestMapping(value="/chapter/{chapterNumber}/lesson/{lessonNumber}", method= RequestMethod.GET)
    public String get(@PathVariable("chapterNumber") int chapterNumber, @PathVariable("lessonNumber") int lessonNumber, HttpServletRequest request, HttpServletResponse response) {
        LOG.info("Incoming request to play chapter: " + chapterNumber + "'s lesson: " + lessonNumber + ".");

        Chapter chapter = chaptersRespository.findByNumber(chapterNumber);
        LOG.info("Fetched chapter: " + chapter);
        Lesson lessonToPlay = chapter.getLessonByNumber(lessonNumber);

        LOG.info("Should be playing content at: " + lessonToPlay.getLocation());
        ivrResponseBuilder(request).addPlayText(lessonToPlay.getLocation());
        LOG.info("Forwarding to helpmenu.");
        return "forward:/helpMenu";
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}