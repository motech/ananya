package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.ananya.domain.FailedRecordsProcessingState;
import org.motechproject.ananya.domain.FrontLineWorkerKey;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllFailedRecordsProcessingStates extends MotechBaseRepository<FailedRecordsProcessingState> {

    @Autowired
    protected AllFailedRecordsProcessingStates(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(FailedRecordsProcessingState.class, db);
        initStandardDesignDocument();
    }
}