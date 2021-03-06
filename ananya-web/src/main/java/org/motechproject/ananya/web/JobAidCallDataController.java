package org.motechproject.ananya.web;

import org.motechproject.ananya.contract.JobAidServiceRequest;
import org.motechproject.ananya.service.JobAidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JobAidCallDataController {

    private static Logger log = LoggerFactory.getLogger(JobAidCallDataController.class);

    private JobAidService jobAidService;

    @Autowired
    public JobAidCallDataController(JobAidService jobAidService) {
        this.jobAidService = jobAidService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/jobaid/transferdata/disconnect")
    @ResponseBody
    public String handleDisconnect(@RequestParam String callId,
                                   @RequestParam String callerId,
                                   @RequestParam String operator,
                                   @RequestParam String circle,
                                   @RequestParam String calledNumber,
                                   @RequestParam String language,
                                   @RequestParam String dataToPost,
                                   @RequestParam String promptList,
                                   @RequestParam Integer callDuration) {

        JobAidServiceRequest jobAidServiceRequest = new JobAidServiceRequest(callId, callerId, calledNumber)
                .withCallDuration(callDuration).withPromptList(promptList).withOperator(operator).withCircle(circle)
                .withJson(dataToPost).withLanguage(language);

        jobAidService.handleDisconnect(jobAidServiceRequest);

        log.info(callId + "- jobaid call ended");
        return getReturnVxml();
    }
    
     @RequestMapping(method = RequestMethod.POST, value = "/jobaid/nocapping/transferdata/disconnect")
    @ResponseBody
    public String handleDisconnectWithoutCapping(@RequestParam String callId,
                                   @RequestParam String callerId,
                                   @RequestParam String operator,
                                   @RequestParam String circle,
                                   @RequestParam String calledNumber,
                                   @RequestParam String language,
                                   @RequestParam String dataToPost,
                                   @RequestParam String promptList) {

        JobAidServiceRequest jobAidServiceRequest = new JobAidServiceRequest(callId, callerId, calledNumber)
                .withOperator(operator).withCircle(circle)
                .withJson(dataToPost).withLanguage(language).withPromptList(promptList);

        jobAidService.handleDisconnectWithoutCapping(jobAidServiceRequest);

        log.info(callId + "- jobaid call ended");
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
