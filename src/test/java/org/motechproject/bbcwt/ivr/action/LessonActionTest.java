package org.motechproject.bbcwt.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.ivr.IVR;
import org.motechproject.bbcwt.ivr.IVRMessage;
import org.motechproject.bbcwt.ivr.IVRRequest;
import org.motechproject.bbcwt.matcher.HealthWorkerCallerIdMatcher;
import org.motechproject.bbcwt.repository.ChaptersRepository;
import org.motechproject.bbcwt.repository.HealthWorkersRepository;
import org.motechproject.bbcwt.repository.MilestonesRepository;
import org.motechproject.bbcwt.util.DateUtil;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class LessonActionTest extends BaseActionTest {
    public static final String LOCATION_OF_CURRENT_HANDLER = "http://localhost/location/of/current/handler";
    public static final String HELP_HANDLER = "/helpHandler";
    private LessonAction chapterAction;

    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private ChaptersRepository chaptersRespository;
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
        chapterAction = new LessonAction(healthWorkersRepository, chaptersRespository, milestonesRepository, dateUtil, messages);

        chapterNumber = 1;
        lessonNumber = 2;
        callerId = "9999988888";

        chapter = new Chapter(chapterNumber);
        lesson = new Lesson(lessonNumber, "This is lesson 1.", "http://lesson/1/endMenu");
        chapter.addLesson(lesson);

        healthWorker = new HealthWorker(callerId);

        when(request.getServletPath()).thenReturn(LOCATION_OF_CURRENT_HANDLER);

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

        verify(ivrDtmfBuilder).addPlayAudio(CONTENT_LOCATION + lesson.getFileName());
        verify(ivrResponseBuilder).withCollectDtmf(collectDtmf);
    }

    @Test
    public void shouldSetTheNextInteractionToHelpHandlerWhenRenderingLesson() {
        String nextAction = chapterAction.get(chapterNumber, lessonNumber, request, response);
        verify(session).setAttribute(IVR.Attributes.NEXT_INTERACTION, LOCATION_OF_CURRENT_HANDLER + HELP_HANDLER);
    }

    @Test
    public void shouldSetThePreviousInteractionToCurrentLocationWhenRenderingLesson() {
        String nextAction = chapterAction.get(chapterNumber, lessonNumber, request, response);
        verify(session).setAttribute(IVR.Attributes.NAVIGATION_POST_HELP, LOCATION_OF_CURRENT_HANDLER);
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

    @Test
    public void shouldPlayHelpIfAnyKeyIsPressedWhilePlayingLesson() {
        final String HELP_AUDIO = "ivr_help.wav";
        when(messages.get(IVRMessage.IVR_HELP)).thenReturn(HELP_AUDIO);
        String nextAction = chapterAction.helpHandler(new IVRRequest(null, null, null, "1"), request, response);
        verify(ivrResponseBuilder).addPlayAudio(CONTENT_LOCATION + HELP_AUDIO);
    }

    @Test
    public void shouldForwardToPreviousLocationAfterPlayingHelp() {
        final String LOCATION_FROM_WHERE_HELP_WAS_REQUESTED = "/locationFromWhereHelpWasRequested";
        when(session.getAttribute(IVR.Attributes.NAVIGATION_POST_HELP)).thenReturn(LOCATION_FROM_WHERE_HELP_WAS_REQUESTED);
        String nextAction = chapterAction.helpHandler(new IVRRequest(null, null, null, "*"), request, response);
        assertEquals("Should navigate to previous action after help is played.", nextAction, "forward:" + LOCATION_FROM_WHERE_HELP_WAS_REQUESTED);
    }

    @Test
    public void shouldForwardToLessonEndMenuIfHelpIsNotRequested() {
        String nextAction = chapterAction.helpHandler(new IVRRequest(null, null, null, null), request, response);
        assertEquals("Should navigate to next action if help is not requested.", nextAction, "forward:/lessonEndMenu");
    }
}