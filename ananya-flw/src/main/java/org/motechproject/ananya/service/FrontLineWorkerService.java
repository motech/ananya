package org.motechproject.ananya.service;

import org.ektorp.UpdateConflictException;
import org.joda.time.DateTime;
import org.motechproject.ananya.contract.FrontLineWorkerCreateResponse;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllFailedRecordsProcessingStates;
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
    private AllFailedRecordsProcessingStates allFailedRecordsProcessingStates;
    private OperatorService operatorService;

    @Autowired
    public FrontLineWorkerService(AllFrontLineWorkers allFrontLineWorkers, LocationService locationService,
                                  AllFrontLineWorkerKeys allFrontLineWorkerKeys, AllFailedRecordsProcessingStates allFailedRecordsProcessingStates, OperatorService operatorService) {
        this.allFrontLineWorkers = allFrontLineWorkers;
        this.locationService = locationService;
        this.allFrontLineWorkerKeys = allFrontLineWorkerKeys;
        this.allFailedRecordsProcessingStates = allFailedRecordsProcessingStates;
        this.operatorService = operatorService;
    }

    public FrontLineWorker findByCallerId(String callerId) {
        return allFrontLineWorkers.findByMsisdn(callerId);
    }

    public FrontLineWorker createOrUpdate(FrontLineWorker frontLineWorker, Location location) {
        String callerId = frontLineWorker.getMsisdn();
        FrontLineWorker existingFrontLineWorker = findByCallerId(callerId);

        if (existingFrontLineWorker == null) {
            return createNewFrontLineWorker(frontLineWorker);
        }

        return updateExistingFrontLineWorker(existingFrontLineWorker, frontLineWorker, location);
    }

    public FrontLineWorker findForJobAidCallerData(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        if (frontLineWorker != null && frontLineWorker.jobAidLastAccessedPreviousMonth()) {
            frontLineWorker.resetJobAidUsageAndPrompts();
            allFrontLineWorkers.update(frontLineWorker);
            log.info("reset last jobaid usage for " + frontLineWorker.getMsisdn());
        }
        return frontLineWorker;
    }

    public FrontLineWorkerCreateResponse createOrUpdateForCall(String callerId, String operator, String circle, String language) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);

        //new flw
        if (frontLineWorker == null) {
            try {
                allFrontLineWorkerKeys.add(new FrontLineWorkerKey(callerId));
                frontLineWorker = new FrontLineWorker(callerId, operator, circle, language);
                frontLineWorker.decideRegistrationStatus(Location.getDefaultLocation());
                allFrontLineWorkers.add(frontLineWorker);

                log.info("created:" + frontLineWorker);
                return new FrontLineWorkerCreateResponse(frontLineWorker, true);
            } catch (UpdateConflictException e) {
                frontLineWorker = allFrontLineWorkers.findByMsisdn(callerId);
                return new FrontLineWorkerCreateResponse(frontLineWorker, false);
            }
        }

        //no-change flw only if FLW's language is null so updated language is not null
        boolean operatorHasNotChanged = frontLineWorker.operatorIs(operator);
        if (frontLineWorker.circleIs(circle) && operatorHasNotChanged && frontLineWorker.isAlreadyRegistered() && !(frontLineWorker.getLanguage() == null && language != null))
            return new FrontLineWorkerCreateResponse(frontLineWorker, false);

        // 
        if (frontLineWorker.getLanguage() == null) {
            if (language != null)
                frontLineWorker.setLanguage(language);
        } else if (!frontLineWorker.getLanguage().equalsIgnoreCase(language)) {
            log.error("received a request for different language. User language is " + frontLineWorker.getLanguage() + " but recevied language with " + language);
        }

        //updated flw
        if (!operatorHasNotChanged) {
            frontLineWorker.setOperator(operator);
            frontLineWorker.resetJobAidUsageAndPrompts();
        }
        frontLineWorker.setCircle(circle);
        frontLineWorker.decideRegistrationStatus(locationService.findByExternalId(frontLineWorker.getLocationId()));
        allFrontLineWorkers.updateFlw(frontLineWorker);
        log.info("updated: [" + frontLineWorker.getMsisdn() + "] with status :[" + frontLineWorker.getStatus() +
                "] ,operator : " + frontLineWorker.getOperator() + ", circle : " + frontLineWorker.getOperator() + ", language : " + frontLineWorker.getLanguage());

        return new FrontLineWorkerCreateResponse(frontLineWorker, true);
    }

    public void updateCertificateCourseState(FrontLineWorker frontLineWorker) {
        allFrontLineWorkers.update(frontLineWorker);
        log.info("updated certificate course state for " + frontLineWorker.getMsisdn());
    }

    public void updateJobAidState(FrontLineWorker frontLineWorker, List<String> promptList, Integer currentCallDuration) {
        for (String prompt : promptList)
            frontLineWorker.markPromptHeard(prompt);

        Integer usageByPulseInMilliSec = operatorService.usageByPulseInMilliSec(frontLineWorker.getOperator(), currentCallDuration);
        frontLineWorker.updateJobAidUsage(usageByPulseInMilliSec);
        frontLineWorker.setLastJobAidAccessTime(DateTime.now());

        allFrontLineWorkers.update(frontLineWorker);
        log.info("updated prompts-heard, jobaid-usage and access-time for " + frontLineWorker.getMsisdn());
    }

    public int getCurrentCourseAttempt(String callerId) {
        FrontLineWorker frontLineWorker = findByCallerId(callerId);
        return frontLineWorker.currentCourseAttempts();
    }

    public List<FrontLineWorker> getAll() {
        return allFrontLineWorkers.getAll();
    }

    private boolean isFLWFromDbOlder(FrontLineWorker frontLineWorker, FrontLineWorker existingFrontLineWorker) {
        if (existingFrontLineWorker.getLastModified() != null && frontLineWorker.getLastModified() != null)
            return (DateUtil.isOnOrBefore(existingFrontLineWorker.getLastModified(), frontLineWorker.getLastModified()));
        return true;
    }

    private FrontLineWorker createNewFrontLineWorker(FrontLineWorker frontLineWorker) {
        frontLineWorker.setRegistrationStatus(RegistrationStatus.UNREGISTERED);
        allFrontLineWorkers.add(frontLineWorker);
        log.info("Created:" + frontLineWorker);
        return frontLineWorker;
    }

    private FrontLineWorker updateExistingFrontLineWorker(FrontLineWorker existingFrontLineWorker, FrontLineWorker frontLineWorker, Location location) {
        boolean updated = existingFrontLineWorker.update(frontLineWorker.getName(), frontLineWorker.getDesignation(), location,
                frontLineWorker.getLastModified(), frontLineWorker.getFlwId(), frontLineWorker.getVerificationStatus(),
                frontLineWorker.getAlternateContactNumber());
        if (!updated) {
            return existingFrontLineWorker;
        }
        allFrontLineWorkers.update(existingFrontLineWorker);
        log.info("Updated:" + existingFrontLineWorker);
        return existingFrontLineWorker;
    }


    public DateTime getLastFailedRecordsProcessedDate() {
        List<FailedRecordsProcessingState> failedRecordsProcessingStates = allFailedRecordsProcessingStates.getAll();
        if (failedRecordsProcessingStates.isEmpty())
            return null;

        FailedRecordsProcessingState failedRecordsProcessingState = failedRecordsProcessingStates.get(0);
        return failedRecordsProcessingState.getLastProcessedDate();
    }

    public List<FrontLineWorker> updateLocation(Location oldLocation, Location newLocation) {
        List<FrontLineWorker> frontLineWorkerList = allFrontLineWorkers.findByLocationId(oldLocation.getExternalId());
        log.info(String.format("Updating location for %s frontLineWorkers from : %s to : %s",
                frontLineWorkerList.size(), oldLocation.getId(), newLocation.getId()));
        for (FrontLineWorker frontLineWorker : frontLineWorkerList) {
            frontLineWorker.updateLocation(newLocation);
            allFrontLineWorkers.updateFlw(frontLineWorker);
        }
        return frontLineWorkerList;
    }

    public void updateLastFailedRecordsProcessedDate(DateTime recordDate) {
        List<FailedRecordsProcessingState> failedRecordsProcessingStates = allFailedRecordsProcessingStates.getAll();

        if (failedRecordsProcessingStates.isEmpty()) {
            allFailedRecordsProcessingStates.add(new FailedRecordsProcessingState(recordDate));
            log.info("Added last processed date:" + recordDate);
        } else {
            FailedRecordsProcessingState failedRecordsProcessingState = failedRecordsProcessingStates.get(0);
            failedRecordsProcessingState.update(recordDate);
            allFailedRecordsProcessingStates.update(failedRecordsProcessingState);
            log.info("Updated last processed date:" + recordDate);
        }
    }

    public void changeMsisdn(String msisdn, String newMsisdn) {
        FrontLineWorker flwByNewMsisdn = allFrontLineWorkers.findByMsisdn(newMsisdn);
        if (flwByNewMsisdn != null) {
            allFrontLineWorkers.remove(flwByNewMsisdn);
        }
        FrontLineWorker flwByOldMsisdn = allFrontLineWorkers.findByMsisdn(msisdn);
        FlwChangeSelector changeSelector = new FlwChangeSelector(flwByOldMsisdn, flwByNewMsisdn);
        setFlwFields(newMsisdn, changeSelector, flwByOldMsisdn);
        allFrontLineWorkers.update(flwByOldMsisdn);
    }

    private void setFlwFields(String newMsisdn, FlwChangeSelector changeSelector, FrontLineWorker flwByOldMsisdn) {
        flwByOldMsisdn.setMsisdn(newMsisdn);
        flwByOldMsisdn.setOperator(changeSelector.getTheLatestOperator());
        flwByOldMsisdn.setReportCard(changeSelector.getHighestReportCard());
        flwByOldMsisdn.setBookMark(changeSelector.getHighestBookMark());
        flwByOldMsisdn.setCurrentJobAidUsage(changeSelector.getTheLatestJobAidUsage());
        flwByOldMsisdn.setLastJobAidAccessTime(changeSelector.getTheLatestLastJobAidAccessTime());
        flwByOldMsisdn.setCertificateCourseAttempts(changeSelector.getLatestCourseAttempt());
    }
}
