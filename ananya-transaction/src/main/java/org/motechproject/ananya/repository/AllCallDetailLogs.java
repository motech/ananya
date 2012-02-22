package org.motechproject.ananya.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.ananya.domain.CallDetailLog;
import org.motechproject.ananya.domain.CallEvent;
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


    public boolean addIfAbsent(CallDetailLog log) {
        CallDetailLog logFromDb = findByCallIdAndCallEvent(log.getCallId(), log.getCallEvent());
        if(logFromDb == null) {
            add(log);
            return true;
        }
        return false;
    }

    @View(name = "by_callIdAndCallEvent", map = "function(doc) { if (doc.type=='CallDetailLog') { emit([doc.callId, doc.callEvent], doc); } }")
    private CallDetailLog findByCallIdAndCallEvent(String callId, CallEvent callEvent) {
        ViewQuery viewQuery = createQuery("by_callIdAndCallEvent").key(ComplexKey.of(callId, callEvent)).includeDocs(true);
        List<CallDetailLog> callDetailLogs = db.queryView(viewQuery, CallDetailLog.class);
        if (callDetailLogs == null || callDetailLogs.isEmpty()) return null;
        return callDetailLogs.get(0);
    }
}
