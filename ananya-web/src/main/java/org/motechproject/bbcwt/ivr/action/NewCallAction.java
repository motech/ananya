package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(NewCallAction.LOCATION)
public class NewCallAction extends BaseAction {
    public static final String LOCATION = "/newcall";

    private HealthWorkersRepository healthWorkers;

    @Autowired
    public NewCallAction(IVRMessage messages, HealthWorkersRepository healthWorkers) {
        this.messages = messages;
        this.healthWorkers = healthWorkers;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        LOG.info("Handling new call.");

        HttpSession session = request.getSession();

        String callerId = ivrRequest.getCid();

        session.setAttribute(IVR.Attributes.CALLER_ID, callerId);
        HealthWorker healthWorker = healthWorkers.findByCallerId(callerId);

        if(healthWorker == null) {
            return "forward:" + IntroductionAction.LOCATION;
        }
        else {
            return "forward:" + ExistingUserAction.LOCATION;
        }
    }
}