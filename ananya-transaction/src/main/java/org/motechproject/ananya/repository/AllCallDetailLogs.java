package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCallDetailLogs extends MotechBaseRepository<CallDetailLog> {

    @Autowired
    public AllCallDetailLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(CallDetailLog.class, dbCouchDbConnector);
    }

    @GenerateView
    public CallDetailLog findByCallId(String callId) {
        ViewQuery viewQuery = createQuery("by_callId").key(callId).includeDocs(true);
        List<CallDetailLog> callDetailLogs = db.queryView(viewQuery, CallDetailLog.class);
        if (callDetailLogs == null || callDetailLogs.isEmpty()) return null;
        return callDetailLogs.get(0);
    }


}
