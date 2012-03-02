package org.motechproject.ananya.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.ananya.domain.IvrFlow;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class AllCallLogs extends MotechBaseRepository<CallLog> {

    @Autowired
    public AllCallLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(CallLog.class, dbCouchDbConnector);
    }

    public CallLog addOrUpdate(CallLog log) {
        CallLog logFromDb = findByCallIdAndCallFlow(log.getCallId(), log.getIvrFlow());
        if(logFromDb == null) {
            add(log);
            return log;
        }
        if(log.getStartTime() !=null)
            logFromDb.setStartTime(log.getStartTime());
        if(log.getEndTime() != null)
            logFromDb.setEndTime(log.getEndTime());
        update(logFromDb);
        return logFromDb;
    }

    @GenerateView
    public Collection<CallLog> findByCallId(String callId) {
        ViewQuery viewQuery = createQuery("by_callId").key(callId).includeDocs(true);
        return db.queryView(viewQuery, CallLog.class);
    }

    @View(name = "by_callIdAndIvrFlow", map = "function(doc) { if (doc.type=='CallLog') { emit([doc.callId, doc.ivrFlow], doc); } }")
    public CallLog findByCallIdAndCallFlow(String callId, IvrFlow ivrFlow) {
        ViewQuery viewQuery = createQuery("by_callIdAndIvrFlow").key(ComplexKey.of(callId, ivrFlow)).includeDocs(true);
        List<CallLog> callDurations = db.queryView(viewQuery, CallLog.class);
        if (callDurations == null || callDurations.isEmpty()) return null;
        return callDurations.get(0);
    }

    public void delete(Collection<CallLog> callLogs) {
        for(CallLog log:callLogs){
            remove(log);
        }
    }
}
