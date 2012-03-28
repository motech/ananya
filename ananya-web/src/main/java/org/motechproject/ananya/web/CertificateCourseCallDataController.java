package org.motechproject.ananya.web;

import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.TransferDataList;
import org.motechproject.ananya.request.CertificationCourseStateRequestList;
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

        TransferDataList transferDataList = new TransferDataList(jsonData);
        CertificationCourseStateRequestList stateRequestList = new CertificationCourseStateRequestList();
        CallDurationList callDurationList = new CallDurationList();

        callLogCounterService.purgeRedundantPackets(callId, transferDataList.all());

        for (TransferData transferData : transferDataList.all()) {
            if (transferData.isCCState())
                stateRequestList.add(callId, callerId, transferData.getData(), transferData.getToken());
            else
                callDurationList.add(callId, callerId, transferData.getData());
        }
        certificateCourseService.saveState(stateRequestList.all());
        callLoggerService.saveAll(callDurationList);

        log.info("Saved state for : callId=" + callId + "|callerId=" + callerId);
        return DUMMY;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transferdata/disconnect")
    @ResponseBody
    public String receiveIVRDataAtDisconnect(HttpServletRequest request) {
        final String callId = request.getParameter("callId");
        receiveCallData(request);

        dataPublishService.publishCallDisconnectEvent(callId);

        log.info("Call ended: " + callId);
        return DUMMY;
    }

}