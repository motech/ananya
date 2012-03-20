package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllOperators;
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
    private SendSMSService sendSMSService;
    private SMSPublisherService smsPublisherService;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers, SendSMSService sendSMSService, SMSPublisherService smsPublisherService) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.sendSMSService = sendSMSService;
        this.smsPublisherService = smsPublisherService;
    }

    public FrontLineWorker createOrUpdate(String msisdn, String name, String designation, Location location) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(msisdn, name, Designation.valueOf(designation), location);
            allFrontLineWorkers.add(frontLineWorker);
        } else {
            frontLineWorker = this.updateFrontLineWorker(frontLineWorker, name, designation, location);
        }
        return frontLineWorker;
    }

    public FrontLineWorker createOrUpdate(String msisdn, String operator) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(msisdn, operator).status(RegistrationStatus.PARTIALLY_REGISTERED);
            allFrontLineWorkers.add(frontLineWorker);
            return frontLineWorker;
        }

        if (StringUtils.equalsIgnoreCase(operator, frontLineWorker.getOperator())) return frontLineWorker;

        frontLineWorker.setOperator(operator);
        allFrontLineWorkers.update(frontLineWorker);
        return frontLineWorker;
    }

    public void addBookMark(String callerId, BookMark bookMark) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        frontLineWorker.addBookMark(bookMark);
        allFrontLineWorkers.update(frontLineWorker);
    }

    public FrontLineWorker findByCallerId(String callerId) {
        return allFrontLineWorkers.findByMsisdn(callerId);
    }

    public void addSMSReferenceNumber(String msisdn, String smsReferenceNumber) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        frontLineWorker.addSMSReferenceNumber(smsReferenceNumber);
        allFrontLineWorkers.update(frontLineWorker);
        smsPublisherService.publishSMSSent(msisdn);
    }

    public int getCurrentCourseAttempt(String msisdn) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        return frontLineWorker.currentCourseAttempt();
    }

    public String getSMSReferenceNumber(String msisdn, int courseAttempt) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        return frontLineWorker.smsReferenceNumber(courseAttempt);
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
            FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);

            int totalScore = frontLineWorker.reportCard().totalScore();
            int currentCertificateCourseAttempts = incrementCertificateCourseAttempts(frontLineWorker);

            if (totalScore >= FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE) {
                sendSMSService.buildAndSendSMS(callerId, frontLineWorker.getLocationId(), currentCertificateCourseAttempts);
            }
        }
    }

    public void updatePromptsForFLW(String msisdn, List<String> promptList) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        for (String prompt : promptList)
            frontLineWorker.markPromptHeard(prompt);
        allFrontLineWorkers.update(frontLineWorker);
    }

    public void updateCurrentUsageForUser(String msisdn, Integer currentCallDuration) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        Integer currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage();
        frontLineWorker.setCurrentJobAidUsage(currentCallDuration+currentJobAidUsage);
        allFrontLineWorkers.update(frontLineWorker);
    }

    private void addScore(String callerId, ReportCard.Score score) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        frontLineWorker.reportCard().addScore(score);
        allFrontLineWorkers.update(frontLineWorker);
    }

    private void resetScoresForChapterIndex(String callerId, Integer chapterIndex) {
        final FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        frontLineWorker.reportCard().clearScoresForChapterIndex(chapterIndex.toString());
        allFrontLineWorkers.update(frontLineWorker);
    }

    private int incrementCertificateCourseAttempts(FrontLineWorker frontLineWorker) {
        int certificateCourseAttempts = frontLineWorker.incrementCertificateCourseAttempts();
        allFrontLineWorkers.update(frontLineWorker);
        return certificateCourseAttempts;
    }

    private void resetAllScores(String msisdn) {
        final FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
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

    public void updateLastJobAidAccessTime(String msisdn) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now());
        allFrontLineWorkers.update(frontLineWorker);
    }

    public FrontLineWorker getFLWForJobAidCallerData(String msisdn, String operator) {
        FrontLineWorker frontLineWorker = createOrUpdate(msisdn, operator);
        DateTime lastJobAidAccessTime = frontLineWorker.getLastJobAidAccessTime();
        if(lastJobAidAccessTime != null &&
                (lastJobAidAccessTime.getMonthOfYear() != DateTime.now().getMonthOfYear() ||
                lastJobAidAccessTime.getYear() != DateTime.now().getYear())){
            frontLineWorker.setCurrentJobAidUsage(0);
            Map<String,Integer> promptsHeard = frontLineWorker.getPromptsHeard();
            promptsHeard.remove("Max_Usage");
            allFrontLineWorkers.update(frontLineWorker);
        }
        return frontLineWorker;
    }
}
