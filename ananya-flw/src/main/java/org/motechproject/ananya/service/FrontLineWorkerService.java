package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FrontLineWorkerService {

    private static Logger log = LoggerFactory.getLogger(FrontLineWorkerService.class);

    private AllFrontLineWorkers allFrontLineWorkers;
    private AllSMSReferences allSMSReferences;
    private SendSMSService sendSMSService;
    private SMSPublisherService smsPublisherService;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers, SendSMSService sendSMSService, SMSPublisherService smsPublisherService, AllSMSReferences allSMSReferences) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.sendSMSService = sendSMSService;
        this.smsPublisherService = smsPublisherService;
        this.allSMSReferences = allSMSReferences;
    }

    public FrontLineWorker findByCallerId(String callerId) {
        return allFrontLineWorkers.findByMsisdn(callerId);
    }

    public FrontLineWorker createOrUpdateRegistered(String callerId, String name, String designation, Location location) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(callerId, name, Designation.valueOf(designation), location);
            allFrontLineWorkers.add(frontLineWorker);
        } else {
            frontLineWorker = this.updateFrontLineWorker(frontLineWorker, name, designation, location);
        }
        return frontLineWorker;
    }

    public FrontLineWorker createOrUpdatePartiallyRegistered(String callerId, String operator) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(callerId, operator).status(RegistrationStatus.PARTIALLY_REGISTERED);
            allFrontLineWorkers.add(frontLineWorker);
            return frontLineWorker;
        }
        if (frontLineWorker.operatorIs(operator))
            return frontLineWorker;

        frontLineWorker.setOperator(operator);
        allFrontLineWorkers.update(frontLineWorker);
        return frontLineWorker;
    }

    public void addBookMark(String callerId, BookMark bookMark) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        frontLineWorker.addBookMark(bookMark);
        allFrontLineWorkers.update(frontLineWorker);
    }

    public void addSMSReferenceNumber(String callerId, String smsReferenceNumber) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        SMSReference smsReference = allSMSReferences.findByMsisdn(callerId);
        if (smsReference == null) {
            smsReference = new SMSReference(callerId);
            allSMSReferences.add(smsReference);
        }
        smsReference.add(smsReferenceNumber, frontLineWorker.currentCourseAttempt());
        allSMSReferences.update(smsReference);
        smsPublisherService.publishSMSSent(callerId);
    }

    public int getCurrentCourseAttempt(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        return frontLineWorker.currentCourseAttempt();
    }

    public SMSReference getSMSReferenceNumber(String callerId) {
        return allSMSReferences.findByMsisdn(callerId);
    }

    public void saveScore(CertificateCourseStateFlwRequest request) {
        String callId = request.getCallId();
        String callerId = request.getCallerId();
        Integer chapterIndex = request.getChapterIndex();
        Integer lessonOrQuestionIndex = request.getLessonOrQuestionIndex();
        Boolean result = request.getResult();

        if (request.isStartCertificationCourseInteraction()) {
            resetAllScores(callerId);
        } else if (request.isStartQuizInteraction()) {
            resetScoresForChapterIndex(callerId, chapterIndex);
        } else if (request.isPlayAnswerExplanationInteraction()) {
            final ReportCard.Score score = new ReportCard.Score(chapterIndex.toString(), lessonOrQuestionIndex.toString(), result, callId);
            addScore(callerId, score);
        } else if (request.isPlayCourseResultInteraction()) {
            FrontLineWorker frontLineWorker = findByCallerId(callerId);

            int totalScore = frontLineWorker.reportCard().totalScore();
            int currentCertificateCourseAttempts = incrementCertificateCourseAttempts(frontLineWorker);

            if (totalScore >= FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE) {
                sendSMSService.buildAndSendSMS(callerId, frontLineWorker.getLocationId(), currentCertificateCourseAttempts);
            }
        }
    }

    public void updatePromptsFor(String callerId, List<String> promptList) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        for (String prompt : promptList)
            frontLineWorker.markPromptHeard(prompt);
        allFrontLineWorkers.update(frontLineWorker);
    }

    public void updateJobAidCurrentUsageFor(String callerId, Integer currentCallDuration) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        Integer currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage();
        frontLineWorker.setCurrentJobAidUsage(currentCallDuration + currentJobAidUsage);
        allFrontLineWorkers.update(frontLineWorker);
    }

    private void addScore(String callerId, ReportCard.Score score) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        frontLineWorker.reportCard().addScore(score);
        allFrontLineWorkers.update(frontLineWorker);
    }

    private void resetScoresForChapterIndex(String callerId, Integer chapterIndex) {
        final FrontLineWorker frontLineWorker = findByCallerId(callerId);
        frontLineWorker.reportCard().clearScoresForChapterIndex(chapterIndex.toString());
        allFrontLineWorkers.update(frontLineWorker);
    }

    private int incrementCertificateCourseAttempts(FrontLineWorker frontLineWorker) {
        int certificateCourseAttempts = frontLineWorker.incrementCertificateCourseAttempts();
        allFrontLineWorkers.update(frontLineWorker);
        return certificateCourseAttempts;
    }

    private void resetAllScores(String msisdn) {
        final FrontLineWorker frontLineWorker = findByCallerId(msisdn);
        if (frontLineWorker != null) {
            frontLineWorker.reportCard().clearAllScores();
            allFrontLineWorkers.update(frontLineWorker);
        }
    }

    private FrontLineWorker updateFrontLineWorker(FrontLineWorker frontLineWorker, String name,
                                                  String designation, Location location) {
        frontLineWorker.setName(name);
        frontLineWorker.setDesignation(designation);
        frontLineWorker.setLocation(location);
        allFrontLineWorkers.update(frontLineWorker);
        return frontLineWorker;
    }

    public void updateLastJobAidAccessTime(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now());
        allFrontLineWorkers.update(frontLineWorker);
    }

    public FrontLineWorker getFLWForJobAidCallerData(String callerId, String operator) {
        FrontLineWorker frontLineWorker = createOrUpdatePartiallyRegistered(callerId, operator);
        DateTime lastJobAidAccessTime = frontLineWorker.getLastJobAidAccessTime();
        if (lastJobAidAccessTime != null &&
                (lastJobAidAccessTime.getMonthOfYear() != DateTime.now().getMonthOfYear() ||
                        lastJobAidAccessTime.getYear() != DateTime.now().getYear())) {
            frontLineWorker.setCurrentJobAidUsage(0);
            Map<String, Integer> promptsHeard = frontLineWorker.getPromptsHeard();
            promptsHeard.remove("Max_Usage");
            allFrontLineWorkers.update(frontLineWorker);
        }
        return frontLineWorker;
    }

    public boolean isNewFLW(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        return frontLineWorker == null;
    }
}
