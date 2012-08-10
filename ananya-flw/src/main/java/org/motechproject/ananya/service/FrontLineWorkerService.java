package org.motechproject.ananya.service;

import org.ektorp.UpdateConflictException;
import org.joda.time.DateTime;
import org.motechproject.ananya.contract.FrontLineWorkerCreateResponse;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.FrontLineWorkerKey;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.repository.AllFrontLineWorkerKeys;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
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
    private LocationService locationService;
    private AllFrontLineWorkerKeys allFrontLineWorkerKeys;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers, LocationService locationService,
                                  AllFrontLineWorkerKeys allFrontLineWorkerKeys) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.locationService = locationService;
        this.allFrontLineWorkerKeys = allFrontLineWorkerKeys;
    }

    public FrontLineWorker findByCallerId(String callerId) {
        return allFrontLineWorkers.findByMsisdn(callerId);
    }

    public FrontLineWorker createOrUpdate(FrontLineWorker frontLineWorker, Location location) {
        String callerId = frontLineWorker.getMsisdn();
        FrontLineWorker existingFrontLineWorker = findByCallerId(callerId);

        if (existingFrontLineWorker == null) {
            return createNewFrontlineWorker(frontLineWorker, location);
        }
        if (isFLWFromDbOlder(frontLineWorker, existingFrontLineWorker)) {
            updateExistingFrontLineWorker(existingFrontLineWorker, frontLineWorker, location);
        }

        return existingFrontLineWorker;
    }

    public FrontLineWorker findForJobAidCallerData(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        if (frontLineWorker != null && frontLineWorker.jobAidLastAccessedPreviousMonth()) {
            frontLineWorker.resetJobAidUsageAndPrompts();
            allFrontLineWorkers.update(frontLineWorker);
            log.info("reset last jobaid usage for " + frontLineWorker);
        }
        return frontLineWorker;
    }

    public FrontLineWorkerCreateResponse createOrUpdateForCall(String callerId, String operator, String circle) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        //new flw
        if (frontLineWorker == null) {
            try {
                allFrontLineWorkerKeys.add(new FrontLineWorkerKey(callerId));
                frontLineWorker = new FrontLineWorker(callerId, operator, circle);
                frontLineWorker.decideRegistrationStatus(Location.getDefaultLocation());
                allFrontLineWorkers.add(frontLineWorker);

                log.info("created:" + frontLineWorker);
                return new FrontLineWorkerCreateResponse(frontLineWorker, true);
            } catch (UpdateConflictException e) {
                frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
                return new FrontLineWorkerCreateResponse(frontLineWorker, false);
            }
        }

        //no-change flw
        if (frontLineWorker.circleIs(circle) && frontLineWorker.operatorIs(operator) && frontLineWorker.isAlreadyRegistered())
            return new FrontLineWorkerCreateResponse(frontLineWorker, false);

        //updated flw
        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.decideRegistrationStatus(locationService.findByExternalId(frontLineWorker.getLocationId()));
        allFrontLineWorkers.updateFlw(frontLineWorker);
        log.info("updated:" + frontLineWorker);

        return new FrontLineWorkerCreateResponse(frontLineWorker, true);
    }

    public void updateCertificateCourseState(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.update(frontLineWorker);
        log.info("updated certificate course state for " + frontLineWorker);
    }

    public void updateJobAidState(FrontLineWorker frontLineWorker, List<String> promptList, Integer currentCallDuration) {
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

    private boolean isFLWFromDbOlder(FrontLineWorker frontLineWorker, FrontLineWorker exisitingFrontLineWorker) {
        if (exisitingFrontLineWorker.getLastModified() != null && frontLineWorker.getLastModified() != null)
            return (DateUtil.isOnOrBefore(exisitingFrontLineWorker.getLastModified(), frontLineWorker.getLastModified()));
        return true;
    }

    private FrontLineWorker createNewFrontlineWorker(FrontLineWorker frontLineWorker, Location location) {
        frontLineWorker.decideRegistrationStatus(location);
        allFrontLineWorkers.add(frontLineWorker);
        log.info("Created:" + frontLineWorker);
        return frontLineWorker;
    }

    private void updateExistingFrontLineWorker(FrontLineWorker existingFrontLineWorker, FrontLineWorker frontLineWorker, Location location) {
        String name = frontLineWorker.getName();
        Designation designation = Designation.getFor(frontLineWorker.designationName());
        DateTime lastModified = frontLineWorker.getLastModified();

        lastModified = lastModified != null ? lastModified : existingFrontLineWorker.getLastModified();
        existingFrontLineWorker.update(name, designation, location, lastModified);
        allFrontLineWorkers.update(existingFrontLineWorker);
        log.info("Updated:" + existingFrontLineWorker);
    }
}
