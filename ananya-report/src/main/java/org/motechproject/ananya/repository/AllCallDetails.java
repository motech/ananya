package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.ananya.domain.*;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCallDetails extends MotechBaseRepository<CallDetail> {

    @Autowired
    protected AllCallDetails(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(CallDetail.class, db);
        initStandardDesignDocument();
    }
}
