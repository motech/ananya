package org.motechproject.bbcwt.ivr.jobaid.action;

import org.apache.log4j.Logger;
import org.motechproject.bbcwt.domain.Level;
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
public class PlayLevel extends JobAidAction {
    private static final Logger LOGGER = Logger.getLogger(PlayLevel.class);

    @Autowired
    private LevelSelection levelSelection;
    @Autowired
    private ChapterSelection chapterSelection;
    @Autowired
    private IVRMessage messages;

    public PlayLevel() {

    }

    public PlayLevel(JobAidContentService jobAidContentService, ChapterSelection chapterSelection, LevelSelection levelSelection, IVRMessage messages) {
        super(jobAidContentService);
        this.levelSelection = levelSelection;
        this.chapterSelection = chapterSelection;
        this.messages = messages;
    }

    @Override
    public void processRequest(IVRContext context, IVRRequest request, IVRResponseBuilder responseBuilder) {
        int enteredLevel = currentLevelNumber(context);
        Level level = currentLevel(context);
        if(level.introduction()!=null) {
            String levelIntroduction = messages.absoluteFileLocation("jobAid/" + level.introduction());
            LOGGER.info(String.format("Playing introduction for level %d : %s.", enteredLevel, levelIntroduction));
            responseBuilder.addPlayAudio(levelIntroduction);
        }
        else {
            LOGGER.info(String.format("There is no introduction for level %%d.", enteredLevel));
        }
    }

    @Override
    public void playPrompt(IVRContext context, IVRRequest request, IVRDtmfBuilder dtmfBuilder) {
        //No prompt to be played, do Nothing
    }

    @Override
    public CallFlowExecutor.ProcessStatus validateInput(IVRContext context, IVRRequest request) {
        return CallFlowExecutor.ProcessStatus.OK;
    }

    @Override
    public IVRAction processAndForwardToNextState(IVRContext context, IVRRequest request) {
        int enteredLevel = currentLevelNumber(context);
        Level level = currentLevel(context);
        if(!level.hasChapters()) {
            LOGGER.info(String.format("There are no chapters in the level: %d, hence going back to the level menu.", enteredLevel));
            return levelSelection;
        }
        return chapterSelection;
    }
}