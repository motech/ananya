package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.*;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.repository.MilestonesRepository;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.powermock.api.mockito.PowerMockito.when;

public class StartQuizActionTest extends BaseActionTest {
    private StartQuizAction startQuizAction;

    @Mock
    private MilestonesRepository milestonesRepository;

    private HealthWorker healthWorker;

    @Before
    public void setup() {
        startQuizAction = new StartQuizAction(milestonesRepository, messages);

        healthWorker = new HealthWorker("9989989998");
    }

    @Test
    public void shouldForwardToFirstQuestionIfItExists() {
        Chapter currentChapter = new Chapter(1);
        Lesson ch1l1 = new Lesson(1, "Chapter 1 Lesson 1", "chapter 1 lesson 1 end menu");
        currentChapter.addLesson(ch1l1);
        Question q1l1 = new Question();
        currentChapter.addQuestion(q1l1);

        Milestone currentMilestone = new Milestone(healthWorker.getId(), currentChapter.getId(), ch1l1.getId(), null, new Date());
        currentMilestone.setChapter(currentChapter);

        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(healthWorker.getCallerId());
        final String MSG_START_OF_QUIZ = "You are about to start quiz for chapter ";
        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(currentMilestone);

        String nextAction = startQuizAction.handle(new IVRRequest(), request, response);

        assertThat(nextAction, is("forward:/chapter/" +currentChapter.getNumber() + "/question/1"));
    }

    @Test
    public void shouldForwardToNextChapterIfNoQuestionExistInCurrentChapter() {
        Chapter chapterWithNoQuestions = new Chapter(1);
        Lesson lesson = new Lesson(1, "Lesson 1", "Lesson 1 end menu");
        Milestone currentMilestone = new Milestone(healthWorker.getId(), chapterWithNoQuestions.getId(), lesson.getId(), null, new Date());
        currentMilestone.setChapter(chapterWithNoQuestions);

        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(healthWorker.getCallerId());
        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(currentMilestone);

        String nextAction = startQuizAction.handle(new IVRRequest(), request, response);
        assertThat(nextAction, is("forward:/startNextChapter"));
    }

}