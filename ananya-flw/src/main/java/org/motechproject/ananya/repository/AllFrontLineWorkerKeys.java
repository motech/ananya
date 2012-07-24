package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.ananya.domain.FrontLineWorkerKey;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllFrontLineWorkerKeys extends MotechBaseRepository<FrontLineWorkerKey> {

    @Autowired
    protected AllFrontLineWorkerKeys(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(FrontLineWorkerKey.class, db);
        initStandardDesignDocument();
    }

}
