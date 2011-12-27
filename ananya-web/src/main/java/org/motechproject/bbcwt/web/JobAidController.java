package org.motechproject.bbcwt.web;

import org.apache.log4j.Logger;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.ivr.jobaid.CallFlowExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/ivr/jobAid")
public class JobAidController {
    private static Logger LOG = Logger.getLogger(JobAidController.class);
    private CallFlowExecutor callFlowExecutor;

    @Autowired
    public JobAidController(CallFlowExecutor callFlowExecutor) {
        this.callFlowExecutor = callFlowExecutor;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String reply(@ModelAttribute IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        IVRContext.SessionAndIVRContextSynchronizer synchronizer = new IVRContext.SessionAndIVRContextSynchronizer();
        IVRContext ivrContext = synchronizer.buildIVRContext(session);

        IVRResponseBuilder responseBuilder = callFlowExecutor.execute(ivrContext, ivrRequest);

        synchronizer.synchronizeSessionWithIVRContext(session, ivrContext);

        if(responseBuilder == null) {
            LOG.info("Invalidating session and returning a blank string.");
            session.invalidate();
            return "";
        } else {
            final String responseXML = responseBuilder.create().getXML();
            LOG.info("Returning response: \n" + responseXML);
            return responseXML;
        }
    }
}