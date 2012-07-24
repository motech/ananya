package org.motechproject.ananya.service;

import org.ektorp.UpdateConflictException;
import org.joda.time.DateTime;
import org.motechproject.ananya.contract.FrontLineWorkerCreateResponse;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFrontLineWorkerKeys;
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

        boolean shouldModify = !frontLineWorker.circleIs(circle) || !frontLineWorker.operatorIs(operator) || frontLineWorker.isUnRegistered();
        if (!shouldModify) return new FrontLineWorkerCreateResponse(frontLineWorker, false);

        frontLineWorker.setOperator(operator);
        frontLineWorker.setCircle(circle);
        frontLineWorker.decideRegistrationStatus(locationService.findByExternalId(frontLineWorker.getLocationId()));
        allFrontLineWorkers.updateFlw(frontLineWorker);

        log.info("updated:" + frontLineWorker);

        return new FrontLineWorkerCreateResponse(frontLineWorker, true);
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
        frontLineWorker.decideRegistrationStatus(location);
        allFrontLineWorkers.update(frontLineWorker);
        log.info("updated:" + frontLineWorker);
        return frontLineWorker;
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


}
