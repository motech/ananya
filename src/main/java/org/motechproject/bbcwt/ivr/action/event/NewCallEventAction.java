package org.motechproject.bbcwt.ivr.action.event;

import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.action.BaseAction;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class NewCallEventAction extends BaseAction {

    private HealthWorkersRepository healthWorkers;

    @Autowired
    public NewCallEventAction(IVRMessage messages, HealthWorkersRepository healthWorkers) {
        this.messages = messages;
        this.healthWorkers = healthWorkers;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
//        session.setAttribute(IVR.Attributes.CALL_STATE, IVR.CallState.COLLECT_PIN);
//        session.setAttribute(IVR.Attributes.CALL_ID, ivrRequest.getSid());
        session.setAttribute(IVR.Attributes.CALLER_ID, ivrRequest.getCid());
        HealthWorker healthWorker = healthWorkers.findByCallerId(ivrRequest.getCid());
        if(healthWorker == null) {
            healthWorkers.add(new HealthWorker(ivrRequest.getCid()));
            return responseWith(ivrRequest, IVRMessage.BBCWT_IVR_NEW_USER_WC_MESSAGE);
        }
        else {
            return responseWith(ivrRequest, IVRMessage.BBCWT_IVR_EXISTING_USER_WC_MESSAGE);
        }
    }
}
