package org.motechproject.bbcwt.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.motechproject.bbcwt.domain.Milestone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MilestonesRepository extends AbstractCouchRepository<Milestone> {
    @Autowired
    public MilestonesRepository(@Qualifier("bbcwtDbConnector") CouchDbConnector db) {
        super(Milestone.class, db);
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
}