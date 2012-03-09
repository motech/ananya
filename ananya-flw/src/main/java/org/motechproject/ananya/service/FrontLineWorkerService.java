package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.exceptions.WorkerDoesNotExistException;
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
    private SendSMSService sendSMSService;
    private SMSPublisherService smsPublisherService;
    private AllOperators allOperators;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers, AllLocations allLocations, SendSMSService sendSMSService, SMSPublisherService smsPublisherService, AllOperators allOperators) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.allLocations = allLocations;
        this.sendSMSService = sendSMSService;
        this.smsPublisherService = smsPublisherService;
        this.allOperators = allOperators;
    }

    private RegistrationStatus getStatus(String msisdn) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        return frontLineWorker != null ? frontLineWorker.status() : RegistrationStatus.UNREGISTERED;
    }

    private boolean isCallerRegistered(String msisdn) {
        return getStatus(msisdn).isRegistered();
    }

    private FrontLineWorker getFrontLineWorker(String msisdn) {
        return allFrontLineWorkers.findByMsisdn(msisdn);
    }

    private void save(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.update(frontLineWorker);
    }

    private void addScore(String callerId, ReportCard.Score score) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(callerId);
        frontLineWorker.reportCard().addScore(score);
        save(frontLineWorker);
    }

    private BookMark getBookmark(String msisdn) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        return frontLineWorker == null ? new EmptyBookmark() : frontLineWorker.bookMark();
    }

    private Map<String, Integer> scoresByChapter(String msisdn){
        final FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        return getScoresByChapterFor(frontLineWorker);
    }

    private void resetScoresForChapterIndex(String callerId, Integer chapterIndex) {
        final FrontLineWorker frontLineWorker = getFrontLineWorker(callerId);
        frontLineWorker.reportCard().clearScoresForChapterIndex(chapterIndex.toString());
        save(frontLineWorker);
    }

    private void resetAllScores(FrontLineWorker frontLineWorker) {
        frontLineWorker.reportCard().clearAllScores();
        save(frontLineWorker);
    }

    private int totalScore(FrontLineWorker frontLineWorker) {
        int totalScore = 0;

        Collection<Integer> scores = this.getScoresByChapterFor(frontLineWorker).values();
        Iterator<Integer> scoresIterator = scores.iterator();
        while(scoresIterator.hasNext()) {
            totalScore += scoresIterator.next();
        }

        return totalScore;
    }

    private Map<String, Integer> getScoresByChapterFor(FrontLineWorker frontLineWorker) {
        return frontLineWorker == null? new HashMap() : frontLineWorker.reportCard().scoresByChapterIndex();
    }

    private int incrementCertificateCourseAttempts(FrontLineWorker frontLineWorker) {
        int certificateCourseAttempts = frontLineWorker.incrementCertificateCourseAttempts();
        save(frontLineWorker);

        return certificateCourseAttempts;
    }

    public String createNew(String msisdn, Designation designation, String panchayatCode, String operator) {
        Location location = allLocations.findByExternalId(panchayatCode);
        Operator operatorObj = allOperators.findByName(operator);
        FrontLineWorker frontLineWorker = new FrontLineWorker(msisdn, designation, location.getId(), operator)
                                            .status(RegistrationStatus.PENDING_REGISTRATION);
        allFrontLineWorkers.add(frontLineWorker);
        return msisdn;
    }

    public void addBookMark(String callerId, BookMark bookMark){
        FrontLineWorker frontLineWorker = getFrontLineWorker(callerId);
        frontLineWorker.addBookMark(bookMark);
        save(frontLineWorker);
    }

    public FrontLineWorker saveName(String msisdn, String name) throws WorkerDoesNotExistException {
        FrontLineWorker frontLineWorker = allFrontLineWorkers.findByMsisdn(msisdn);

        if (frontLineWorker == null)
            throw new WorkerDoesNotExistException();

        frontLineWorker.name(name);
        frontLineWorker.status(RegistrationStatus.REGISTERED);
        allFrontLineWorkers.update(frontLineWorker);

        return frontLineWorker;
    }

    public void resetScoresWhenStartingCertificateCourse(String msisdn) {
        final FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        if(frontLineWorker != null && !frontLineWorker.hasStartedCertificationCourse()) {
            resetAllScores(frontLineWorker);
        }
    }

    public void addSMSReferenceNumber(String msisdn, String smsReferenceNumber) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        frontLineWorker.addSMSReferenceNumber(smsReferenceNumber);
        save(frontLineWorker);
        smsPublisherService.publishSMSSent(msisdn);
    }

    public int getCurrentCourseAttempt(String msisdn) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        return frontLineWorker.currentCourseAttempt();
    }

    public String getSMSReferenceNumber(String msisdn, int courseAttempt) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        return frontLineWorker.smsReferenceNumber(courseAttempt);
    }

    public void saveScore(CertificateCourseStateFlwRequest request){
        String interactionKey = request.getInteractionKey();
        Integer chapterIndex = request.getChapterIndex();
        Integer lessonOrQuestionIndex = request.getLessonOrQuestionIndex();
        Boolean result = request.getResult();
        String callId = request.getCallId();
        String callerId = request.getCallerId();

        if(InteractionKeys.StartQuizInteraction.equals(interactionKey)) {
            resetScoresForChapterIndex(callerId, chapterIndex);
        } else if (InteractionKeys.PlayAnswerExplanationInteraction.equals(interactionKey)) {
            final ReportCard.Score score = new ReportCard.Score(chapterIndex.toString(), lessonOrQuestionIndex.toString(), result, callId);
            addScore(callerId, score);
        } else if (InteractionKeys.PlayCourseResultInteraction.equals(interactionKey)) {
            FrontLineWorker frontLineWorker = getFrontLineWorker(callerId);
            int totalScore = totalScore(frontLineWorker);
            int currentCertificateCourseAttempts = incrementCertificateCourseAttempts(frontLineWorker);

            if(totalScore >= FrontLineWorker.CERTIFICATE_COURSE_PASSING_SCORE) {
                sendSMSService.buildAndSendSMS(callerId, frontLineWorker.getLocationId(), currentCertificateCourseAttempts);
            }
        }
    }

    public CallerDataResponse getCallerData(String msisdn) {
        String bookmark = getBookmark(msisdn).asJson();
        boolean isCallerRegistered = isCallerRegistered(msisdn);
        Map<String, Integer> scoresByChapter = scoresByChapter(msisdn);
        Boolean reachedMaxUsageForMonth = hasReachedMaxUsageForMonth(msisdn);
        return new CallerDataResponse(bookmark, isCallerRegistered, scoresByChapter, reachedMaxUsageForMonth);
    }

    private Boolean hasReachedMaxUsageForMonth(String msisdn) {
        FrontLineWorker frontLineWorker = getFrontLineWorker(msisdn);
        if (frontLineWorker == null)
            return false;
        
        Integer currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage();
        //TODO:should FLWService talk to operator domain? [Imdad/Sush]
        Operator operator = allOperators.findByName(frontLineWorker.getOperator());
        return (operator.getAllowedUsagePerMonth() < currentJobAidUsage);
    }
}
