package org.motechproject.ananya.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.ananya.domain.log.CertificationCourseLog;
import org.motechproject.ananya.domain.log.RegistrationLog;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCertificationCourseLogs extends MotechBaseRepository<CertificationCourseLog> {
    @Autowired
    public AllCertificationCourseLogs(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(CertificationCourseLog.class, dbCouchDbConnector);
    }

    @View(name = "by_callId_and_token", map = "function(doc) { if (doc.type=='CertificationCourseLog') { emit([doc.callId, doc.token], doc); } }")
    public CertificationCourseLog findByCallIdAndToken(String callId, String token) {
        ViewQuery query = createQuery("by_callId_and_token").key(ComplexKey.of(callId, token));
        List<CertificationCourseLog> result = db.queryView(query, CertificationCourseLog.class);
        if(result.size() > 0) {
            return result.get(0);
        }
        return null;
    }

    public boolean addIfAbsent(CertificationCourseLog courseLog) {
        final CertificationCourseLog logFromDb = findByCallIdAndToken(courseLog.getCallId(), courseLog.getToken());
        if(logFromDb == null) {
            add(courseLog);
            return true;
        }
        return false;
    }
}
