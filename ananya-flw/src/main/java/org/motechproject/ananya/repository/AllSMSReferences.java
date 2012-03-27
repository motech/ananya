package org.motechproject.ananya.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.SMSReference;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSMSReferences extends MotechBaseRepository<SMSReference> {

    @Autowired
    protected AllSMSReferences(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(SMSReference.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public SMSReference findByMsisdn(String msisdn) {
        ViewQuery viewQuery = createQuery("by_msisdn").key(msisdn).includeDocs(true);
        List<SMSReference> smsReferences = db.queryView(viewQuery, SMSReference.class);
        if (smsReferences == null || smsReferences.isEmpty()) return null;
        return smsReferences.get(0);
    }

}
