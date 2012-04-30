package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllAudioTrackerLogs extends MotechBaseRepository<AudioTrackerLog> {
    @Autowired
    public AllAudioTrackerLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(AudioTrackerLog.class, dbCouchDbConnector);
    }

    @GenerateView
    public AudioTrackerLog findByCallId(String callId) {
        ViewQuery viewQuery = createQuery("by_callId").key(callId).includeDocs(true);
        return db.queryView(viewQuery, AudioTrackerLog.class).get(0);
    }
}
