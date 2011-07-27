package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/newcall")
public class NewCallEventAction extends BaseAction {

    private HealthWorkersRepository healthWorkers;

    @Autowired
    public NewCallEventAction(IVRMessage messages, HealthWorkersRepository healthWorkers) {
        this.messages = messages;
        this.healthWorkers = healthWorkers;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        LOG.info("Handling new call.");
        HttpSession session = request.getSession();
        session.setAttribute(IVR.Attributes.CALLER_ID, ivrRequest.getCid());
        HealthWorker healthWorker = healthWorkers.findByCallerId(ivrRequest.getCid());
        if(healthWorker == null) {
            healthWorkers.add(new HealthWorker(ivrRequest.getCid()));

            IVRDtmfBuilder optionsBuilder = ivrDtmfBuilder().withPlayText(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_OPTIONS));
            IVRResponseBuilder ivrResponseBuilder = ivrResponseBuilder().withPlayText(messages.get(IVRMessage.BBCWT_IVR_NEW_USER_WC_MESSAGE)).withCollectDtmf(optionsBuilder.create());

            session.setAttribute(IVR.Attributes.NEXT_INTERACTION, "/helpMenuAnswer");
            return ivrResponseBuilder.create().getXML();
        }
        else {
            return responseWith(ivrRequest, IVRMessage.BBCWT_IVR_EXISTING_USER_WC_MESSAGE);
        }
    }
}
