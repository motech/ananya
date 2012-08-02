package org.motechproject.ananya.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSMSLogs extends MotechBaseRepository<SMSLog> {

    @Autowired
    public AllSMSLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(SMSLog.class, dbCouchDbConnector);
    }

    @GenerateView
    public SMSLog findByCallId(String callId) {
        ViewQuery viewQuery = createQuery("by_callId").key(callId).includeDocs(true);
        List<SMSLog> logs = db.queryView(viewQuery, SMSLog.class);
        if (logs == null || logs.isEmpty()) return null;
        return logs.get(0);
    }

    @View(name = "by_callerId_and_attempts", map = "function(doc) { if (doc.type==='SMSLog') { emit([doc.callerId, doc.courseAttempts], doc); } }")
    public SMSLog findByCallerIdAndAttempts(String callerId, Integer courseAttempts) {
        ViewQuery query = createQuery("by_callerId_and_attempts").key(ComplexKey.of(callerId, courseAttempts));
        List<SMSLog> smsLogs = db.queryView(query, SMSLog.class);
        return smsLogs == null || smsLogs.isEmpty() ? null : smsLogs.get(0);
    }

}
