package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.matcher.MilestoneMatcher;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.util.DateUtil;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class LessonEndAnswerActionTest extends BaseActionTest {

    private LessonEndAnswerAction lessonEndAnswerAction;

    @Mock
    private ChaptersRespository chaptersRespository;
    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private HealthWorkersRepository healthWorkersRepository;
    @Mock
    private DateUtil dateUtil;

    private String callerId;
    private Milestone currentMilestone;
    private HealthWorker healthWorker;
    private Chapter chapter;
    private Lesson currentLesson;

    @Before
    public void setUp() {
        callerId = "9999988888";
        healthWorker = new HealthWorker(callerId);
        healthWorker.setId("UniqueHealthWorkerId");

        chapter = new Chapter(1);
        chapter.setId("ChapterId1");
        Lesson lesson1 = new Lesson(1, "Lesson 1");
        currentLesson = new Lesson(2, "Lesson 2");
        Lesson lesson3 = new Lesson(3, "Lesson 3");
        chapter.addLesson(lesson1);
        chapter.addLesson(currentLesson);
        chapter.addLesson(lesson3);

        currentMilestone = new Milestone(healthWorker.getId(), chapter.getId(), currentLesson.getId(), new Date());

        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(callerId);
        when(healthWorkersRepository.findByCallerId(callerId)).thenReturn(healthWorker);
        when(milestonesRepository.findByHealthWorker(healthWorker)).thenReturn(currentMilestone);
        when(chaptersRespository.get(currentMilestone.getChapterId())).thenReturn(chapter);

        lessonEndAnswerAction = new LessonEndAnswerAction(healthWorkersRepository, chaptersRespository, milestonesRepository, dateUtil, messages);
    }

    @Test
    public void shouldSetTheEndDateInTheMilestone() {
        Date currentTime = new Date();
        when(dateUtil.getDate()).thenReturn(currentTime);

        IVRRequest ivrRequest = new IVRRequest(null, null, null, "1");

        lessonEndAnswerAction.handle(ivrRequest, request, response);

        verify(milestonesRepository, times(1)).add(currentMilestone);
        verify(milestonesRepository, times(1)).add(argThat(new MilestoneMatcher(currentMilestone.getHealthWorkerId(), currentMilestone.getChapterId(), currentMilestone.getLessonId(), currentMilestone.getStartDate(), currentTime)));
    }

    @Test
    public void shouldNavigateToTheLastChapterIfUserAnswers1() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "1");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);
        assertEquals(nextAction, "forward:/chapter/"+chapter.getNumber()+"/lesson/"+currentLesson.getNumber());
    }

    @Test
    public void shouldNavigateToTheNextChapterIfUserAnswers2() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "2");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);
        assertEquals(nextAction, "forward:/chapter/"+chapter.getNumber()+"/lesson/"+ (currentLesson.getNumber()+1) );
    }

    @Test
    public void shouldNavigateToTheEndLessonMenuIfUserAnswerIsInvalid() {
        IVRRequest ivrRequest = new IVRRequest(null, null, null, "4");

        String nextAction = lessonEndAnswerAction.handle(ivrRequest, request, response);
        assertEquals(nextAction, "forward:/lessonEndMenu");
    }
}