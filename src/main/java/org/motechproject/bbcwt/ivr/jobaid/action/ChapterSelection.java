package org.motechproject.bbcwt.ivr.jobaid.action;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.bbcwt.domain.Level;
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
public class ChapterSelection extends JobAidAction {

    private static final Logger LOGGER = Logger.getLogger(ChapterSelection.class);

    @Autowired
    private PlayChapter playChapter;
    @Autowired
    private IVRMessage messages;

    public ChapterSelection() {

    }

    public ChapterSelection(JobAidContentService jobAidContentService, PlayChapter playChapter, IVRMessage messages) {
        super(jobAidContentService);
        this.playChapter = playChapter;
        this.messages = messages;
    }

    @Override
    public void processRequest(IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder) {
        //Do nothing
    }

    @Override
    public void playPrompt(IVRContext context, IVRRequest request, IVRDtmfBuilder dtmfBuilder) {
        Level level = currentLevel(context);

        final String chapterMenu = messages.absoluteFileLocation("jobAid/" + level.menu());
        assembleReturnToStartOption(messages, dtmfBuilder);
        LOGGER.info(String.format("Playing chapterMenu menu: %s", chapterMenu));
        dtmfBuilder.addPlayAudio(chapterMenu);
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

        int chapterRequested = Integer.parseInt(request.getData());
        int noOfChapters = currentLevel(context).numberOfChapters();

        if(chapterRequested < 0 || chapterRequested > noOfChapters) {
            LOGGER.info(String.format("ChapterRequested Requested: %d should be less than Number of Available Chapters: %d", chapterRequested, noOfChapters));
            return CallFlowExecutor.ProcessStatus.INVALID_IP;
        }

        return CallFlowExecutor.ProcessStatus.OK;
    }

    @Override
    public IVRAction processAndForwardToNextState(IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder) {
        int chapterRequested = Integer.parseInt(request.getData());
        ((JobAidFlowState)context.flowSpecificState()).setChapter(chapterRequested);
        return playChapter;
    }

}