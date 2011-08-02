package org.motechproject.bbcwt.repository;

import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MilestonesRepositoryTest extends SpringIntegrationTest {
    @Autowired
    private MilestonesRepository milestonesRepository;
    @Autowired
    private HealthWorkersRepository healthWorkersRepository;
    @Autowired
    private ChaptersRespository chaptersRespository;
    @Mock
    private DateUtil dateProvider;

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
        markForDeletion(chapter);

        Date currentTime = new Date();
        Milestone milestone = new Milestone(healthWorker.getId(), chapter.getId(), lesson1.getId(), currentTime);
        milestonesRepository.add(milestone);
        markForDeletion(milestone);

        milestone.setLessonId(lesson2.getId());

        milestonesRepository.add(milestone);
        markForDeletion(milestone);

        Milestone mostRecentMilestone = milestonesRepository.findByHealthWorker(healthWorker);
        assertEquals("Milestone recorded should be that of the last completed lesson.", mostRecentMilestone.getLessonId(), lesson2.getId());
    }

    @Test
    public void markNewChapterStartShouldCreateAndReturnMilestoneIfNotAlreadyPresent() {
        HealthWorker healthWorker = new HealthWorker("9989989980");

        healthWorkersRepository.add(healthWorker);
        markForDeletion(healthWorker);

        Chapter chapter = new Chapter(1);
        Lesson lesson1 = new Lesson(1, "http://somewhere/lesson/1");
        chapter.addLesson(lesson1);

        chaptersRespository.add(chapter);
        markForDeletion(chapter);

        Milestone newMilestone = milestonesRepository.markNewChapterStart(healthWorker.getCallerId(), 1, 1);
        assertNotNull(newMilestone);

        Milestone newMilestoneFromDB = milestonesRepository.findByHealthWorker(healthWorker);
        markForDeletion(newMilestoneFromDB);

        assertNotNull(newMilestoneFromDB);

        assertEquals(newMilestoneFromDB.getHealthWorkerId(), healthWorker.getId());
        assertEquals(newMilestoneFromDB.getChapterId(), chapter.getId());
        assertEquals(newMilestoneFromDB.getLessonId(), lesson1.getId());
        assertNotNull(newMilestoneFromDB.getStartDate());
        assertNull(newMilestoneFromDB.getEndDate());

        assertNotNull(milestonesRepository.get(newMilestoneFromDB.getId()));
    }

    @Test
    public void markNewChapterStartShouldUpdateAndReturnMilestoneIfAlreadyPresent() {
        HealthWorker healthWorker = new HealthWorker("9989989980");

        healthWorkersRepository.add(healthWorker);
        markForDeletion(healthWorker);

        Chapter chapter1 = new Chapter(1);
        Lesson lesson1 = new Lesson(1, "http://somewhere/lesson/1");
        chapter1.addLesson(lesson1);
        chaptersRespository.add(chapter1);
        markForDeletion(chapter1);

        Chapter chapter2 = new Chapter(2);
        Lesson lesson2 = new Lesson(1, "http://somewhere/lesson/2");
        chapter2.addLesson(lesson2);
        chaptersRespository.add(chapter2);
        markForDeletion(chapter2);

        Milestone newMilestone = milestonesRepository.markNewChapterStart(healthWorker.getCallerId(), 1, 1);
        markForDeletion(newMilestone);
        milestonesRepository.markLastMilestoneFinish(healthWorker.getCallerId());

        Milestone updatedMilestoneReturned = milestonesRepository.markNewChapterStart(healthWorker.getCallerId(), 2, 1);
        markForDeletion(updatedMilestoneReturned);
        assertNotNull(updatedMilestoneReturned);

        Milestone updatedMilestoneFromDB = milestonesRepository.findByHealthWorker(healthWorker);
        assertNotNull(updatedMilestoneFromDB);
        assertEquals(updatedMilestoneFromDB.getId(), newMilestone.getId());
        assertEquals(updatedMilestoneFromDB.getHealthWorkerId(), healthWorker.getId());
        assertEquals(updatedMilestoneFromDB.getChapterId(), chapter2.getId());
        assertEquals(updatedMilestoneFromDB.getLessonId(), lesson2.getId());
        assertNotNull(updatedMilestoneFromDB.getStartDate());
        assertNull(updatedMilestoneFromDB.getEndDate());
    }

    @Test
    public void markLastMilestoneFinishShouldSetTheEndDateOfExistingMilestone(){
        HealthWorker healthWorker = new HealthWorker("9989989980");

        healthWorkersRepository.add(healthWorker);
        markForDeletion(healthWorker);

        Chapter chapter = new Chapter(1);
        Lesson lesson1 = new Lesson(1, "http://somewhere/lesson/1");
        Lesson lesson2 = new Lesson(2, "http://somewhere/lesson/2");

        chapter.addLesson(lesson1);
        chapter.addLesson(lesson2);

        chaptersRespository.add(chapter);
        markForDeletion(chapter);

        Date currentTime = new Date();
        Milestone milestone = new Milestone(healthWorker.getId(), chapter.getId(), lesson1.getId(), currentTime);
        milestonesRepository.add(milestone);
        markForDeletion(milestone);

        Milestone finishedMilestoneReturned = milestonesRepository.markLastMilestoneFinish(healthWorker.getCallerId());
        markForDeletion(finishedMilestoneReturned);
        assertNotNull(finishedMilestoneReturned);

        Milestone finishedMilestoneFromDB = milestonesRepository.findByHealthWorker(healthWorker);
        assertTrue(finishedMilestoneFromDB.isAccomplished());
    }
}