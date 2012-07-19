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

    public FrontLineWorker createOrUpdateForCall(String callerId, String operator, String circle) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(callerId, operator);
            frontLineWorker.setCircle(circle);
            frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
            frontLineWorker.setModified();

            allFrontLineWorkers.add(frontLineWorker);
            log.info("created:" + frontLineWorker);
            return frontLineWorker;
        }

        boolean shouldStatusBeModified = frontLineWorker.status() == RegistrationStatus.UNREGISTERED;

        if (frontLineWorker.operatorIs(operator) && frontLineWorker.circleIs(circle) && !shouldStatusBeModified)  {
            return frontLineWorker;
        }

        if (shouldStatusBeModified) frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setModified();
        allFrontLineWorkers.update(frontLineWorker);

        log.info("updated:" + frontLineWorker);
        return frontLineWorker;
    }

    public FrontLineWorker findForJobAidCallerData(String callerId, String operator, String circle) {
        FrontLineWorker frontLineWorker = createOrUpdateForCall(callerId, operator, circle);
        if (frontLineWorker.jobAidLastAccessedPreviousMonth()) {
            frontLineWorker.resetJobAidUsageAndPrompts();
            allFrontLineWorkers.update(frontLineWorker);
            log.info("reset last jobaid usage for " + frontLineWorker);
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
        log.info("created SMS reference for:" + smsReference.getMsisdn());
    }

    public void updateSMSReferenceNumber(SMSReference smsReference) {
        allSMSReferences.update(smsReference);
        log.info("updated SMS reference for:" + smsReference.getMsisdn());
    }

    public void updateJobAidState(String callerId, List<String> promptList, Integer currentCallDuration) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        for (String prompt : promptList)
            frontLineWorker.markPromptHeard(prompt);

        Integer currentJobAidUsage = frontLineWorker.getCurrentJobAidUsage();
        frontLineWorker.setCurrentJobAidUsage(currentCallDuration + currentJobAidUsage);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now());

        allFrontLineWorkers.update(frontLineWorker);
        log.info("updated prompts-heard, jobaid-usage and access-time for " + frontLineWorker);
    }

    public void updateCertificateCourseStateFor(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.update(frontLineWorker);
        log.info("updated certificate course state for " + frontLineWorker);
    }

    public List<FrontLineWorker> getAll() {
        return allFrontLineWorkers.getAll();
    }

    public FrontLineWorker createOrUpdateForImport(String callerId, String name, Designation designation, Location location) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(callerId, name, designation, location, RegistrationStatus.UNREGISTERED);
            allFrontLineWorkers.add(frontLineWorker);

            List<FrontLineWorker> existingFrontLineWorkers = allFrontLineWorkers.getAllForMsisdn(frontLineWorker.getMsisdn());
            if (existingFrontLineWorkers.size() > 1) {
                log.warn("Duplicate MSISDNs found in system. deleting");
                removeDuplicateFLWs(existingFrontLineWorkers);
            }
            log.info("created:" + frontLineWorker);
            return frontLineWorker;
        }

        frontLineWorker.update(name, designation, location);
        frontLineWorker.setRegistrationStatus(deduceRegistrationStatus(frontLineWorker, location));
        allFrontLineWorkers.update(frontLineWorker);
        log.info("updated:" + frontLineWorker);
        return frontLineWorker;
    }

    private void removeDuplicateFLWs(List<FrontLineWorker> existingFrontLineWorkers) {
        for (int i = 0; i < existingFrontLineWorkers.size() - 1; i++) {
            allFrontLineWorkers.remove(existingFrontLineWorkers.get(i));
        }
    }

    /*
    * This function deduces the status based on the new business definition. This however does only
    * part of the work. It assumes that the FLW has called into the service before.
    *
    * @see @link http://mingle.motechproject.org/projects/motech_bbc_production_support/cards/102
    *
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

        return RegistrationStatus.PARTIALLY_REGISTERED;
    }

    /*
    * EDIT: This function deduces the status based on the old business definition. It is maintained here
    * because of its usage in an old registration correction seed.
    *
    * Returns a registration status of the FrontLineWorker based on the current information of the
    * in the database, like location and designation etc. This is a non-transient field and is not
    * picked up from the db field registrationStatus.
    */
    public RegistrationStatus deduceRegistrationStatusOld(FrontLineWorker frontLineWorker, Location location) {
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
