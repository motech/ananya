package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllFrontLineWorkers extends MotechBaseRepository<FrontLineWorker> {

    @Autowired
    protected AllFrontLineWorkers(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(FrontLineWorker.class, db);
    }
}
