package org.motechproject.bbcwt.repository;

import org.junit.Test;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MilestonesRepositoryTest extends SpringIntegrationTest {
    @Autowired
    private MilestonesRepository milestonesRepository;
    @Autowired
    private HealthWorkersRepository healthWorkersRepository;
    @Autowired
    private ChaptersRespository chaptersRespository;

    @Test
    public void shouldGiveBackMilestoneForHealthWorker() {
        String callerId = "9989989980";

        HealthWorker healthWorker = new HealthWorker(callerId);
        healthWorkersRepository.add(healthWorker);
        markForDeletion(healthWorker);

        Chapter chapter = new Chapter(1);
        Lesson lesson = new Lesson(1, "http://somewhere/lesson");
        chapter.addLesson(lesson);
        chaptersRespository.add(chapter);
        markForDeletion(chapter);

        Date currentDate = new Date();
        Milestone milestone = new Milestone(healthWorker.getId(), chapter.getId(), lesson.getId(), currentDate);
        milestonesRepository.add(milestone);
        milestonesRepository.add(milestone);
        markForDeletion(milestone);

        Milestone noiseMilestone = new Milestone("noiseWorkerId", "somechapterId", "someLessonId", currentDate);
        milestonesRepository.add(noiseMilestone);
        milestonesRepository.add(noiseMilestone);
        markForDeletion(noiseMilestone);

        Milestone milestoneOfInterest = milestonesRepository.findByHealthWorker(healthWorker);
        assertNotNull("Milestone for a user who has it should not be null.", milestoneOfInterest);
        assertEquals("Milestone fetched should be that of the user queried.", milestoneOfInterest.getHealthWorkerId(), healthWorker.getId());
    }

    @Test
    public void addingAMilestoneForAUserShouldUpdateAnyExistingMilestoneForTheUser() {
        HealthWorker healthWorker = new HealthWorker("9989989980");

        healthWorkersRepository.add(healthWorker);
        markForDeletion(healthWorker);

        Chapter chapter = new Chapter(1);
        Lesson lesson1 = new Lesson(1, "http://somewhere/lesson/1");
        Lesson lesson2 = new Lesson(2, "http://somewhere/lesson/2");

        chapter.addLesson(lesson1);
        chapter.addLesson(lesson2);

        chaptersRespository.add(chapter);

        Date currentTime = new Date();
        Milestone milestone = new Milestone(healthWorker.getId(), chapter.getId(), lesson1.getId(), currentTime);
        milestonesRepository.add(milestone);

        milestone.setLessonId(lesson2.getId());

        milestonesRepository.add(milestone);

        Milestone mostRecentMilestone = milestonesRepository.findByHealthWorker(healthWorker);
        assertEquals("Milestone recorded should be that of the last completed lesson.", mostRecentMilestone.getLessonId(), lesson2.getId());

    }

}