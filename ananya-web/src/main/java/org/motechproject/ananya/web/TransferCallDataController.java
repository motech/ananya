package org.motechproject.ananya.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.request.CertificationCourseStateRequest;
import org.motechproject.ananya.service.CallLogCounterService;
import org.motechproject.ananya.service.CallLoggerService;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.ReportPublisherService;
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
    private static Logger log = LoggerFactory.getLogger(TransferCallDataController.class);
    private CallLoggerService callLoggerService;
    private CertificateCourseService certificateCourseService;
    private CallLogCounterService callLogCounterService;
    private ReportPublisherService reportPublisherService;

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
    public TransferCallDataController(CallLoggerService callLoggerService,
                    CertificateCourseService certificateCourseService, CallLogCounterService callLogCounterService, ReportPublisherService reportPublisherService) {
        this.callLoggerService = callLoggerService;
        this.certificateCourseService = certificateCourseService;
        this.callLogCounterService = callLogCounterService;
        this.reportPublisherService = reportPublisherService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transferdata")
    @ResponseBody
    public String receiveIVRData(HttpServletRequest request) {
        final String callerId = request.getParameter("callerId");
        final String callId = request.getParameter("callId");
        // TODO: get called number here

        String stringifiedData = request.getParameter("dataToPost");

        // TODO: annotate transfer data to implement deserialization information itself
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TransferData.class, new TransferData());
        Gson gson = gsonBuilder.create();

        Type collectionType = new TypeToken<Collection<TransferData>>(){}.getType();
        Collection<TransferData> dataCollection = gson.fromJson(stringifiedData, collectionType);

        callLogCounterService.purgeRedundantPackets(callId, dataCollection);

        ArrayList<CertificationCourseStateRequest> certificationCourseStateRequests = new ArrayList<CertificationCourseStateRequest>();
        Collection<CallDuration> durations = new ArrayList<CallDuration>();

        for(TransferData transferData : dataCollection) {
            if(transferData.getType().equals(TransferData.TYPE_CC_STATE))
                certificationCourseStateRequests.add(
                    CertificationCourseStateRequest.makeObjectFromJson(
                            callerId, callId, transferData.getToken(), transferData.getData()));
            else
                durations.add(CaptureCallLog(transferData.getData(), callId, callerId));
        }

        certificateCourseService.saveState(certificationCourseStateRequests);


        HandleCallDuration(durations);
        
        return "";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transferdata/disconnect")
    @ResponseBody
    public String receiveIVRDataAtDisconnect(HttpServletRequest request){
        final String callId = request.getParameter("callId");
        receiveIVRData(request);
        reportPublisherService.publishCallDisconnectEvent(callId);

        return "";
    }


    private CallDuration CaptureCallLog(String data, String callId, String callerId) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<CallDuration>(){}.getType();
        CallDuration callDuration = gson.fromJson(data, collectionType);
        callDuration.setCallId(callId);
        callDuration.setCallerId(callerId);
        return callDuration;
    }

    private void HandleCallDuration(Collection<CallDuration> deatilLogs) {
        for(CallDuration duration : deatilLogs) {
            callLoggerService.save(duration);
        }
    }
}
