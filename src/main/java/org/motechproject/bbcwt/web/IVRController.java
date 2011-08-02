package org.motechproject.bbcwt.web;

import org.apache.log4j.Logger;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/ivr")
public class IVRController {
    private static Logger LOG = Logger.getLogger(IVRController.class);

    public IVRController() {
    }

    @RequestMapping(value = "reply", method = RequestMethod.GET)
    public String reply(@ModelAttribute IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);

        LOG.info("Interaction for: " + ivrRequest.getCid());

        if(session == null) {
            LOG.info("There is no session, new call, forwarding.");
            return "forward:/newcall";
        }
        else {
            LOG.info("Should continue interaction.");
            return "forward:/ivr/continueInteraction";
        }
    }

    @RequestMapping(value = "continueInteraction", method = RequestMethod.GET)
    public String continueInteraction(@ModelAttribute IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String nextController = (String)request.getSession().getAttribute(IVR.Attributes.NEXT_INTERACTION);
        LOG.info("Continuing to next interaction at: " + nextController);
        return "forward:" + nextController;
    }
}
