package org.motechproject.bbcwt.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HealthWorkersRepository extends CouchDbRepositorySupport<HealthWorker> {
    @Autowired
    public HealthWorkersRepository(@Qualifier("bbcwtDbConnector") CouchDbConnector db) {
        super(HealthWorker.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public HealthWorker findByCallerId(String callerId) {
        List<HealthWorker> healthWorkers = queryView("by_callerId", callerId);
        if(healthWorkers!=null && !healthWorkers.isEmpty()) {
            return healthWorkers.get(0);
        }
        return null;
    }
}