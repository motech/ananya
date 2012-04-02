package org.motechproject.ananya.web;

import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.TransferDataList;
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

    @RequestMapping(method = RequestMethod.POST, value = "/transferdata")
    @ResponseBody
    public String receiveCallData(HttpServletRequest request) {
        final String callId = request.getParameter("callId");
        final String callerId = request.getParameter("callerId");
        final String jsonData = request.getParameter("dataToPost");

        TransferDataList transferDataList = new TransferDataList(jsonData);
        CertificationCourseStateRequestList stateRequestList = new CertificationCourseStateRequestList();
        CallDurationList callDurationList = new CallDurationList();

        for (TransferData transferData : transferDataList.all()) {
            if (transferData.isCCState())
                stateRequestList.add(callId, callerId, transferData.getData(), transferData.getToken());
            else
                callDurationList.add(callId, callerId, transferData.getData());
        }
        certificateCourseService.saveState(stateRequestList);
        callLoggerService.saveAll(callDurationList);

        log.info("Transfer data completed for: callId=" + callId + "|callerId=" + callerId);
        return getReturnVxml();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transferdata/disconnect")
    @ResponseBody
    public String receiveIVRDataAtDisconnect(HttpServletRequest request) {
        final String callId = request.getParameter("callId");
        receiveCallData(request);

        dataPublishService.publishCallDisconnectEvent(callId);

        log.info("Call ended: " + callId);
        return getReturnVxml();
    }

    private String getReturnVxml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<vxml version=\"2.1\" xsi:schemaLocation=\"http://www.w3.org/2001/vxml http://www.w3.org/TR/voicexml21/vxml.xsd\">");
        builder.append("<form id=\"endCall\">");
        builder.append("<block><disconnect/></block>");
        builder.append("/form></vxml>");
        return builder.toString();
    }

}