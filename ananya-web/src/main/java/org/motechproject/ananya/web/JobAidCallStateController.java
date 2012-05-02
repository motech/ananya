package org.motechproject.ananya.web;

import org.motechproject.ananya.domain.CallDurationList;
import org.motechproject.ananya.domain.TransferData;
import org.motechproject.ananya.domain.TransferDataList;
import org.motechproject.ananya.request.AudioTrackerRequestList;
import org.motechproject.ananya.request.JobAidPromptRequest;
import org.motechproject.ananya.service.CallLoggerService;
import org.motechproject.ananya.service.JobAidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class JobAidCallStateController {

    private static Logger log = LoggerFactory.getLogger(JobAidCallStateController.class);

    private JobAidService jobAidService;
    private CallLoggerService callLoggerService;

    @Autowired
    public JobAidCallStateController(JobAidService jobAidService, CallLoggerService callLoggerService) {
        this.jobAidService = jobAidService;
        this.callLoggerService = callLoggerService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/jobaid/transferdata/disconnect")
    @ResponseBody
    public String updateJobAidCallData(HttpServletRequest request){
        final String callId = request.getParameter("callId");
        final String callerId = request.getParameter("callerId");
        final String calledNumber = request.getParameter("calledNumber");
        final String jsonData = request.getParameter("dataToPost");

        TransferDataList transferDataList = new TransferDataList(jsonData);
        AudioTrackerRequestList audioTrackerList = new AudioTrackerRequestList(callId, callerId);
        CallDurationList callDurationList = new CallDurationList(callId, callerId, calledNumber);

        for (TransferData transferData : transferDataList.all()) {
            if (transferData.isAudioTrackerState())
                audioTrackerList.add(transferData.getData(),transferData.getToken());
            else
                callDurationList.add(transferData.getData());
        }

        jobAidService.saveAudioTrackerState(audioTrackerList);
        callLoggerService.saveAll(callDurationList);

        log.info("Transfer data completed for: callId=" + callId + "|callerId=" + callerId);
        return getReturnVxml();

    }

    @RequestMapping(method = RequestMethod.GET, value = "/jobaid/updateprompt")
    @ResponseBody
    public String updateJobAidPrompts(HttpServletRequest request) {
        final String callId = request.getParameter("callId");
        final String callerId = request.getParameter("callerId");
        final String promptIds = request.getParameter("promptList");

        log.info("Jobaid prompt update - callId = " + callId +
                " | callerId = " + callerId + " | promptList = " + promptIds);

        jobAidService.updateJobAidPrompts(new JobAidPromptRequest(callId, callerId, promptIds));
        return validECMAResponse();
    }


    @RequestMapping(method = RequestMethod.GET, value = "/jobaid/updateusage")
    @ResponseBody
    public String updateJobAidUsage(HttpServletRequest request) {
        final String callId = request.getParameter("callId");
        final String callerId = request.getParameter("callerId");
        final Integer callDuration = Integer.valueOf(request.getParameter("callDuration"));

        log.info("Jobaid usage update - callId = " + callId +
                " | callerId = " + callerId + " | callDuration = " + callDuration);

        jobAidService.updateCurrentUsageAndSetLastAccessTimeForUser(callerId, callDuration);
        return validECMAResponse();
    }

    private String validECMAResponse() {
        return "var ananyaResponse = \"ANANYA_SUCCESS\";";
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
