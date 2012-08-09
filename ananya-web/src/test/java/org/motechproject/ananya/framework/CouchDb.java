package org.motechproject.ananya.framework;

import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.*;
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
    @Autowired
    private AllFrontLineWorkerKeys allFrontLineWorkerKeys;
    @Autowired
    private AllCertificateCourseLogs allCertificateCourseLogs;
    @Autowired
    private AllAudioTrackerLogs allAudioTrackerLogs;
    @Autowired
    private AllRegistrationLogs allRegistrationLogs;
    @Autowired
    private AllSMSLogs allSMSLogs;
    @Autowired
    private AllCallLogs allCallLogs;


    public CouchDb confirmPartiallyRegistered(String callerId, String operator) {
        FrontLineWorker worker = allFrontLineWorkers.findByMsisdn(callerId);
        assertNotNull(worker);
        assertEquals(operator, worker.getOperator());
        return this;
    }

    public CouchDb confirmJobAidUsage(String callerId, Integer currentUsage, Integer maxUsage) {
        FrontLineWorker worker = allFrontLineWorkers.findByMsisdn(callerId);
        Operator operator = allOperators.findByName(worker.getOperator());
        assertEquals(currentUsage, worker.getCurrentJobAidUsage());
        assertEquals(new Integer(maxUsage * 60 * 1000), operator.getAllowedUsagePerMonth());
        return this;
    }


    public CouchDb confirmBookmarkUpdated(String callerId, BookMark bookmark) {
        FrontLineWorker worker = allFrontLineWorkers.findByMsisdn(callerId);
        assertEquals(bookmark.asJson(), worker.bookMark().asJson());
        return this;
    }

    public CouchDb confirmScoresSaved(String callerId, ReportCard reportCard) {
        FrontLineWorker worker = allFrontLineWorkers.findByMsisdn(callerId);
        assertTrue(worker.reportCard().scores().containsAll(reportCard.scores()));
        return this;
    }

    public CouchDb confirmFlwDoesNotExist(String callerId) {
        assertNull(allFrontLineWorkers.findByMsisdn(callerId));
        return this;
    }

    public CouchDb confirmPromptsHeard(String callerId, List<String> prompts) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        for (String prompt : prompts)
            assert (frontLineWorker.getPromptsHeard().containsKey(prompt));
        return this;
    }

    public CouchDb confirmSMSReference(String callerId, String smsReferenceNumber) {
        SMSReference smsReference = allSMSReferences.findByMsisdn(callerId);
        assertEquals(smsReferenceNumber, smsReference.referenceNumbers(1));
        return this;
    }

    public void clearFLWData(String callerId) {
        FrontLineWorker byMsisdn = allFrontLineWorkers.findByMsisdn(callerId);
        if (byMsisdn == null) return;
        allFrontLineWorkers.remove(byMsisdn);
        allFrontLineWorkerKeys.removeAll();
        allSMSReferences.removeAll();
    }

    public CouchDb clearAllLogs() {
        allRegistrationLogs.removeAll();
        allAudioTrackerLogs.removeAll();
        allCallLogs.removeAll();
        allSMSLogs.removeAll();
        allCertificateCourseLogs.removeAll();
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

    public CouchDb updateBookMark(String callerId, int chapIndex, int lessonIndex) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        BookMark bookmark = new BookMark("", chapIndex, lessonIndex);
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

    private Integer convertToMilliSec(int expected) {
        return expected * 60 * 1000;
    }

    public CouchDb confirmNoRegistrationLogFor(String callId) {
        assertNull(allRegistrationLogs.findByCallId(callId));
        return this;
    }

    public CouchDb confirmNoAudioTrackerLogFor(String callId) {
        assertNull(allAudioTrackerLogs.findByCallId(callId));
        return this;
    }

    public CouchDb confirmNoCallLogFor(String callId) {
        assertNull(allCallLogs.findByCallId(callId));
        return this;
    }

    public CouchDb confirmNoCourseLogFor(String callId) {
        assertNull(allCertificateCourseLogs.findByCallId(callId));
        return this;
    }

    public CouchDb confirmNoSMSLog(String callId) {
        assertNull(allSMSLogs.findByCallId(callId));
        return this;
    }

    public Boolean confirmSMSSent(String callerId) {
        return allSMSReferences.findByMsisdn(callerId) != null;
    }
}
