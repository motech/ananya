package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCallDetailLogs extends MotechBaseRepository<CallDetailLog> {

    @Autowired
    public AllCallDetailLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(CallDetailLog.class, dbCouchDbConnector);
    }

}
