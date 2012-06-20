package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllSMSReferences;
import org.motechproject.util.DateUtil;
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

    public FrontLineWorker createOrUpdate(FrontLineWorker frontLineWorker, Location location) {
        String callerId = frontLineWorker.getMsisdn();
        String name = frontLineWorker.getName();
        Designation designation = Designation.getFor(frontLineWorker.designationName());
        RegistrationStatus registrationStatus = frontLineWorker.getStatus();
        String circle = frontLineWorker.getCircle();
        String operator = frontLineWorker.getOperator();
        DateTime lastModified = frontLineWorker.getLastModified();

        FrontLineWorker exisitingFrontLineWorker = findByCallerId(callerId);

        if (exisitingFrontLineWorker == null) {
            exisitingFrontLineWorker = new FrontLineWorker(callerId, name, designation, operator, circle, location, registrationStatus, lastModified);
            allFrontLineWorkers.add(exisitingFrontLineWorker);
            log.info("Created:" + exisitingFrontLineWorker);
            return exisitingFrontLineWorker;
        }

        if (isFLWFromDbOlder(frontLineWorker, exisitingFrontLineWorker)) {
            lastModified = lastModified != null ? lastModified : exisitingFrontLineWorker.getLastModified();
            exisitingFrontLineWorker.update(name, designation, location, registrationStatus, circle, operator, lastModified);
            allFrontLineWorkers.update(exisitingFrontLineWorker);
            log.info("Updated:" + exisitingFrontLineWorker);
        }
        
        return exisitingFrontLineWorker;
    }

    private boolean isFLWFromDbOlder(FrontLineWorker frontLineWorker, FrontLineWorker exisitingFrontLineWorker) {
        if (exisitingFrontLineWorker.getLastModified() != null && frontLineWorker.getLastModified() != null)
            return (DateUtil.isOnOrBefore(exisitingFrontLineWorker.getLastModified(), frontLineWorker.getLastModified()));
        return true;
    }


    public FrontLineWorker createOrUpdateUnregistered(String callerId, String operator, String circle) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(callerId, operator);
            frontLineWorker.setCircle(circle);
            frontLineWorker.setRegistrationStatus(RegistrationStatus.UNREGISTERED);
            frontLineWorker.setModified();

            allFrontLineWorkers.add(frontLineWorker);
            log.info("Created:" + frontLineWorker);
            return frontLineWorker;
        }

        if (frontLineWorker.operatorIs(operator) && frontLineWorker.circleIs(circle))
            return frontLineWorker;

        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setModified();
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

    public List<FrontLineWorker> getAll() {
        return allFrontLineWorkers.getAll();
    }

    public FrontLineWorker createOrUpdate(String callerId, String name, Designation designation, Location location, RegistrationStatus registrationStatus) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(callerId, name, designation, location, registrationStatus);
            allFrontLineWorkers.add(frontLineWorker);
            List<FrontLineWorker> existingFrontLineWorkers = allFrontLineWorkers.getAllForMsisdn(frontLineWorker.getMsisdn());
            if (existingFrontLineWorkers.size() > 1) {
                removeDuplicateFLWs(existingFrontLineWorkers);
            }
            log.info("Created:" + frontLineWorker);
            return frontLineWorker;
        }

        frontLineWorker.update(name, designation, location);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("Updated:" + frontLineWorker);
        return frontLineWorker;
    }

    private void removeDuplicateFLWs(List<FrontLineWorker> existingFrontLineWorkers) {
        for (int i = 0; i < existingFrontLineWorkers.size() - 1; i++) {
            allFrontLineWorkers.remove(existingFrontLineWorkers.get(i));
        }
    }

    /*
    * Returns a registration status of the FrontLineWorker based on the current information of the
    * in the database, like location and designation etc. This is a non-transient field and is not
    * picked up from the db field registrationStatus.
    */
    public RegistrationStatus deduceRegistrationStatus(FrontLineWorker frontLineWorker, Location location) {
        boolean locationAbsent = (Location.getDefaultLocation().equals(location));
        boolean locationIncomplete = location.isMissingDetails();
        boolean designationInvalid = Designation.isInValid(frontLineWorker.designationName());
        boolean nameInvalid = StringUtils.isBlank(frontLineWorker.getName());

        if (!(locationAbsent || locationIncomplete || designationInvalid || nameInvalid))
            return RegistrationStatus.REGISTERED;

        if (locationAbsent && designationInvalid && nameInvalid) return RegistrationStatus.UNREGISTERED;

        return RegistrationStatus.PARTIALLY_REGISTERED;
    }
}
