package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.action.inputhandler.KeyPressHandler;
import org.motechproject.bbcwt.ivr.action.inputhandler.PlayHelpAction;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/lessonEndAnswer")
public class LessonEndAnswerAction extends AbstractPromptAnswerHandler {
    private MilestonesRepository milestonesRepository;

    @Autowired
    public LessonEndAnswerAction(MilestonesRepository milestonesRepository, IVRMessage messages) {
        super(messages);
        this.milestonesRepository = milestonesRepository;
    }

    @Override
    protected KeyPressHandler invalidInputHandler() {
        return new InvalidKeyPressResponseAction();
    }

    @Override
    protected void intializeKeyPressHandlerMap(final Map<Character, KeyPressHandler> keyPressHandlerMap) {
        keyPressHandlerMap.put('1', new RestartPreviousLessonAction());
        keyPressHandlerMap.put('2', new StartNextLessonAction());
        keyPressHandlerMap.put('%', new PlayHelpAction(messages, "forward:/lessonEndMenu"));
        keyPressHandlerMap.put(NO_INPUT, new NoKeyPressResponseAction());
    }

    class RestartPreviousLessonAction implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(ivrContext.getCallerId());

            Chapter currentChapter = milestone.getChapter();
            Lesson lastLesson = currentChapter.getLessonById(milestone.getLessonId());

            ivrContext.resetNoInputCount();
            ivrContext.resetInvalidInputCount();

            return "forward:/chapter/"+currentChapter.getNumber()+"/lesson/"+lastLesson.getNumber();
        }
    }

    class StartNextLessonAction implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(ivrContext.getCallerId());

            Chapter currentChapter = milestone.getChapter();
            Lesson lastLesson = currentChapter.getLessonById(milestone.getLessonId());
            Lesson nextLesson = currentChapter.nextLesson(lastLesson);

            ivrContext.resetNoInputCount();
            ivrContext.resetInvalidInputCount();

            return "forward:/chapter/"+currentChapter.getNumber()+"/lesson/"+ nextLesson.getNumber();
        }
    }

    class NoKeyPressResponseAction implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.incrementNoInputCount();
            int allowedNumberOfNoInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_NO_INPUTS));

            if(ivrContext.getNoInputCount() > allowedNumberOfNoInputs) {
                Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(ivrContext.getCallerId());

                Chapter currentChapter = milestone.getChapter();
                Lesson lastLesson = currentChapter.getLessonById(milestone.getLessonId());
                Lesson nextLesson = currentChapter.nextLesson(lastLesson);

                ivrContext.resetInvalidInputCount();
                ivrContext.resetNoInputCount();

                return "forward:/chapter/"+currentChapter.getNumber()+"/lesson/"+ nextLesson.getNumber();
            }

            return "forward:/lessonEndMenu";
        }
    }

    class InvalidKeyPressResponseAction implements KeyPressHandler {
        @Override
        public String execute(Character keyPressed, IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.incrementInvalidInputCount();
            int allowedNumberOfInvalidInputs = Integer.parseInt(messages.get(IVRMessage.ALLOWED_NUMBER_OF_INVALID_INPUTS));

            if(ivrContext.getInvalidInputCount() > allowedNumberOfInvalidInputs) {
                Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(ivrContext.getCallerId());

                Chapter currentChapter = milestone.getChapter();
                Lesson lastLesson = currentChapter.getLessonById(milestone.getLessonId());
                Lesson nextLesson = currentChapter.nextLesson(lastLesson);

                ivrContext.resetInvalidInputCount();
                ivrContext.resetNoInputCount();

                return "forward:/chapter/"+currentChapter.getNumber()+"/lesson/"+ nextLesson.getNumber();
            }

            ivrResponseBuilder.addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));
            return "forward:/lessonEndMenu";
        }
    }
}
