package org.motechproject.bbcwt.ivr.action;


import com.ozonetel.kookoo.CollectDtmf;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Question;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.repository.ReportCardsRepository;
import org.motechproject.bbcwt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class QuestionAction extends BaseAction {
    private ChaptersRespository chaptersRespository;
    private MilestonesRepository milestonesRepository;

    @Autowired
    public QuestionAction(ChaptersRespository chaptersRespository, MilestonesRepository milestonesRepository, IVRMessage messages)  {
        this.chaptersRespository = chaptersRespository;
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
    }

    @RequestMapping(value="/chapter/{chapterNumber}/question/{questionNumber}", method= RequestMethod.GET)
    @ResponseBody
    public String get(@PathVariable("chapterNumber") int chapterNumber, @PathVariable("questionNumber") int questionNumber, HttpServletRequest request, HttpServletResponse response) {
        Chapter chapter = chaptersRespository.findByNumber(chapterNumber);
        Question question = chapter.getQuestionByNumber(questionNumber);

        String callerId = (String)request.getSession().getAttribute(IVR.Attributes.CALLER_ID);

        milestonesRepository.markNewQuestionStart(callerId, chapterNumber, questionNumber);

        ivrResponseBuilder(request).addPlayText(question.getQuestionLocation());
        CollectDtmf answerDtmf = ivrDtmfBuilder(request).withPlayText(question.getOptionsLocation()).create();
        ivrResponseBuilder(request).withCollectDtmf(answerDtmf);

        request.getSession().setAttribute(IVR.Attributes.NEXT_INTERACTION, "/collectAnswer");
        return ivrResponseBuilder(request).create().getXML();
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}