package org.motechproject.bbcwt.ivr.action;


import com.ozonetel.kookoo.CollectDtmf;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Question;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.ChaptersRepository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class QuestionAction extends BaseAction {
    private ChaptersRepository chaptersRespository;
    private MilestonesRepository milestonesRepository;

    @Autowired
    public QuestionAction(ChaptersRepository chaptersRespository, MilestonesRepository milestonesRepository, IVRMessage messages)  {
        this.chaptersRespository = chaptersRespository;
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
    }

    @RequestMapping(value="/chapter/{chapterNumber}/question/{questionNumber}", method= RequestMethod.GET)
    @ResponseBody
    public String get(@PathVariable("chapterNumber") int chapterNumber, @PathVariable("questionNumber") int questionNumber, HttpServletRequest request, HttpServletResponse response) {
        Chapter chapter = chaptersRespository.findByNumber(chapterNumber);
        Question question = chapter.getQuestionByNumber(questionNumber);

        String callerId = healthWorkerCallerIdFromSession(request.getSession());

        milestonesRepository.markNewQuestionStart(callerId, chapterNumber, questionNumber);

        CollectDtmf answerDtmf = ivrDtmfBuilder(request).addPlayAudio(absoluteFileLocation(question.getQuestionLocation())).create();
        ivrResponseBuilder(request).withCollectDtmf(answerDtmf);

        request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, "/collectAnswer");
        return ivrResponseBuilder(request).create().getXML();
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}