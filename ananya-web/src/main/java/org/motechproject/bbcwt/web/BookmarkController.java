package org.motechproject.bbcwt.web;

import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BookmarkController {
    private FrontLineWorkerService frontLineWorkerService;

    @Autowired
    public BookmarkController(FrontLineWorkerService frontLineWorkerService) {
        this.frontLineWorkerService = frontLineWorkerService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/bookmark/add")
    @ResponseBody
    public String addBookMark(HttpServletRequest request) {
        String callerId = request.getParameter("callerId");
        FrontLineWorker frontLineWorker = frontLineWorkerService.getFrontLineWorker(callerId);

        BookMark bookMark = new BookMark(request.getParameter("bookmark.type"), request.getParameter("bookmark.chapterIndex"), request.getParameter("bookmark.lessonIndex"));
        frontLineWorker.addBookMark(bookMark);
        frontLineWorkerService.save(frontLineWorker);

        return "</done>";
    }

    public BookMark getBookmark(String callerId) {
        FrontLineWorker worker = frontLineWorkerService.getFrontLineWorker(callerId);
        if (worker == null || worker.getBookmark() == null) {
            return new EmptyBookmark();
        }
        return worker.getBookmark();
    }
}
