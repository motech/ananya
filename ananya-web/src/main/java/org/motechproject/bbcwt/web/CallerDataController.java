package org.motechproject.bbcwt.web;

import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CallerDataController {
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public CallerDataController(FrontLineWorkerService frontLineWorkerService) {
        this.frontLineWorkerService = frontLineWorkerService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/bookmark/add")
    @ResponseBody
    public String addBookMark(HttpServletRequest request) {
        String callerId = request.getParameter("callerId");
        BookMark bookMark = new BookMark(request.getParameter("bookmark.type"), request.getParameter("bookmark.chapterIndex"), request.getParameter("bookmark.lessonIndex"));

        frontLineWorkerService.addBookMark(callerId, bookMark);

        return "</done>";
    }

    public BookMark getBookmark(String callerId) {
        FrontLineWorker worker = frontLineWorkerService.getFrontLineWorker(callerId);
        if (worker == null || worker.bookMark() == null) {
            return new EmptyBookmark();
        }
        return worker.bookMark();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/score/add")
    @ResponseBody
    public String addScore(@RequestParam String callerId,
                           @RequestParam String chapterIndex,
                           @RequestParam String questionIndex,
                           @RequestParam boolean result,
                           HttpServletRequest request) {
        ReportCard.Score score = new ReportCard.Score(chapterIndex, questionIndex, result);
        frontLineWorkerService.addScore(callerId, score);
        return null;
    }
}
