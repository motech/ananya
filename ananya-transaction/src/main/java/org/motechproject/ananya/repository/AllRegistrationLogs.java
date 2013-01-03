package org.motechproject.ananya.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllRegistrationLogs extends MotechBaseRepository<RegistrationLog> {

    @Autowired
    public AllRegistrationLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(RegistrationLog.class, dbCouchDbConnector);
    }

    @GenerateView
    public RegistrationLog findByCallId(String callId) {
        ViewQuery viewQuery = createQuery("by_callId").key(callId).includeDocs(true);
        List<RegistrationLog> logs = db.queryView(viewQuery, RegistrationLog.class);
        if (logs == null || logs.isEmpty()) return null;
        return logs.get(0);
    }

    @View(name = "by_invalid_msisdn", map="function(doc) { if(doc.type === 'RegistrationLog' && doc.callerId && doc.callerId.indexOf('E') !== -1 ) {emit(doc.callId, doc._id)} }")
    public void deleteRegistrationLogsForInvalidMsisdns() {
        List<RegistrationLog> registrationLogs = queryView("by_invalid_msisdn");
        List<BulkDeleteDocument> bulkDeleteDocuments = new ArrayList<>();
        for (RegistrationLog registrationLog : registrationLogs) {
            bulkDeleteDocuments.add(BulkDeleteDocument.of(registrationLog));
        }
        db.executeBulk(bulkDeleteDocuments);
    }


    public void deleteFor(String callId) {
        RegistrationLog registrationLog = findByCallId(callId);
        if(null != registrationLog) {
            remove(registrationLog);
        }
    }
}