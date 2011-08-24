package org.motechproject.bbcwt.ivr.action;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.domain.Question;
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
import javax.servlet.http.HttpSession;

@Controller
public class ExistingUserMenuAction extends BaseAction{

    private MilestonesRepository milestonesRepository;

    @Autowired
    public ExistingUserMenuAction(MilestonesRepository milestonesRepository, IVRMessage messages) {
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
    }

    @Override
    @RequestMapping(value="/existingUserMenu", method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        final HttpSession session = request.getSession();
        final String callerId = (String) session.getAttribute(IVR.Attributes.CALLER_ID);

        Milestone currentMilestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);

        final Chapter currentChapter = currentMilestone.getChapter();

        if(currentMilestone.isAtLesson()) {
            int chapterNumber = currentChapter.getNumber();
            int lessonNumber = currentChapter.getLessonById(currentMilestone.getLessonId()).getNumber();

            if(chapterNumber == 1 && lessonNumber == 1) {
                ivrDtmfBuilder(request).withPlayAudio(absoluteFileLocation(messages.get(IVRMessage.WELCOME_BACK_RESUME_CHAPTER_1_LESSON_1)));
                session.setAttribute(IVR.Attributes.NEXT_INTERACTION, "/helpMenuAnswer");
            }
            else {
                if(chapterNumber == 2 && lessonNumber == 1) {
                    recedeMilestoneToPreviousChapterLastQuestion(callerId);


                    ivrDtmfBuilder(request).withPlayAudio(absoluteFileLocation(messages.get(IVRMessage.WELCOME_BACK_RESUME_CHAPTER_2_LESSON_1)));
                    session.setAttribute(IVR.Attributes.NEXT_INTERACTION, "/endOfQuizMenuAnswer");
                }
                else {
                    session.setAttribute(IVR.Attributes.NEXT_INTERACTION, "/existingUserMenu/responseForLesson");
                    ivrDtmfBuilder(request).withPlayAudio(absoluteFileLocation(messages.get(IVRMessage.WELCOME_BACK_BETWEEN_LESSONS)));
                }
            }
        }

        if(currentMilestone.isAtQuestion()) {
            Question currentQuestion = currentChapter.getQuestionById(currentMilestone.getQuestionId());

            int previousAnsweredQuestion;
            if(currentMilestone.isAccomplished()) {
                previousAnsweredQuestion = currentQuestion.getNumber();
            }
            else {
                previousAnsweredQuestion = currentQuestion.getNumber()-1;
            }

            final boolean stillAnsweringFirstQuestion = previousAnsweredQuestion < 1;

            if(stillAnsweringFirstQuestion) {
                session.setAttribute(IVR.Attributes.NEXT_INTERACTION, "/existingUserMenu/responseForLessonOrQuiz");
                ivrDtmfBuilder(request).withPlayAudio(absoluteFileLocation(messages.get(IVRMessage.WELCOME_BACK_BETWEEN_LESSON_AND_QUIZ)));
            }
            else {
                ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.WELCOME_BACK_BETWEEN_QUIZ_QUESTIONS)));
                return "forward:/chapter/"+ currentChapter.getNumber()+"/question/"+ previousAnsweredQuestion;
            }
        }

        return "forward:/existingUserMenu/returnForIVR";
    }

    private void recedeMilestoneToPreviousChapterLastQuestion(String callerId) {
        //Needed to change milestone here, since the endOfQuizMenuAnswer will require this.
        milestonesRepository.markNewQuestionStart(callerId, 1, 4);
        milestonesRepository.markLastMilestoneFinish(callerId);
    }

    @RequestMapping(value="/existingUserMenu/returnForIVR", method = RequestMethod.GET)
    @ResponseBody
    public String returnForIVR(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        ivrResponseBuilder(request).withCollectDtmf(ivrDtmfBuilder(request).create());
        return ivrResponseBuilder(request).create().getXML();
    }

    @RequestMapping(value="/existingUserMenu/responseForLesson", method = RequestMethod.GET)
    public String responseForLesson(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        char chosenOption = ivrInput(ivrRequest);

        final HttpSession session = request.getSession();
        final String callerId = (String) session.getAttribute(IVR.Attributes.CALLER_ID);

        Milestone currentMilestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);
        final Chapter currentChapter = currentMilestone.getChapter();
        Lesson currentLesson = currentChapter.getLessonById(currentMilestone.getLessonId());

        if(chosenOption == '1') {
            return "forward:/chapter/"+currentChapter.getNumber()+"/lesson/"+ currentLesson.getNumber();
        } else {
            if(chosenOption == '2') {
                int prevLessonNumber = currentLesson.getNumber() - 1;
                prevLessonNumber = prevLessonNumber>0?prevLessonNumber:1;
                return "forward:/chapter/"+currentChapter.getNumber()+"/lesson/"+ prevLessonNumber;
            }
            else {
                if(chosenOption != NO_INPUT) {
                    ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));
                }
                return "forward:/existingUserMenu";
            }
        }
    }

    @RequestMapping(value="/existingUserMenu/responseForLessonOrQuiz", method = RequestMethod.GET)
    public String responseForLessonOrQuiz(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        char chosenOption = ivrInput(ivrRequest);

        final HttpSession session = request.getSession();
        final String callerId = (String) session.getAttribute(IVR.Attributes.CALLER_ID);

        Milestone currentMilestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);
        final Chapter currentChapter = currentMilestone.getChapter();

        if(chosenOption == '1') {
            int lastLessonNumber = currentChapter.getLessons().size();
            return "forward:/chapter/" + currentChapter.getNumber() + "/lesson/" + lastLessonNumber;
        } else {
            if(chosenOption == '2') {
                return "forward:/startQuiz";
            }
            else {
                if(chosenOption != NO_INPUT) {
                    ivrResponseBuilder(request).addPlayAudio(absoluteFileLocation(messages.get(IVRMessage.INVALID_INPUT)));
                }
                return "forward:/existingUserMenu";
            }
        }
    }
}