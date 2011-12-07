package org.motechproject.bbcwt.ivr.jobaid.action;

import org.apache.log4j.Logger;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.ivr.jobaid.CallFlowExecutor;
import org.motechproject.bbcwt.ivr.jobaid.IVRAction;
import org.motechproject.bbcwt.service.JobAidContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayWelcome extends JobAidAction {
    private static final Logger LOGGER = Logger.getLogger(PlayWelcome.class);

    @Autowired
    private LevelSelection levelSelection;
    @Autowired
    private IVRMessage messages;

    public PlayWelcome() {

    }

    public PlayWelcome(JobAidContentService jobAidContentService, LevelSelection levelSelection, IVRMessage messages) {
        super(jobAidContentService);
        this.levelSelection = levelSelection;
        this.messages = messages;
    }

    public void processRequest(IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder){
        JobAidCourse jobAidCourse = currentCourse();
        if(context.firstInteractionInCall()) {
            final String welcomeMsg = messages.absoluteFileLocation("jobAid/" + jobAidCourse.introduction());
            LOGGER.info(String.format("Assembling the welcome message %s", welcomeMsg));
            responseBuilder.addPlayAudio(welcomeMsg);
        }
    }

    public void playPrompt(IVRContext context, IVRRequest request, IVRDtmfBuilder dtmfBuilder) {
    }

    public CallFlowExecutor.ProcessStatus validateInput(IVRContext context, IVRRequest request) {
        return CallFlowExecutor.ProcessStatus.OK;
    }

    public IVRAction processAndForwardToNextState(IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder) {
        return levelSelection;
    }
}

