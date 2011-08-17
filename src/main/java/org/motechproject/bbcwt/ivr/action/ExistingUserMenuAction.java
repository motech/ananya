package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/existingUserMenu")
public class ExistingUserMenuAction extends BaseAction{

    private MilestonesRepository milestonesRepository;

    @Autowired
    public ExistingUserMenuAction(MilestonesRepository milestonesRepository, IVRMessage messages) {
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        final String callerId = (String)request.getSession().getAttribute(IVR.Attributes.CALLER_ID);
        Milestone currentMilestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);
        if(currentMilestone.isAtLesson()) {
            if(currentMilestone.isAccomplished()) {

            }
            else {

            }
        }
        if(currentMilestone.isAtQuestion()) {
            if(currentMilestone.isAccomplished()) {

            }
            else {

            }
        }
        return ivrResponseBuilder(request).addPlayText("Still building the existing user flow.").withHangUp().create().getXML();
    }
}