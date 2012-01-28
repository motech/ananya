package org.motechproject.bbcwt.ivr.jobaid.action;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.ivr.jobaid.CallFlowExecutor;
import org.motechproject.bbcwt.ivr.jobaid.IVRAction;
import org.motechproject.bbcwt.ivr.jobaid.JobAidFlowState;
import org.motechproject.bbcwt.service.JobAidContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LevelSelection extends JobAidAction {
    private static final Logger LOGGER = Logger.getLogger(LevelSelection.class);

    @Autowired
    private PlayLevel playLevel;
    @Autowired
    private IVRMessage messages;

    public LevelSelection() {

    }

    public LevelSelection(JobAidContentService jobAidContentService, PlayLevel playLevel, IVRMessage messages) {
        super(jobAidContentService);
        this.playLevel = playLevel;
        this.messages = messages;
    }

    @Override
    public void processRequest(IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder) {
        //do nothing
    }

    @Override
    public void playPrompt(IVRContext context, IVRRequest request, IVRDtmfBuilder dtmfBuilder) {
        JobAidCourse course = currentCourse();
        final String levelMenu = messages.absoluteFileLocation("jobaid/" + course.menu());
        LOGGER.info(String.format("Playing level menu: %s", levelMenu));
        dtmfBuilder.addPlayAudio(levelMenu);
        dtmfBuilder.withMaximumLengthOfResponse(1);
    }

    @Override
    public CallFlowExecutor.ProcessStatus validateInput(IVRContext context, IVRRequest request) {
        if(StringUtils.isEmpty(request.getData())) {
            LOGGER.info("No data received.");
            return CallFlowExecutor.ProcessStatus.NO_IP;
        }

        if(!StringUtils.isNumeric(request.getData())) {
            LOGGER.info("Non numeric data received.");
            return CallFlowExecutor.ProcessStatus.INVALID_IP;
        }

        int levelRequested = Integer.parseInt(request.getData());
        int noOfLevels = currentCourse().numberOfLevels();

        if(levelRequested < 0 || levelRequested > noOfLevels) {
            LOGGER.info(String.format("Level Requested: %d should be less than Number of Available Levels: %d", levelRequested, noOfLevels));
            return CallFlowExecutor.ProcessStatus.INVALID_IP;
        }

        return CallFlowExecutor.ProcessStatus.OK;
    }

    @Override
    public IVRAction processAndForwardToNextState(IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder) {
        int levelRequested = Integer.parseInt(request.getData());
        ((JobAidFlowState)context.flowSpecificState()).setLevel(levelRequested);
        return playLevel;
    }
}