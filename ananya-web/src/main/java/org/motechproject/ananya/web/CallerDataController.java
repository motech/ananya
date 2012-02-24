package org.motechproject.ananya.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.domain.CertificationCourseBookmark;
import org.motechproject.ananya.domain.DataTransferType;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.service.CallDetailLoggerService;
import org.motechproject.ananya.service.CertificateCourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

@Controller
public class CallerDataController {
    private static Logger log = LoggerFactory.getLogger(RegistrationController.class);
    private CallDetailLoggerService callDetailLoggerService;
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
    public CallerDataController(CallDetailLoggerService callDetailLoggerService, CertificateCourseService certificateCourseService) {
        this.callDetailLoggerService = callDetailLoggerService;
        this.certificateCourseService = certificateCourseService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/coursestate/add")
    @ResponseBody
    public String receiveIVRData(HttpServletRequest request){
        String callerId = request.getParameter("callerId");
        String callId = request.getParameter("callId");

        String stringifiedData = request.getParameter("dataToPost");

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<TransferData>>(){}.getType();
        Collection<TransferData> data = gson.fromJson(stringifiedData, collectionType);

        Collection<CertificationCourseBookmark> bookmarks = new ArrayList<CertificationCourseBookmark>();
        Collection<CallDetailLog> detailLogs = new ArrayList<CallDetailLog>();

        for(TransferData transferData : data){
            if(transferData.getType() == DataTransferType.CC_BOOKMARK)
                    bookmarks.add(CaptureBookmark(transferData.getData(), callId, callerId, transferData.getToken()));
            else
                detailLogs.add(CaptureLogDetail(transferData.getData(), callId, callerId, transferData.getToken()));
        }
        
        HandleBookmark(bookmarks);
        HandleCallDuration(detailLogs);

        return "";
    }



    private CallDetailLog CaptureLogDetail(String data, String callId, String callerId, String token) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<CallDetailLog>(){}.getType();
        return gson.fromJson(data, collectionType);
    }

    private CertificationCourseBookmark CaptureBookmark(String data, String callId, String callerId, String token) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<CertificationCourseBookmark>(){}.getType();
        CertificationCourseBookmark certificationCourseBookmark = gson.fromJson(data, collectionType);
        certificationCourseBookmark.setToken(token);
        certificationCourseBookmark.setCallId(callId);
        certificationCourseBookmark.setCallerId(callerId);
        return certificationCourseBookmark;
    }

    private void HandleBookmark(Collection<CertificationCourseBookmark> bookmarks)
    {
        System.out.println("\n\nPrinting the data packets received.");
        for(CertificationCourseBookmark bookmark : bookmarks) {
            certificateCourseService.saveState(bookmark);
        }
    }

    private void HandleCallDuration(Collection<CallDetailLog> deatilLogs) {
        System.out.println("\n\nPrinting the data packets received.");
        for(CallDetailLog detailLog : deatilLogs) {
            callDetailLoggerService.save(detailLog);
        }
    }
}
