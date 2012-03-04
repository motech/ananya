package org.motechproject.ananya.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.ananya.domain.CallFlowType;
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

    public CallLog addOrUpdate(CallLog callLog) {
        CallLog callLogFromDb = findByCallIdAndCallFlow(callLog.getCallId(), callLog.getCallFlowType());
        if (callLogFromDb == null) {
            add(callLog);
            return callLog;
        }
        if (callLog.getStartTime() != null)
            callLogFromDb.setStartTime(callLog.getStartTime());
        if (callLog.getEndTime() != null)
            callLogFromDb.setEndTime(callLog.getEndTime());
        update(callLogFromDb);
        return callLogFromDb;
    }

    @GenerateView
    public Collection<CallLog> findByCallId(String callId) {
        ViewQuery viewQuery = createQuery("by_callId").key(callId).includeDocs(true);
        return db.queryView(viewQuery, CallLog.class);
    }

    @View(name = "by_callIdAndCallFlowType", map = "function(doc) { if (doc.type=='CallLog') { emit([doc.callId, doc.callFlowType], doc); } }")
    public CallLog findByCallIdAndCallFlow(String callId, CallFlowType callFlowType) {
        ViewQuery viewQuery = createQuery("by_callIdAndCallFlowType").key(ComplexKey.of(callId, callFlowType)).includeDocs(true);
        List<CallLog> callDurations = db.queryView(viewQuery, CallLog.class);
        if (callDurations == null || callDurations.isEmpty()) return null;
        return callDurations.get(0);
    }

    public void delete(Collection<CallLog> callLogs) {
        for (CallLog log : callLogs) {
            remove(log);
        }
    }
}
