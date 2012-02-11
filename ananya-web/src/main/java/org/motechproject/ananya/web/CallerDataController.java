package org.motechproject.ananya.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.motechproject.ananya.domain.BookMark;
import org.motechproject.ananya.domain.CertificateCourseData;
import org.motechproject.ananya.domain.ReportCard;
import org.motechproject.ananya.domain.log.CertificationCourseLog;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

@Controller
public class CallerDataController {
    private FrontLineWorkerService frontLineWorkerService;
    private CertificateCourseService certificateCourseService;

    private static enum CourseStateDataKeys {
        CHAPTER_INDEX("chapterIndex", Integer.class),
        LESSON_OR_QUESTION_INDEX("lessonOrQuestionIndex", Integer.class),
        QUESTION_RESPONSE("questionResponse", Integer.class),
        RESULT("result", Boolean.class),
        INTERACTION_KEY("interactionKey", String.class);

        public final String value;
        public final Class klass;

        CourseStateDataKeys(String key, Class klass) {
            this.value = key;
            this.klass = klass;
        }
    };

    @Autowired
    public CallerDataController(FrontLineWorkerService frontLineWorkerService, CertificateCourseService certificateCourseService) {
        this.frontLineWorkerService = frontLineWorkerService;
        this.certificateCourseService = certificateCourseService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/coursestate/add")
    @ResponseBody
    public String receiveIVRData(HttpServletRequest request){
        String callerId = request.getParameter("callerId");
        String callId = request.getParameter("callId");

        String stringifiedData = request.getParameter("dataToPost");

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<CertificateCourseData>>(){}.getType();
        Collection<CertificateCourseData> data = gson.fromJson(stringifiedData, collectionType);

        System.out.println("\n\nPrinting the data packets received.");
        for(CertificateCourseData eachPacket : data) {
            final CertificationCourseLog courseLog = eachPacket.data();
            courseLog.setCallerId(callerId);
            courseLog.setCallId(callId);
            courseLog.setToken(eachPacket.token());
            System.out.println(eachPacket);
            certificateCourseService.saveState(courseLog);
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/bookmark/add")
    @ResponseBody
    public String addBookMark(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        for(Map.Entry<String, String[]> entry: parameterMap.entrySet()) {
            System.out.println(entry.getKey()+ ":");
            for(String value:entry.getValue()) {
                System.out.println("\t" + value);
            }
        }
        String callerId = request.getParameter("callerId");
        BookMark bookMark = new BookMark(
                request.getParameter("bookmark.type"),
                request.getParameter("bookmark.chapterIndex"),
                request.getParameter("bookmark.lessonIndex"));

        frontLineWorkerService.addBookMark(callerId, bookMark);
        return "";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/score/add")
    @ResponseBody
    public String addScore(HttpServletRequest request) {
        String callerId = request.getParameter("callerId");
        ReportCard.Score score = new ReportCard.Score(
                request.getParameter("quizResponse.chapterIndex"),
                request.getParameter("quizResponse.questionIndex"),
                Boolean.parseBoolean(request.getParameter("quizResponse.result")));

        frontLineWorkerService.addScore(callerId, score);
        return "<done/>";
    }
}
