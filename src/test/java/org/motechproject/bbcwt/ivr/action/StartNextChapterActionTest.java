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
import org.motechproject.bbcwt.repository.ChaptersRepository;
import org.motechproject.bbcwt.repository.MilestonesRepository;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

public class StartNextChapterActionTest extends BaseActionTest {
    private StartNextChapterAction startNextChapter;
    @Mock
    private MilestonesRepository milestonesRepository;
    @Mock
    private ChaptersRepository chaptersRespository;

    private HealthWorker healthWorker;
    private Chapter chapter1;
    private Chapter chapter2;
    private Milestone currentMilestone;

    @Before
    public void setup() {
        startNextChapter = new StartNextChapterAction(milestonesRepository, chaptersRespository, messages);

        healthWorker = new HealthWorker("9989989998");

        chapter1 = new Chapter(1);
        Lesson ch1l1 = new Lesson(1, "Chapter 1 Lesson 1", "Chapter 1 lesson 1 end menu");
        chapter1.addLesson(ch1l1);
        chapter2 = new Chapter(2);
        Lesson ch2l1 = new Lesson(1, "Chapter 2 Lesson 1", "Chapter 2 Lesson 1 end menu");
        chapter2.addLesson(ch2l1);

        currentMilestone = new Milestone(healthWorker.getId(), chapter1.getId(), ch1l1.getId(), null, new Date());
        currentMilestone.setChapter(chapter1);

    }

    @Test
    public void shouldForwardToNextChapterIfItExists() {
        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(healthWorker.getCallerId());
        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(currentMilestone);
        when(chaptersRespository.findByNumber(chapter1.getNumber()+1)).thenReturn(chapter2);

        String nextAction = startNextChapter.handle(new IVRRequest(), request, response);

        assertThat(nextAction, is("forward:/chapter/"+chapter2.getNumber()+"/lesson/1"));
    }

    @Test
    public void shouldForwardToEndOfCourseIfAllChaptersAreDone() {
        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(healthWorker.getCallerId());
        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(currentMilestone);
        when(chaptersRespository.findByNumber(chapter1.getNumber()+1)).thenReturn(null);

        String nextAction = startNextChapter.handle(new IVRRequest(), request, response);

        assertThat(nextAction, is("forward:/endOfCourse"));
    }
}