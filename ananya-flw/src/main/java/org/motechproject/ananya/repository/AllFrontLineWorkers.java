package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllFrontLineWorkers extends MotechBaseRepository<FrontLineWorker> {

    @Autowired
    protected AllFrontLineWorkers(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(FrontLineWorker.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public FrontLineWorker findByMsisdn(String msisdn) {
        ViewQuery viewQuery = createQuery("by_msisdn").key(msisdn).includeDocs(true);
        List<FrontLineWorker> workers = db.queryView(viewQuery, FrontLineWorker.class);
        if (workers == null || workers.isEmpty()) return null;
        return workers.get(0);
    }

    public List<FrontLineWorker> getAllForMsisdn(String msisdn) {
        ViewQuery viewQuery = createQuery("by_msisdn").key(msisdn).includeDocs(true);
        List<FrontLineWorker> workers = db.queryView(viewQuery, FrontLineWorker.class);
        if (workers == null || workers.isEmpty()) return null;
        return workers;
    }
}
