package org.motechproject.ananya.repository;

import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.ananya.domain.FrontLineWorkerKey;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AllFrontLineWorkerKeys extends MotechBaseRepository<FrontLineWorkerKey> {

    @Autowired
    protected AllFrontLineWorkerKeys(@Qualifier("ananyaDbConnector") CouchDbConnector db) {
        super(FrontLineWorkerKey.class, db);
        initStandardDesignDocument();
    }

    @View(name = "by_invalid_msisdn", map="function(doc) { if(doc.type === 'FrontLineWorkerKey' && doc._id.indexOf('E') !== -1 ) {emit(doc._id)} }")
    public void deleteFLWsWithInvalidMsisdn() {
        List<FrontLineWorkerKey> invalidFLWs = queryView("by_invalid_msisdn");
        List<BulkDeleteDocument> bulkDeleteDocuments = new ArrayList<>();
        for (FrontLineWorkerKey invalidFLW : invalidFLWs) {
            bulkDeleteDocuments.add(BulkDeleteDocument.of(invalidFLW));
        }
        db.executeBulk(bulkDeleteDocuments);
    }
}
