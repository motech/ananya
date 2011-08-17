package org.motechproject.bbcwt.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.bbcwt.domain.*;
import org.motechproject.bbcwt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.*;


public class MilestonesRepositoryTest extends SpringIntegrationTest {
    @Autowired
    private MilestonesRepository milestonesRepository;
    @Autowired
    private HealthWorkersRepository healthWorkersRepository;
    @Autowired
    private ChaptersRespository chaptersRespository;
    @Mock
    private DateUtil dateProvider;

    private HealthWorker healthWorker;
    private Chapter chapter;
    private Lesson lesson1;
    private Lesson lesson2;
    private Question question1;
    private Question question2;

    @Before
    public void setup() {
        String callerId = "9989989980";

        healthWorker = new HealthWorker(callerId);
        healthWorkersRepository.add(healthWorker);
        markForDeletion(healthWorker);

        chapter = new Chapter(1);
        lesson1 = new Lesson(1, "http://somewhere/lesson", "http://somewhere/lessonEndMenu");
        lesson2 = new Lesson(2, "http://somewhere/lesson/2", "http://somewhere/lessonEndMenu2");
        chapter.addLesson(lesson1);
        chapter.addLesson(lesson2);

        question1 = new Question(1, null, null, -1, null, null);
        question2 = new Question(2, null, null, -1, null, null);
        chapter.addQuestion(question1);
        chapter.addQuestion(question2);

        chaptersRespository.add(chapter);
        markForDeletion(chapter);
    }

    @Test
    public void shouldGiveBackMilestoneForHealthWorker() {

        Date currentDate = new Date();
        Milestone milestone = new Milestone(healthWorker.getId(), chapter.getId(), lesson1.getId(), null, currentDate);
        milestonesRepository.add(milestone);
        milestonesRepository.add(milestone);
        markForDeletion(milestone);

        Milestone noiseMilestone = new Milestone("noiseWorkerId", "somechapterId", "someLessonId", null, currentDate);
        milestonesRepository.add(noiseMilestone);
        milestonesRepository.add(noiseMilestone);
        markForDeletion(noiseMilestone);

        Milestone milestoneOfInterest = milestonesRepository.findByHealthWorker(healthWorker);
        assertNotNull("Milestone for a user who has it should not be null.", milestoneOfInterest);
        assertEquals("Milestone fetched should be that of the user queried.", milestoneOfInterest.getHealthWorkerId(), healthWorker.getId());
    }

    @Test
    public void addingAMilestoneForAUserShouldUpdateAnyExistingMilestoneForTheUser() {
        Date currentTime = new Date();
        Milestone milestone = new Milestone(healthWorker.getId(), chapter.getId(), lesson1.getId(), null, currentTime);
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
        Chapter chapter1 = new Chapter(1);
        Lesson lesson1 = new Lesson(1, "http://somewhere/lesson/1", "http://somewhere/lesson/1/endmenu");
        chapter1.addLesson(lesson1);
        chaptersRespository.add(chapter1);
        markForDeletion(chapter1);

        Chapter chapter2 = new Chapter(2);
        Lesson lesson2 = new Lesson(1, "http://somewhere/lesson/2", "http://somwhere/lesson/2/endmenu");
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
    public void markNewChapterStartShouldNullifyTheQuestionIdInMilestone() {
        milestonesRepository.markNewQuestionStart(healthWorker.getCallerId(), chapter.getNumber(), question1.getNumber());

        Milestone updatedMilestoneReturned = milestonesRepository.markNewChapterStart(healthWorker.getCallerId(), chapter.getNumber(), lesson1.getNumber());
        markForDeletion(updatedMilestoneReturned);

        Milestone updatedMilestoneFromDB = milestonesRepository.findByHealthWorker(healthWorker);
        assertNull(updatedMilestoneFromDB.getQuestionId());
    }

    @Test
    public void markNewQuestionStartShouldUpdateAndReturnMilestone() {
        Milestone questionStartMilestone = milestonesRepository.markNewQuestionStart(healthWorker.getCallerId(), chapter.getNumber(), question1.getNumber());
        assertNotNull(questionStartMilestone);

        Milestone questionStartMilestoneFromDB = milestonesRepository.findByHealthWorker(healthWorker);
        markForDeletion(questionStartMilestoneFromDB);

        assertNotNull(questionStartMilestoneFromDB);

        assertEquals(questionStartMilestoneFromDB.getHealthWorkerId(), healthWorker.getId());
        assertEquals(questionStartMilestoneFromDB.getChapterId(), chapter.getId());
        assertEquals(questionStartMilestoneFromDB.getLessonId(), null);
        assertEquals(questionStartMilestoneFromDB.getQuestionId(), question1.getId());
        assertNotNull(questionStartMilestoneFromDB.getStartDate());
        assertNull(questionStartMilestoneFromDB.getEndDate());
    }

    @Test
    public void markLastMilestoneFinishShouldSetTheEndDateOfExistingMilestone(){
        Date currentTime = new Date();
        Milestone milestone = new Milestone(healthWorker.getId(), chapter.getId(), lesson1.getId(), null, currentTime);
        milestonesRepository.add(milestone);
        markForDeletion(milestone);

        Milestone finishedMilestoneReturned = milestonesRepository.markLastMilestoneFinish(healthWorker.getCallerId());
        markForDeletion(finishedMilestoneReturned);
        assertNotNull(finishedMilestoneReturned);

        Milestone finishedMilestoneFromDB = milestonesRepository.findByHealthWorker(healthWorker);
        assertTrue(finishedMilestoneFromDB.isAccomplished());
    }
}