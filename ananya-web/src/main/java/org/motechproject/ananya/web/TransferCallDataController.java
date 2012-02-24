package org.motechproject.ananya.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.motechproject.ananya.domain.CallDuration;
import org.motechproject.ananya.domain.CertificationCourseBookmark;
import org.motechproject.ananya.domain.DataTransferType;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.service.CallLoggerService;
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
public class TransferCallDataController {
    private static Logger log = LoggerFactory.getLogger(RegistrationController.class);
    private CallLoggerService callLoggerService;
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
    public TransferCallDataController(CallLoggerService callLoggerService, CertificateCourseService certificateCourseService) {
        this.callLoggerService = callLoggerService;
        this.certificateCourseService = certificateCourseService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transferdata")
    @ResponseBody
    public String receiveIVRData(HttpServletRequest request){
        String callerId = request.getParameter("callerId");
        String callId = request.getParameter("callId");

        String stringifiedData = request.getParameter("dataToPost");

        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<TransferData>>(){}.getType();
        Collection<TransferData> data = gson.fromJson(stringifiedData, collectionType);

        Collection<CertificationCourseBookmark> bookmarks = new ArrayList<CertificationCourseBookmark>();
        Collection<CallDuration> durations = new ArrayList<CallDuration>();

        for(TransferData transferData : data){
            if(transferData.getType() == DataTransferType.CC_BOOKMARK)
                    bookmarks.add(CaptureBookmark(transferData.getData(), callId, callerId, transferData.getToken()));
            else
                durations.add(CaptureLogDetail(transferData.getData(), callId, callerId));
        }
        
        HandleBookmark(bookmarks);
        HandleCallDuration(durations);

        return "";
    }


    private CallDuration CaptureLogDetail(String data, String callId, String callerId) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<CallDuration>(){}.getType();
        CallDuration callDuration = gson.fromJson(data, collectionType);
        callDuration.setCallId(callId);
        callDuration.setCallerId(callerId);
        return callDuration;
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

    private void HandleCallDuration(Collection<CallDuration> deatilLogs) {
        System.out.println("\n\nPrinting the data packets received.");
        for(CallDuration duration : deatilLogs) {
            callLoggerService.save(duration);
        }
    }
}
