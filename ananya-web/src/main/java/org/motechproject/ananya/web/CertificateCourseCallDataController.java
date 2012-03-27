package org.motechproject.ananya.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.motechproject.ananya.domain.CallDuration;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.request.CertificationCourseStateRequest;
import org.motechproject.ananya.service.CallLogCounterService;
import org.motechproject.ananya.service.CallLoggerService;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.publish.DataPublishService;
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
import java.util.List;

@Controller
public class CertificateCourseCallDataController {

    private static Logger log = LoggerFactory.getLogger(CertificateCourseCallDataController.class);
    public static final String DUMMY = "<dummy/>";

    private CallLoggerService callLoggerService;
    private CallLogCounterService callLogCounterService;
    private CertificateCourseService certificateCourseService;
    private DataPublishService dataPublishService;

    @Autowired
    public CertificateCourseCallDataController(CallLoggerService callLoggerService,
                                               CertificateCourseService certificateCourseService,
                                               CallLogCounterService callLogCounterService,
                                               DataPublishService dataPublishService) {
        this.callLoggerService = callLoggerService;
        this.certificateCourseService = certificateCourseService;
        this.callLogCounterService = callLogCounterService;
        this.dataPublishService = dataPublishService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transferdata")
    @ResponseBody
    public String receiveCallData(HttpServletRequest request) {
        final String callId = request.getParameter("callId");
        final String callerId = request.getParameter("callerId");
        final String jsonData = request.getParameter("dataToPost");
        log.info("callId=" + callId + "|callerId=" + callerId + "|jsonData=" + jsonData);

        List<CertificationCourseStateRequest> stateRequests = new ArrayList<CertificationCourseStateRequest>();
        List<CallDuration> durations = new ArrayList<CallDuration>();
        List<TransferData> transferDataList = extractDataTransferList(jsonData);

        callLogCounterService.purgeRedundantPackets(callId, transferDataList);

        for (TransferData transferData : transferDataList) {
            if (transferData.isCCState())
                stateRequests.add(CertificationCourseStateRequest.makeObjectFromJson(
                        callerId, callId, transferData.getToken(), transferData.getData()));
            else
                durations.add(captureCallLog(callId, callerId, transferData.getData()));
        }
        certificateCourseService.saveState(stateRequests);
        log.info("Saved state");

        for (CallDuration duration : durations) {
            callLoggerService.save(duration);
        }
        return DUMMY;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transferdata/disconnect")
    @ResponseBody
    public String receiveIVRDataAtDisconnect(HttpServletRequest request) {
        final String callId = request.getParameter("callId");
        receiveCallData(request);

        dataPublishService.publishCallDisconnectEvent(callId);

        log.info("Call ended: "+callId);
        return DUMMY;
    }

    private List<TransferData> extractDataTransferList(String jsonData) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(TransferData.class, new TransferData());
        Gson gson = gsonBuilder.create();
        Type collectionType = new TypeToken<Collection<TransferData>>() {
        }.getType();
        return gson.fromJson(jsonData, collectionType);
    }

    private CallDuration captureCallLog(String callId, String callerId, String data) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<CallDuration>() {
        }.getType();
        CallDuration callDuration = gson.fromJson(data, collectionType);
        callDuration.setCallId(callId);
        callDuration.setCallerId(callerId);
        return callDuration;
    }

}
