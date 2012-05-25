package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCallLogs extends MotechBaseRepository<CallLog> {

    @Autowired
    public AllCallLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(CallLog.class, dbCouchDbConnector);
    }

    @GenerateView
    public CallLog findByCallId(String callId) {
        ViewQuery viewQuery = createQuery("by_callId").key(callId).includeDocs(true);
        List<CallLog> callLogs = db.queryView(viewQuery, CallLog.class);
        if(callLogs.size() > 0)
            return callLogs.get(0);
        return null;
    }
}
