package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVRContext;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.ivr.builder.IVRDtmfBuilder;
import org.motechproject.bbcwt.ivr.builder.IVRResponseBuilder;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/chapterEndAnswer")
public class LastLessonEndAnswerAction extends BaseAction{

    private MilestonesRepository milestonesRepository;

    private Map<Character, KeyPressResponseAction> keyPressActionMap;
    private KeyPressResponseAction invalidKeyPressResponseAction;

    @Autowired
    public LastLessonEndAnswerAction(MilestonesRepository milestonesRepository, IVRMessage messages) {
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
        this.invalidKeyPressResponseAction = new InvalidKeyPressResponseAction();
        initializeKeyPressActionMap();
    }

    private void initializeKeyPressActionMap() {
        keyPressActionMap = new HashMap(3);
        keyPressActionMap.put('1', new Key1ResponseAction());
        keyPressActionMap.put('2', new Key2ResponseAction());
        keyPressActionMap.put(NO_INPUT, new NoKeyPressResponseAction());
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        IVRContext.SessionAndIVRContextSynchronizer synchronizer = new IVRContext.SessionAndIVRContextSynchronizer();
        IVRContext ivrContext = synchronizer.buildIVRContext(session);

        char chosenOption = ivrInput(ivrRequest);
        KeyPressResponseAction keyPressResponseAction = determineActionToExecute(chosenOption);

        String forward = keyPressResponseAction.execute(ivrContext, ivrResponseBuilder(request), ivrDtmfBuilder(request));

        synchronizer.synchronizeSessionWithIVRContext(session, ivrContext);

        return forward;
    }

    private KeyPressResponseAction determineActionToExecute(char chosenOption) {
        KeyPressResponseAction responseAction = keyPressActionMap.get(chosenOption);
        return responseAction!=null?responseAction: invalidKeyPressResponseAction;
    }

    private class Key1ResponseAction implements KeyPressResponseAction {
        @Override
        public String execute(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            Milestone milestone = milestonesRepository.currentMilestoneWithLinkedReferences(ivrContext.getCallerId());

            Chapter currentChapter = milestone.getChapter();
            Lesson lastLesson = currentChapter.getLessonById(milestone.getLessonId());

            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();

            return "forward:/chapter/"+currentChapter.getNumber()+"/lesson/"+lastLesson.getNumber();
        }
    }

    private class Key2ResponseAction implements KeyPressResponseAction {
        @Override
        public String execute(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
            ivrContext.resetInvalidInputCount();
            ivrContext.resetNoInputCount();
            return "forward:/startQuiz";
        }
    }

    private class NoKeyPressResponseAction implements KeyPressResponseAction {
        @Override
        public String execute(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
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

    private class InvalidKeyPressResponseAction implements KeyPressResponseAction {
        @Override
        public String execute(IVRContext ivrContext, IVRResponseBuilder ivrResponseBuilder, IVRDtmfBuilder ivrDtmfBuilder) {
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