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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class ExistingUserAction extends BaseAction{

    public static final String LOCATION = "/existingUserHandler";
    private MilestonesRepository milestonesRepository;

    @Autowired
    public ExistingUserAction(MilestonesRepository milestonesRepository, IVRMessage messages) {
        this.milestonesRepository = milestonesRepository;
        this.messages = messages;
    }

    @Override
    @RequestMapping(value= LOCATION, method = RequestMethod.GET)
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        final HttpSession session = request.getSession();
        final String callerId = healthWorkerCallerIdFromSession(session);

        final Milestone currentMilestone = milestonesRepository.currentMilestoneWithLinkedReferences(callerId);
        final Chapter currentChapter = currentMilestone.getChapter();
        final int currentChapterNumber = currentChapter.getNumber();

        if(currentMilestone.isAtLesson()) {
            final Lesson lastPlayedLesson = currentChapter.getLessonById(currentMilestone.getLessonId());
            final int lessonNumber = lastPlayedLesson.getNumber();

            int lessonNumberToNavigateTo;

            if(currentMilestone.isAccomplished()) {
                final boolean lessonPlayedIsTheLastInTheChapter = (currentChapter.nextLesson(lastPlayedLesson) == null);

                if(lessonPlayedIsTheLastInTheChapter){
                    return "forward:/startQuiz";
                } else {
                    lessonNumberToNavigateTo = lessonNumber+1;
                }
            } else {
                lessonNumberToNavigateTo = lessonNumber;
            }
            return "forward:/chapter/" + currentChapterNumber + "/lesson/" + lessonNumberToNavigateTo;
        }

        else {
            final Question lastPlayedQuestion = currentChapter.getQuestionById(currentMilestone.getQuestionId());
            final int lastPlayedQuestionNumber = lastPlayedQuestion.getNumber();

            int questionNumberToNavigateTo;
            if(currentMilestone.isAccomplished()) {
                final boolean questionPlayedIsTheLastInTheChapter = (currentChapter.nextQuestion(lastPlayedQuestion) == null);

                if(questionPlayedIsTheLastInTheChapter) {
                    return "forward:/informScore";
                } else {
                    questionNumberToNavigateTo = lastPlayedQuestionNumber + 1;
                }
            } else {
                questionNumberToNavigateTo = lastPlayedQuestionNumber;
            }
            return "forward:/chapter/"+ currentChapter.getNumber()+"/question/"+ questionNumberToNavigateTo;
        }
    }
}