package org.motechproject.bbcwt.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.bbcwt.domain.*;
import org.motechproject.bbcwt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MilestonesRepository extends AbstractCouchRepository<Milestone> {
    private HealthWorkersRepository healthWorkers;
    private ChaptersRepository chapters;
    private DateUtil dateProvider;

    @Autowired
    public MilestonesRepository(@Qualifier("bbcwtDbConnector") CouchDbConnector db, HealthWorkersRepository healthWorkers, ChaptersRepository chapters, DateUtil dateProvider) {
        super(Milestone.class, db);
        this.healthWorkers = healthWorkers;
        this.chapters = chapters;
        this.dateProvider = dateProvider;
    }

    public Milestone findByHealthWorker(HealthWorker healthWorker) {
        return this.findByHealthWorkerId(healthWorker.getId());
    }

    @Override
    public void add(Milestone milestone) {
        if(this.findByHealthWorkerId(milestone.getHealthWorkerId()) == null ){
            super.add(milestone);
        }
        else {
            this.update(milestone);
        }
    }

    @GenerateView
    public Milestone findByHealthWorkerId(String healthWorkerId) {
        List<Milestone> milestones = queryView("by_healthWorkerId", healthWorkerId);
        if(milestones!=null && milestones.size() > 0) {
            return milestones.get(0);
        }
        return null;
    }

    //TODO: Throw exceptions in case healthworker, chapter or lessonNumber passed are not found.
    public Milestone markNewChapterStart(String healthWorkerCallerId, int chapterNumber, int lessonNumber) {
        Chapter chapter = chapters.findByNumber(chapterNumber);
        Lesson lesson = chapter.getLessonByNumber(lessonNumber);
        Milestone milestone = this.findByCallerId(healthWorkerCallerId);

        if(milestone == null || chapter == null || lesson == null) {
            return null;
        }

        milestone.setChapterId(chapter.getId());
        milestone.setQuestionId(null);
        milestone.setLessonId(lesson.getId());
        milestone.setStartDate(dateProvider.getDate());
        milestone.setEndDate(null);

        this.add(milestone);
        return milestone;
    }

    //TODO: Can this be made a CDB View?
    private Milestone findByCallerId(String healthWorkerCallerId) {
        HealthWorker healthWorker = healthWorkers.findByCallerId(healthWorkerCallerId);

        if(healthWorker == null) {
            return null;
        }

        Milestone milestone = this.findByHealthWorker(healthWorker);
        if(milestone==null) {
            milestone = new Milestone();
            milestone.setHealthWorkerId(healthWorker.getId());
        }
        return milestone;
    }

    public Milestone markNewQuestionStart(String healthWorkerCallerId, int chapterNumber, int questionNumber) {
        Chapter chapter = chapters.findByNumber(chapterNumber);
        Question question = chapter.getQuestionByNumber(questionNumber);
        Milestone milestone = this.findByCallerId(healthWorkerCallerId);

        if(milestone == null || chapter == null || question == null) {
            return null;
        }

        milestone.setChapterId(chapter.getId());
        milestone.setLessonId(null);
        milestone.setQuestionId(question.getId());
        milestone.setStartDate(dateProvider.getDate());
        milestone.setEndDate(null);

        this.add(milestone);
        return milestone;
    }

    //TODO: Throw exceptions in case no milestone or healthworker is found.
    public Milestone markLastMilestoneFinish(String healthWorkerCallerId) {
        Milestone milestone = milestoneForHealthWorker(healthWorkerCallerId);
        if (milestone == null) return null;

        milestone.setEndDate(dateProvider.getDate());

        this.add(milestone);
        return milestone;
    }

    private Milestone milestoneForHealthWorker(String healthWorkerCallerId) {
        HealthWorker healthWorker = healthWorkers.findByCallerId(healthWorkerCallerId);

        if(healthWorker == null) {
            return null;
        }

        Milestone milestone = this.findByHealthWorker(healthWorker);

        if(milestone != null) {
            milestone.setHealthWorker(healthWorker);
        }

        return milestone;
    }

    public Milestone currentMilestoneWithLinkedReferences(String healthWorkerCallerId) {
        Milestone currentMilestone = milestoneForHealthWorker(healthWorkerCallerId);

        if(currentMilestone != null) {
            Chapter chapter = chapters.get(currentMilestone.getChapterId());
            currentMilestone.setChapter(chapter);
        }

        return currentMilestone;
    }

}