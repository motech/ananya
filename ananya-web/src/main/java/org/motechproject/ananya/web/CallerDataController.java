package org.motechproject.ananya.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.CertificationCourseLog;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.FrontLineWorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.Collection;

@Controller
public class CallerDataController {
    private static Logger log = LoggerFactory.getLogger(RegistrationController.class);
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
        Type collectionType = new TypeToken<Collection<TransferData<CertificationCourseLog>>>(){}.getType();
        Collection<TransferData<CertificationCourseLog>> data = gson.fromJson(stringifiedData, collectionType);

        System.out.println("\n\nPrinting the data packets received.");
        for(TransferData eachPacket : data) {
            final CertificationCourseLog courseLog = (CertificationCourseLog) eachPacket.data();
            courseLog.setCallerId(callerId);
            courseLog.setCallId(callId);
            courseLog.setToken(eachPacket.token());
            System.out.println(eachPacket);
            certificateCourseService.saveState(courseLog);
        }
        return null;
    }
    

}
