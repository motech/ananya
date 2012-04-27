package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
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
}
