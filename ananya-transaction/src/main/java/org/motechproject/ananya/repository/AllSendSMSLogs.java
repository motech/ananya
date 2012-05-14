package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.SendSMSLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSendSMSLogs extends MotechBaseRepository<SendSMSLog> {
    
    @Autowired
    public AllSendSMSLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(SendSMSLog.class, dbCouchDbConnector);
    }

    @GenerateView
    public SendSMSLog findByCallerId(String callerId) {
        ViewQuery viewQuery = createQuery("by_callerId").key(callerId).includeDocs(true);
        List<SendSMSLog> logs = db.queryView(viewQuery, SendSMSLog.class);
        if (logs == null || logs.isEmpty()) return null;
        return logs.get(0);
    }

    public void deleteFor(String callerId) {
        SendSMSLog sendSMSLog = findByCallerId(callerId);
        if(null != sendSMSLog){
            remove(sendSMSLog);
        }
    }
}
