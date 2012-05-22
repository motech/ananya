package org.motechproject.ananya.web;

import org.motechproject.ananya.action.TransferDataStateAction;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.CertificationCourseStateRequestList;
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

@Controller
public class CertificateCourseCallDataController {

    private static Logger log = LoggerFactory.getLogger(CertificateCourseCallDataController.class);

    private CallLoggerService callLoggerService;
    private CertificateCourseService certificateCourseService;
    private DataPublishService dataPublishService;

    @Autowired
    public CertificateCourseCallDataController(CallLoggerService callLoggerService,
                                               CertificateCourseService certificateCourseService,
                                               DataPublishService dataPublishService) {
        this.callLoggerService = callLoggerService;
        this.certificateCourseService = certificateCourseService;
        this.dataPublishService = dataPublishService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transferdata/disconnect")
    @ResponseBody
    public String receiveIVRDataAtDisconnect(HttpServletRequest request) {
        final String callId = request.getParameter("callId");
        final String callerId = new CallerIdParam(request.getParameter("callerId")).getValue();
        final String calledNumber = request.getParameter("calledNumber");
        final String jsonData = request.getParameter("dataToPost");

        TransferDataList transferDataList = new TransferDataList(jsonData);
        CertificationCourseStateRequestList stateRequestList = new CertificationCourseStateRequestList(callId, callerId);
        AudioTrackerRequestList audioTrackerList = new AudioTrackerRequestList(callId, callerId);
        CallDurationList callDurationList = new CallDurationList(callId, callerId, calledNumber);

        for (TransferData transferData : transferDataList.all()) {
            TransferDataStateAction transferDataStateAction = TransferDataStateAction.getFor(transferData.getType());
            transferDataStateAction.addToRequest(transferData, stateRequestList, audioTrackerList, callDurationList);
        }

        certificateCourseService.saveState(stateRequestList);
        certificateCourseService.saveAudioTrackerState(audioTrackerList);
        callLoggerService.saveAll(callDurationList);
        dataPublishService.publishCallDisconnectEvent(callId, callerId, ServiceType.CERTIFICATE_COURSE);

        log.info("Transfer data completed for: callId=" + callId + "|callerId=" + callerId);
        log.info("Call ended: " + callId);
        return getReturnVxml();
    }

    private String getReturnVxml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<vxml version=\"2.1\" xsi:schemaLocation=\"http://www.w3.org/2001/vxml http://www.w3.org/TR/voicexml21/vxml.xsd\">");
        builder.append("<form id=\"endCall\">");
        builder.append("<block><disconnect/></block>");
        builder.append("</form></vxml>");
        return builder.toString();
    }

}