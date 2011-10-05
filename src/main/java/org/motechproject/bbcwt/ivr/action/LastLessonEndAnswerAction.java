package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.action.inputhandler.KeyPressHandler;
import org.motechproject.bbcwt.ivr.action.inputhandler.PlayHelpAction;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/chapterEndAnswer")
public class LastLessonEndAnswerAction extends AbstractPromptAnswerHandler {

    private MilestonesRepository milestonesRepository;

    @Autowired
    public LastLessonEndAnswerAction(MilestonesRepository milestonesRepository, IVRMessage messages) {
        super(messages);
        this.milestonesRepository = milestonesRepository;
    }

    @Override
    protected KeyPressHandler invalidInputHandler() {
        return new InvalidKeyPressResponseAction();
    }

    @Override
    protected void intializeKeyPressHandlerMap(final Map<Character, KeyPressHandler> keyPressHandlerMap) {
        keyPressHandlerMap.put('1', new Key1ResponseAction());
        keyPressHandlerMap.put('2', new Key2ResponseAction());
        keyPressHandlerMap.put('*', new PlayHelpAction(messages, "forward:/lessonEndMenu"));
        keyPressHandlerMap.put(NO_INPUT, new NoKeyPressResponseAction());
    }

    private class Key1ResponseAction implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(ivrContext.getCallerId());

            Chapter currentChapter = milestone.getChapter();
            Lesson lastLesson = currentChapter.getLessonById(milestone.getLessonId());

            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();

            return "forward:/chapter/"+currentChapter.getNumber()+"/lesson/"+lastLesson.getNumber();
        }
    }

    private class Key2ResponseAction implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();
            return "forward:/startQuiz";
        }
    }

    private class NoKeyPressResponseAction implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            ivrContext.incrementNoInputCount();
            int allowedNumberOfNoInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_NO_INPUTS));

            if(ivrContext.getNoInputCount() > allowedNumberOfNoInputs) {
                ivrContext.resetInvalidInputCount();
                ivrContext.resetNoInputCount();
                return "forward:/startNextChapter";
            }

            return "forward:/lessonEndMenu";
        }
    }

    private class InvalidKeyPressResponseAction implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder) {
            ivrContext.incrementInvalidInputCount();
            int allowedNumberOfInvalidInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_INVALID_INPUTS));

            if(ivrContext.getInvalidInputCount() > allowedNumberOfInvalidInputs) {
                ivrContext.resetInvalidInputCount();
                ivrContext.resetNoInputCount();
                return "forward:/startNextChapter";
            }

            ivrResponseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));
            return "forward:/lessonEndMenu";
        }
    }
}