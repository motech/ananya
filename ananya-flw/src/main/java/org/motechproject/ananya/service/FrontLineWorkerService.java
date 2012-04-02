package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllSMSReferences;
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
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers,
                                  SendSMSService sendSMSService,
                                  SMSPublisherService smsPublisherService,
                                  AllSMSReferences allSMSReferences) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.sendSMSService = sendSMSService;
        this.smsPublisherService = smsPublisherService;
        this.allSMSReferences = allSMSReferences;
    }

    public FrontLineWorker findByCallerId(String callerId) {
        return allFrontLineWorkers.findByMsisdn(callerId);
    }

    public FrontLineWorker createOrUpdate(String callerId, String name, Designation designation, Location location, RegistrationStatus registrationStatus) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(callerId, name, designation, location, registrationStatus);
            allFrontLineWorkers.add(frontLineWorker);
            log.info("Created:" + frontLineWorker);
        }

        frontLineWorker.update(name, designation, location);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated:" + frontLineWorker);
        return frontLineWorker;
    }

    public FrontLineWorker createOrUpdatePartiallyRegistered(String callerId, String operator) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(callerId, operator);
            frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
            allFrontLineWorkers.add(frontLineWorker);
            log.info("Created:" + frontLineWorker);
            return frontLineWorker;
        }
        if (frontLineWorker.operatorIs(operator))
            return frontLineWorker;

        frontLineWorker.setOperator(operator);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated:" + frontLineWorker);
        return frontLineWorker;
    }

    public void addSMSReferenceNumber(String callerId, String smsReferenceNumber) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        SMSReference smsReference = getSMSReferenceNumber(callerId);

        if (smsReference == null) {
            smsReference = new SMSReference(callerId, frontLineWorker.getId());
            allSMSReferences.add(smsReference);
            log.info("Created SMS reference for:" + frontLineWorker);
        }
        smsReference.add(smsReferenceNumber, frontLineWorker.currentCourseAttempt());
        allSMSReferences.update(smsReference);
        log.info("Updated SMS reference for:" + frontLineWorker);

        smsPublisherService.publishSMSSent(callerId);
    }

    public int getCurrentCourseAttempt(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        return frontLineWorker.currentCourseAttempt();
    }

    public SMSReference getSMSReferenceNumber(String callerId) {
        return allSMSReferences.findByMsisdn(callerId);
    }

    public void updatePromptsFor(String callerId, List<String> promptList) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        for (String prompt : promptList)
            frontLineWorker.markPromptHeard(prompt);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated prompts heard for " + frontLineWorker);
    }

    public void updateJobAidCurrentUsageFor(String callerId, Integer currentCallDuration) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        Integer currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage();
        frontLineWorker.setCurrentJobAidUsage(currentCallDuration + currentJobAidUsage);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated jobaid usage for " + frontLineWorker);
    }

    public void updateJobAidLastAccessTime(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now());
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated last jobaid access time " + frontLineWorker);
    }

    public void updateCertificateCourseStateFor(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.update(frontLineWorker);
        if (frontLineWorker.hasCompletedCertificateCourse()) {
            sendSMSService.buildAndSendSMS(frontLineWorker.getMsisdn(), frontLineWorker.getLocationId(), frontLineWorker.currentCourseAttempt());
            log.info("Course completion SMS sent for " + frontLineWorker);
        }
        log.info("Updated certificate course state for " + frontLineWorker);
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
            log.info("Reset last jobaid usage for " + frontLineWorker);
        }
        return frontLineWorker;
    }

    public boolean isNewFLW(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        return frontLineWorker == null;
    }
}
