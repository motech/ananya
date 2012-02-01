package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllLocations extends MotechBaseRepository<Location> {

    @Autowired
    protected AllLocations(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(Location.class, db);
        initStandardDesignDocument();
    }
}
