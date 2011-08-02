package org.motechproject.bbcwt.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Milestone;
import org.motechproject.bbcwt.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MilestonesRepository extends AbstractCouchRepository<Milestone> {
    private HealthWorkersRepository healthWorkers;
    private ChaptersRespository chapters;
    private DateUtil dateProvider;

    @Autowired
    public MilestonesRepository(@Qualifier("bbcwtDbConnector") CouchDbConnector db, HealthWorkersRepository healthWorkers, ChaptersRespository chapters, DateUtil dateProvider) {
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
        HealthWorker healthWorker = healthWorkers.findByCallerId(healthWorkerCallerId);
        Chapter chapter = chapters.findByNumber(chapterNumber);

        if(healthWorker == null || chapter == null) {
            return null;
        }

        Lesson lesson = chapter.getLessonByNumber(lessonNumber);

        if(lesson == null) {
            return null;
        }

        Milestone milestone = this.findByHealthWorker(healthWorker);
        if(milestone==null) {
            milestone = new Milestone();
            milestone.setHealthWorkerId(healthWorker.getId());
        }
        milestone.setChapterId(chapter.getId());
        milestone.setLessonId(lesson.getId());
        milestone.setStartDate(dateProvider.getDate());
        milestone.setEndDate(null);

        this.add(milestone);
        return milestone;
    }

    //TODO: Throw exceptions in case no milestone or healthworker is found.
    public Milestone markLastMilestoneFinish(String healthWorkerCallerId) {
        HealthWorker healthWorker = healthWorkers.findByCallerId(healthWorkerCallerId);

        if(healthWorker == null) {
            return null;
        }

        Milestone milestone = this.findByHealthWorker(healthWorker);

        if(milestone == null) {
            return null;
        }

        milestone.setEndDate(dateProvider.getDate());

        this.add(milestone);
        return milestone;
    }

    public Milestone currentMilestoneWithLinkedReferences(String healthWorkerCallerId) {
        return null;
    }

    public Milestone nextMilestoneWithLinkedReferences(String healthWorkerCallerId) {
        return null;
    }
}