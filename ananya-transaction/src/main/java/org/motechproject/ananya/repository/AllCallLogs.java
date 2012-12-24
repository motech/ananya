package org.motechproject.ananya.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    @View(name = "by_invalid_msisdn", map="function(doc) { if(doc.type === 'CallLog' && doc.callerId && doc.callerId.indexOf('E') !== -1 ) {emit(doc.callId, doc._id)} }")
    public void deleteCallLogsForInvalidMsisdns() {
        List<CallLog> callLogs = queryView("by_invalid_msisdn");
        List<BulkDeleteDocument> bulkDeleteDocuments = new ArrayList<>();
        for (CallLog callLog : callLogs) {
            bulkDeleteDocuments.add(BulkDeleteDocument.of(callLog));
        }
        db.executeBulk(bulkDeleteDocuments);
    }
}
