package org.motechproject.ananya.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.ananya.domain.AudioTrackerLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllAudioTrackerLogs extends MotechBaseRepository<AudioTrackerLog> {
    @Autowired
    public AllAudioTrackerLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(AudioTrackerLog.class, dbCouchDbConnector);
    }

    @View(name = "by_callId", map = "function(doc) { if (doc.type=='AudioTrackerLog') { emit([doc.callId], doc); } }")
    public AudioTrackerLog findByCallId(String callId) {
        ViewQuery query = createQuery("by_callId").key(ComplexKey.of(callId));
        List<AudioTrackerLog> result = db.queryView(query, AudioTrackerLog.class);
        if (result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    @View(name = "by_invalid_msisdn", map="function(doc) { if(doc.type === 'AudioTrackerLog' && doc.callerId && doc.callerId.indexOf('E') !== -1 ) {emit(doc.callId, doc._id)} }")
    public void deleteAudioTrackerLogsForInvalidMsisdns() {
        List<AudioTrackerLog> invalidAudioTrackerLogs = queryView("by_invalid_msisdn");
        List<BulkDeleteDocument> bulkDeleteDocuments = new ArrayList<>();
        for (AudioTrackerLog invalidAudioTrackerLog : invalidAudioTrackerLogs) {
            bulkDeleteDocuments.add(BulkDeleteDocument.of(invalidAudioTrackerLog));
        }
        db.executeBulk(bulkDeleteDocuments);
    }

    public void deleteFor(String callId) {
        AudioTrackerLog audioTrackerLog = findByCallId(callId);
        if (audioTrackerLog != null) {
            remove(audioTrackerLog);
        }
    }
}
