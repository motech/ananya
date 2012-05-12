package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllRegistrationLogs extends MotechBaseRepository<RegistrationLog> {

    @Autowired
    public AllRegistrationLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(RegistrationLog.class, dbCouchDbConnector);
    }

    @GenerateView
    public RegistrationLog findByCallerId(String callerId) {
        ViewQuery viewQuery = createQuery("by_callerId").key(callerId).includeDocs(true);
        List<RegistrationLog> logs = db.queryView(viewQuery, RegistrationLog.class);
        if (logs == null || logs.isEmpty()) return null;
        return logs.get(0);
    }

    public void deleteFor(String callerId) {

    }
}