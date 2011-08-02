package org.motechproject.bbcwt.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.ivr.IVR;
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
    public void shouldPlayTheLessonRequested()
    {
        String nextAction = chapterAction.get(chapterNumber, lessonNumber, request, response);

        verify(ivrResponseBuilder).addPlayText(lesson.getLocation());
        assertThat(nextAction, is("forward:/helpMenu"));
    }

    @Test
    public void shouldSetTheMilestone(){
        healthWorker.setId("healthWorkerUniqueId");
        chapter.setId("chapterUniqueId");

        Date currentDate = new Date();
        when(dateUtil.getDate()).thenReturn(currentDate);

        String nextAction = chapterAction.get(chapterNumber, lessonNumber, request, response);

        verify(milestonesRepository).add(argThat(new MilestoneMatcher(healthWorker.getId(), chapter.getId(), lesson.getId(), currentDate)));
    }

    @Test
    public void shouldModifyTheExistingMilestone() {
        healthWorker.setId("healthWorkerUniqueId");
        chapter.setId("chapterUniqueId");

        Milestone existingMilestone = new Milestone();

        when(milestonesRepository.findByHealthWorker(healthWorker)).thenReturn(existingMilestone);
        Date currentDate = new Date();
        when(dateUtil.getDate()).thenReturn(currentDate);

        String nextAction = chapterAction.get(chapterNumber, lessonNumber, request, response);

        verify(milestonesRepository).add(existingMilestone);
        verify(milestonesRepository).add(argThat(new MilestoneMatcher(healthWorker.getId(), chapter.getId(), lesson.getId(), currentDate)));
    }

    public static class MilestoneMatcher extends ArgumentMatcher<Milestone> {
        private String chapterIdToMatch;
        private String lessonIdToMatch;
        private Date startDateToMatch;
        private String healthWorkerIdToMatch;

        private Milestone calledWith;

        public MilestoneMatcher(String healthWorkerId, String chapterIdToMatch, String lessonIdToMatch, Date startDate) {
            this.healthWorkerIdToMatch = healthWorkerId;
            this.chapterIdToMatch = chapterIdToMatch;
            this.lessonIdToMatch = lessonIdToMatch;
            this.startDateToMatch = startDate;
        }

        @Override
        public boolean matches(Object arg) {
            if(arg instanceof Milestone) {
                Milestone arg1 = (Milestone) arg;
                calledWith = arg1;

                return  StringUtils.equals(arg1.getHealthWorkerId(), healthWorkerIdToMatch) &&
                        StringUtils.equals(arg1.getChapterId(), chapterIdToMatch) &&
                        StringUtils.equals(arg1.getLessonId(), lessonIdToMatch) &&
                        (arg1.getStartDate()==null ? startDateToMatch == null : arg1.getStartDate().equals(startDateToMatch));
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            StringBuffer message = new StringBuffer();
            message.append("EXPECTED passed milestone to contain:\n");
            message.append("HealthWorker: " + healthWorkerIdToMatch + "\n");
            message.append("Chapter: " + chapterIdToMatch + "\n");
            message.append("Lesson: " + lessonIdToMatch + "\n");
            message.append("Date: " + startDateToMatch + "\n");
            message.append("BUT the milestone passed contains:");
            message.append("HealthWorker: " + calledWith.getHealthWorkerId() + "\n");
            message.append("Chapter: " + calledWith.getChapterId() + "\n");
            message.append("Lesson: " + calledWith.getLessonId() + "\n");
            message.append("Date: " + calledWith.getStartDate() + "\n");

            description.appendText(message.toString());
        }
    }


}