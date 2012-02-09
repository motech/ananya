package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.ananya.domain.log.RegistrationLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllRegistrationLogs extends MotechBaseRepository<RegistrationLog> {

    @Autowired
    public AllRegistrationLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(RegistrationLog.class, dbCouchDbConnector);
    }
}
