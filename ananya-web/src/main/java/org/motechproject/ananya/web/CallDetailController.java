package org.motechproject.ananya.web;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.domain.CallEvent;
import org.motechproject.ananya.service.CallDetailLoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CallDetailController {
    private CallDetailLoggerService callDetailLoggerService;
    private static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    public CallDetailController(CallDetailLoggerService callDetailLoggerService) {
        this.callDetailLoggerService = callDetailLoggerService;
    }

    @RequestMapping(method = RequestMethod.POST, value="/ananya/calldetail/add")
    @ResponseBody
    public String addCallDetailData(HttpServletRequest request){
        String callerId = request.getParameter("session.connection.remote.uri");

        CallDetailLog callDetailLog = new CallDetailLog("", callerId, CallEvent.REGISTRATION_START, DateTime.now(), "OM");

        callDetailLoggerService.Save(callDetailLog);

        return "Post Done";
    }
}
