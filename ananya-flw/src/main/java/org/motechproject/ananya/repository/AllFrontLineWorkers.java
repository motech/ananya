package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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

    public List<FrontLineWorker> getMsisdnsFrom(String startKey, int count) {
        return db.queryView(
                createQuery("by_msisdn").startKey(startKey).limit(count).includeDocs(true),
                FrontLineWorker.class);
    }

    public FrontLineWorker updateFlw(FrontLineWorker frontLineWorker) {
        try {
            update(frontLineWorker);
        } catch (UpdateConflictException e) {
            FrontLineWorker existingFlw = get(frontLineWorker.getId());
            frontLineWorker = existingFlw.updateWith(frontLineWorker);
            update(frontLineWorker);
            log.error(e.getMessage());
        }
        return frontLineWorker;
    }

    @GenerateView
    public FrontLineWorker findByFlwId(UUID flwId){
        ViewQuery viewQuery = createQuery("by_flwId").key(flwId).includeDocs(true);
        List<FrontLineWorker> workers = db.queryView(viewQuery, FrontLineWorker.class);
        if (workers == null || workers.isEmpty()) return null;
        return workers.get(0);
    }
}
