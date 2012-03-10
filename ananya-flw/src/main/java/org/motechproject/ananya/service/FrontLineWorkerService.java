package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.repository.AllOperators;
import org.motechproject.ananya.request.CertificateCourseStateFlwRequest;
import org.motechproject.ananya.response.CallerDataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class FrontLineWorkerService {

    private static Logger log = LoggerFactory.getLogger(FrontLineWorkerService.class);

    private AllFrontLineWorkers allFrontLineWorkers;
    private AllLocations allLocations;
    private AllOperators allOperators;
    private SendSMSService sendSMSService;
    private SMSPublisherService smsPublisherService;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers, AllLocations allLocations, SendSMSService sendSMSService, SMSPublisherService smsPublisherService, AllOperators allOperators) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.allLocations = allLocations;
        this.sendSMSService = sendSMSService;
        this.smsPublisherService = smsPublisherService;
        this.allOperators = allOperators;
    }

    private void addScore(String callerId, ReportCard.Score score) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        frontLineWorker.reportCard().addScore(score);
        allFrontLineWorkers.update(frontLineWorker);
    }

    private BookMark getBookmark(String msisdn) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        return frontLineWorker == null ? new EmptyBookmark() : frontLineWorker.bookMark();
    }

    private Map<String, Integer> scoresByChapter(String msisdn) {
        final FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        return getScoresByChapterFor(frontLineWorker);
    }

    private void resetScoresForChapterIndex(String callerId, Integer chapterIndex) {
        final FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        frontLineWorker.reportCard().clearScoresForChapterIndex(chapterIndex.toString());
        allFrontLineWorkers.update(frontLineWorker);
    }

    private int totalScore(FrontLineWorker frontLineWorker) {
        int totalScore = 0;
        Collection<Integer> scores = this.getScoresByChapterFor(frontLineWorker).values();
        Iterator<Integer> scoresIterator = scores.iterator();
        while (scoresIterator.hasNext())
            totalScore += scoresIterator.next();
        return totalScore;
    }

    private Map<String, Integer> getScoresByChapterFor(FrontLineWorker frontLineWorker) {
        return frontLineWorker == null ? new HashMap() : frontLineWorker.reportCard().scoresByChapterIndex();
    }

    private int incrementCertificateCourseAttempts(FrontLineWorker frontLineWorker) {
        int certificateCourseAttempts = frontLineWorker.incrementCertificateCourseAttempts();
        allFrontLineWorkers.update(frontLineWorker);
        return certificateCourseAttempts;
    }

    private FrontLineWorker createNew(String msisdn, String operator) {
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, operator).status(RegistrationStatus.PARTIALLY_REGISTERED);
        allFrontLineWorkers.add(frontLineWorker);
        return frontLineWorker;
    }

    public void addBookMark(String callerId, BookMark bookMark) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
        frontLineWorker.addBookMark(bookMark);
        allFrontLineWorkers.update(frontLineWorker);
    }

    private void resetAllScores(String msisdn) {
        final FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        if (frontLineWorker != null) {
            frontLineWorker.reportCard().clearAllScores();
            allFrontLineWorkers.update(frontLineWorker);
        }
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
            int totalScore = totalScore(frontLineWorker);
            int currentCertificateCourseAttempts = incrementCertificateCourseAttempts(frontLineWorker);

            if (totalScore >= FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE) {
                sendSMSService.buildAndSendSMS(callerId, frontLineWorker.getLocationId(), currentCertificateCourseAttempts);
            }
        }
    }

    public CallerDataResponse createCallerData(String msisdn, String operator) {
        String bookmark = getBookmark(msisdn).asJson();
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        if (frontLineWorker == null) {
            frontLineWorker = createNew(msisdn, operator);
        }
        Map<String, Integer> scoresByChapter = scoresByChapter(msisdn);
        Boolean reachedMaxUsageForMonth = hasReachedMaxUsageForMonth(msisdn);

        return new CallerDataResponse(bookmark, frontLineWorker.status().isRegistered(), scoresByChapter, reachedMaxUsageForMonth);
    }

    private Boolean hasReachedMaxUsageForMonth(String msisdn) {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);
        if (frontLineWorker == null) return false;
        int currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage() == null ? 0 : frontLineWorker.getCurrentJobAidUsage();
        Operator operator = allOperators.findByName(frontLineWorker.getOperator());
        return (currentJobAidUsage >= operator.getAllowedUsagePerMonth());
    }
}
