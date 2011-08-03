package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.matcher.HealthWorkerCallerIdMatcher;
import org.motechproject.bbcwt.matcher.MilestoneMatcher;
import org.motechproject.bbcwt.repository.ChaptersRespository;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.util.DateUtil;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class ChapterActionTest extends BaseActionTest {
    private ChapterAction chapterAction;

    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private ChaptersRespository chaptersRespository;
    @Mock
    private HealthWorkersRepository healthWorkersRepository;
    @Mock
    private DateUtil dateUtil;

    private int chapterNumber;
    private int lessonNumber;
    private String callerId;
    private Chapter chapter;
    private Lesson lesson;
    private HealthWorker healthWorker;

    @Before
    public void setup()
    {
        chapterAction = new ChapterAction(healthWorkersRepository, chaptersRespository, milestonesRepository, dateUtil, messages);

        chapterNumber = 1;
        lessonNumber = 2;
        callerId = "9999988888";

        chapter = new Chapter(chapterNumber);
        lesson = new Lesson(lessonNumber, "This is lesson 1.");
        chapter.addLesson(lesson);

        healthWorker = new HealthWorker(callerId);

        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(callerId);
        when(healthWorkersRepository.findByCallerId(callerId)).thenReturn(healthWorker);
        when(chaptersRespository.findByNumber(chapterNumber)).thenReturn(chapter);

    }

    @Test
    public void shouldRegisterUserIfNotPresent() {
        String newNumber = "1-NEW-NUMBER";
        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(newNumber);
        when(healthWorkersRepository.findByCallerId(newNumber)).thenReturn(null);

        String nextAction = chapterAction.get(chapterNumber, lessonNumber, request, response);

        verify(healthWorkersRepository).add(argThat(new HealthWorkerCallerIdMatcher(newNumber)));
    }

    @Test
    public void shouldPlayTheLessonRequested()
    {
        String nextAction = chapterAction.get(chapterNumber, lessonNumber, request, response);

        verify(ivrResponseBuilder).addPlayText(lesson.getLocation());
        assertThat(nextAction, is("forward:/lessonEndMenu"));
    }

    @Test
    public void shouldSetTheMilestone(){
        healthWorker.setId("healthWorkerUniqueId");
        chapter.setId("chapterUniqueId");

        Date currentDate = new Date();
        when(dateUtil.getDate()).thenReturn(currentDate);

        String nextAction = chapterAction.get(chapterNumber, lessonNumber, request, response);

        verify(milestonesRepository).markNewChapterStart(healthWorker.getCallerId(), chapter.getNumber(), lesson.getNumber());
    }
}