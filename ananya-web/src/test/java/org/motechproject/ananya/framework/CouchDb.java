package org.motechproject.ananya.framework;

import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.AllOperators;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static junit.framework.Assert.*;

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
        if (byMsisdn == null) return;
        allFrontLineWorkers.remove(byMsisdn);
    }

    public void clearSMSReferences(String callerId) {
        SMSReference smsReference = allSMSReferences.findByMsisdn(callerId);
        allSMSReferences.remove(smsReference);
    }

    public CouchDb confirmBookmarkUpdated(String callerId, BookMark playCourseResultBookMark) {
        FrontLineWorker worker = allFrontLineWorkers.findByMsisdn(callerId);
        assertEquals(playCourseResultBookMark, worker.bookMark());
        return this;
    }

    public CouchDb confirmScoresSaved(String callerId, ReportCard reportCard) {
        FrontLineWorker worker = allFrontLineWorkers.findByMsisdn(callerId);
        assertEquals(reportCard.scores(), worker.reportCard().scores());
        return this;
    }

    public CouchDb confirmFlwDoesNotExist(String callerId) {
        assertNull(allFrontLineWorkers.findByMsisdn(callerId));
        return this;
    }

    public CouchDb createPartiallyRegisteredFlwFor(String callerId, String operator, String circle) {
        FrontLineWorker frontLineWorker = new FrontLineWorker(callerId, operator, circle);
        frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        allFrontLineWorkers.add(frontLineWorker);
        return this;
    }

    public CouchDb updatePromptsHeard(String callerId, String prompt) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        frontLineWorker.markPromptHeard(prompt);
        allFrontLineWorkers.update(frontLineWorker);
        return this;
    }

    public CouchDb updateCurrentJobAidUsage(String callerId, int currentUsage) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        frontLineWorker.setCurrentJobAidUsage(convertToMilliSec(currentUsage));
        allFrontLineWorkers.update(frontLineWorker);
        return this;
    }

    private Integer convertToMilliSec(int expected) {
        return expected * 60 * 1000;
    }

    public CouchDb updateBookMark(String callerId, int chapIndex, int lessonIndex) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        BookMark bookmark = new BookMark("",chapIndex,lessonIndex);
        frontLineWorker.addBookMark(bookmark);
        allFrontLineWorkers.update(frontLineWorker);
        return this;
    }

    public CouchDb updateScores(String callerId, List<Score> scores) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        for (Score score : scores)
            frontLineWorker.reportCard().addScore(score);
        allFrontLineWorkers.update(frontLineWorker);
        return this;
    }
}
