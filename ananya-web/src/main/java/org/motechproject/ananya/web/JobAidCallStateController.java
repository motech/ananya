package org.motechproject.ananya.web;

import org.motechproject.ananya.exceptions.AnanyaException;
import org.motechproject.ananya.request.JobAidPromptRequest;
import org.motechproject.ananya.service.JobAidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class JobAidCallStateController {

    private static Logger log = LoggerFactory.getLogger(TransferCallDataController.class);
    
    private JobAidService jobAidService;

    @Autowired
    public JobAidCallStateController(JobAidService jobAidService) {
        this.jobAidService = jobAidService;
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

        return "";
    }

}
