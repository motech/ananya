package org.motechproject.ananya.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrontLineWorkerService {

    private static Logger log = LoggerFactory.getLogger(FrontLineWorkerService.class);

    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers) {
        this.allFrontLineWorkers = allFrontLineWorkers;
    }

    public FrontLineWorker findByCallerId(String callerId) {
        return allFrontLineWorkers.findByMsisdn(callerId);
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

        if (frontLineWorker.operatorIs(operator) && frontLineWorker.circleIs(circle) && frontLineWorker.isAlreadyRegistered()) {
            return frontLineWorker;
        }

        if (frontLineWorker.isUnRegistered())
            frontLineWorker.setRegistrationStatus(RegistrationStatus.PARTIALLY_REGISTERED);
        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.setModified();
        allFrontLineWorkers.update(frontLineWorker);

        log.info("updated:" + frontLineWorker);
        return frontLineWorker;
    }

    public FrontLineWorker createOrUpdateForImport(String callerId, String name, Designation designation, Location location) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        if (frontLineWorker == null) {
            frontLineWorker = new FrontLineWorker(callerId, name, designation, location, RegistrationStatus.UNREGISTERED);
            allFrontLineWorkers.add(frontLineWorker);
            log.info("created:" + frontLineWorker);
            return frontLineWorker;
        }

        frontLineWorker.update(name, designation, location);
        frontLineWorker.setRegistrationStatus(deduceRegistrationStatus(frontLineWorker, location));
        allFrontLineWorkers.update(frontLineWorker);
        log.info("updated:" + frontLineWorker);
        return frontLineWorker;
    }

    public void updateCertificateCourseState(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.update(frontLineWorker);
        log.info("updated certificate course state for " + frontLineWorker);
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

    public int getCurrentCourseAttempt(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        return frontLineWorker.currentCourseAttempt();
    }

    public List<FrontLineWorker> getAll() {
        return allFrontLineWorkers.getAll();
    }

    public RegistrationStatus deduceRegistrationStatus(FrontLineWorker frontLineWorker, Location location) {
        boolean locationAbsent = (Location.getDefaultLocation().equals(location));
        boolean locationIncomplete = location.isMissingDetails();
        boolean designationInvalid = Designation.isInValid(frontLineWorker.designationName());
        boolean nameInvalid = StringUtils.isBlank(frontLineWorker.getName());

        if (locationAbsent || locationIncomplete || designationInvalid || nameInvalid)
            return RegistrationStatus.PARTIALLY_REGISTERED;
        return RegistrationStatus.REGISTERED;
    }


}
