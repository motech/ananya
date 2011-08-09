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
import org.motechproject.bbcwt.repository.MilestonesRepository;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

public class RepeatLastChapterActionTest extends BaseActionTest {
    private RepeatLastChapterAction repeatLastChapter;

    @Mock
    private MilestonesRepository milestonesRepository;

    private HealthWorker healthWorker;
    private Chapter chapter1;
    private Milestone currentMilestone;

    @Before
    public void setup() {
        repeatLastChapter = new RepeatLastChapterAction(milestonesRepository);

        healthWorker = new HealthWorker("9989989998");

        chapter1 = new Chapter(1);
        Lesson ch1l1 = new Lesson(1, "Chapter 1 Lesson 1");
        chapter1.addLesson(ch1l1);

        currentMilestone = new Milestone(healthWorker.getId(), chapter1.getId(), ch1l1.getId(), null, new Date());
        currentMilestone.setChapter(chapter1);
    }

    @Test
    public void shouldForwardToFirstLessonInCurrentChapterIfItExists() {
        when(session.getAttribute(IVR.Attributes.CALLER_ID)).thenReturn(healthWorker.getCallerId());
        when(milestonesRepository.currentMilestoneWithLinkedReferences(healthWorker.getCallerId())).thenReturn(currentMilestone);

        String nextAction = repeatLastChapter.handle(new IVRRequest(), request, response);

        assertThat(nextAction, is("forward:/chapter/"+chapter1.getNumber()+"/lesson/1"));
    }
}