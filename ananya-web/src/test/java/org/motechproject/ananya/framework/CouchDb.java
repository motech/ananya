package org.motechproject.ananya.framework;

import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.AllOperators;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@Repository
public class CouchDb {

    @Autowired
    private AllLocations allLocations;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    private AllOperators allOperators;

    @Autowired
    private AllSMSReferences allSMSReferences;

    public CouchDb confirmPartiallyRegistered(String callerId, String operator) {
        FrontLineWorker worker = allFrontLineWorkers.findByMsisdn(callerId);
        assertNotNull(worker);
        assertEquals(operator, worker.getOperator());
        return this;
    }

    public CouchDb confirmUsage(String callerId, Integer currentUsage, Integer maxUsage) {
        FrontLineWorker worker = allFrontLineWorkers.findByMsisdn(callerId);
        Operator operator = allOperators.findByName(worker.getOperator());
        assertEquals(new Integer(currentUsage * 60 * 1000), worker.getCurrentJobAidUsage());
        assertEquals(new Integer(maxUsage * 60 * 1000), operator.getAllowedUsagePerMonth());
        return this;
    }
    
    

    public void clearFLWData(String callerId) {
        FrontLineWorker byMsisdn = allFrontLineWorkers.findByMsisdn(callerId);
        if(byMsisdn == null) return;
        allFrontLineWorkers.remove(byMsisdn);
    }

    public void clearSMSReferences(String callerId) {
        SMSReference smsReference = allSMSReferences.findByMsisdn(callerId);
        allSMSReferences.remove(smsReference);
    }

    public CouchDb confirmBookmarkUpdated(String callerId, BookMark playCourseResultBookMark) {
        FrontLineWorker worker = allFrontLineWorkers.findByMsisdn(callerId);
        assertEquals(playCourseResultBookMark , worker.bookMark());
        return this;
    }

    public CouchDb confirmScoresSaved(String callerId, ReportCard reportCard) {
        FrontLineWorker worker = allFrontLineWorkers.findByMsisdn(callerId);
        assertEquals(reportCard.scores(), worker.reportCard().scores());
        return this;
    }
}
