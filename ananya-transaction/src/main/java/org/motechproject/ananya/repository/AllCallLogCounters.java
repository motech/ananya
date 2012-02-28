package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.CallLogCounter;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCallLogCounters extends MotechBaseRepository<CallLogCounter> {

    @Autowired
    protected AllCallLogCounters(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(CallLogCounter.class, dbCouchDbConnector);
    }

   @GenerateView
   public CallLogCounter findByCallId(String callId) {
       ViewQuery query = createQuery("by_callId").key(callId).includeDocs(true);
       List<CallLogCounter> callLogCounters = db.queryView(query, CallLogCounter.class);
       if (callLogCounters == null || callLogCounters.isEmpty()) return null;
       return callLogCounters.get(0);
   }
}
