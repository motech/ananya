package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontLineWorkerService {

    private static Logger log = LoggerFactory.getLogger(FrontLineWorkerService.class);

    private AllFrontLineWorkers allFrontLineWorkers;
    private AllSMSReferences allSMSReferences;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers,
                                  AllSMSReferences allSMSReferences) {
        this.allFrontLineWorkers = allFrontLineWorkers;
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
            return frontLineWorker;
        }
        frontLineWorker.update(name, designation, location);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated:" + frontLineWorker);
        return frontLineWorker;
    }

    public FrontLineWorker createOrUpdateUnregistered(String callerId, String operator, String circle) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(callerId, operator);
            frontLineWorker.setCircle(circle);
            frontLineWorker.setRegistrationStatus(RegistrationStatus.UNREGISTERED);
            allFrontLineWorkers.add(frontLineWorker);
            log.info("Created:" + frontLineWorker);
            return frontLineWorker;
        }
        if (frontLineWorker.operatorIs(operator) && frontLineWorker.circleIs(circle))
            return frontLineWorker;

        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated:" + frontLineWorker);
        return frontLineWorker;
    }

    public FrontLineWorker findForJobAidCallerData(String callerId, String operator, String circle) {
        FrontLineWorker frontLineWorker = createOrUpdateUnregistered(callerId, operator, circle);
        if (frontLineWorker.jobAidLastAccessedPreviousMonth()) {
            frontLineWorker.resetJobAidUsageAndPrompts();
            allFrontLineWorkers.update(frontLineWorker);
            log.info("Reset last jobaid usage for " + frontLineWorker);
        }
        return frontLineWorker;
    }

    public int getCurrentCourseAttempt(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        return frontLineWorker.currentCourseAttempt();
    }

    public void updateRegistrationStatus(String msisdn, RegistrationStatus registrationStatus) {
        FrontLineWorker frontLineWorker = findByCallerId(msisdn);
        frontLineWorker.setRegistrationStatus(registrationStatus);
        allFrontLineWorkers.update(frontLineWorker);
    }

    public SMSReference getSMSReferenceNumber(String callerId) {
        return allSMSReferences.findByMsisdn(callerId);
    }

    public void addSMSReferenceNumber(SMSReference smsReference) {
        allSMSReferences.add(smsReference);
        log.info("Created SMS reference for:" + smsReference.getMsisdn());
    }

    public void updateSMSReferenceNumber(SMSReference smsReference) {
        allSMSReferences.update(smsReference);
        log.info("Updated SMS reference for:" + smsReference.getMsisdn());
    }

    public void updatePromptsFor(String callerId, List<String> promptList) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        for (String prompt : promptList)
            frontLineWorker.markPromptHeard(prompt);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated prompts heard for " + frontLineWorker);
    }

    public void updateJobAidUsageAndAccessTime(String callerId, Integer currentCallDuration) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        Integer currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage();
        frontLineWorker.setCurrentJobAidUsage(currentCallDuration + currentJobAidUsage);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now());
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated jobaid usage and access time for " + frontLineWorker);
    }

    public void updateCertificateCourseStateFor(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated certificate course state for " + frontLineWorker);
    }

    public boolean isNewFlwOrOperatorOrCircleIsEmpty(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        return frontLineWorker == null || StringUtils.isEmpty(frontLineWorker.getOperator()) ||
                StringUtils.isEmpty(frontLineWorker.getCircle());
    }

    public List<FrontLineWorker> findByRegisteredDate(DateTime startDate, DateTime endDate) {
        return allFrontLineWorkers.findByRegisteredDate(startDate, endDate);
    }

    public List<FrontLineWorker> getAll() {
        return allFrontLineWorkers.getAll();
    }

    public void updateFrontLineWorkerWithDefaultCircle(List<FrontLineWorker> frontLineWorkers, String defaultCircle) {
        for (FrontLineWorker frontLineWorker : frontLineWorkers) {
            frontLineWorker.setCircle(defaultCircle);
            allFrontLineWorkers.update(frontLineWorker);
            log.info("Updated FrontLineWorker: " + frontLineWorker.getMsisdn() + "with circle: " + frontLineWorker.getCircle());
        }
    }
}
