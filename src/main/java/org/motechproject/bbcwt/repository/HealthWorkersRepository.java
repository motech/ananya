package org.motechproject.bbcwt.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.motechproject.bbcwt.domain.HealthWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class HealthWorkersRepository extends CouchDbRepositorySupport<HealthWorker> {
    @Autowired
    public HealthWorkersRepository(@Qualifier("bbcwtDbConnector") CouchDbConnector db) {
        super(HealthWorker.class, db);
        initStandardDesignDocument();
    }


}