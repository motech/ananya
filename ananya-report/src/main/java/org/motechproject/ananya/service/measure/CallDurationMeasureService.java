package org.motechproject.ananya.service.measure;

import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.CallLogItem;
import org.motechproject.ananya.service.measure.response.CallDetailsResponse;
import org.motechproject.ananya.domain.CallUsageDetails;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllCallDurationMeasures;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.motechproject.ananya.service.CallLogService;
import org.motechproject.ananya.service.dimension.LocationDimensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CallDurationMeasureService {
    private static Logger log = LoggerFactory.getLogger(CallDurationMeasureService.class);

    private CallLogService callLoggerService;
    private AllCallDurationMeasures allCallDurationMeasures;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;
    private AllTimeDimensions allTimeDimensions;
    private LocationDimensionService locationDimensionService;

    public CallDurationMeasureService() {
    }

    @Autowired
    public CallDurationMeasureService(CallLogService callLoggerService,
                                      AllCallDurationMeasures allCallDurationMeasures,
                                      AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                      AllRegistrationMeasures allRegistrationMeasures,
                                      AllTimeDimensions allTimeDimensions,
                                      LocationDimensionService locationDimensionService) {
        this.callLoggerService = callLoggerService;
        this.allCallDurationMeasures = allCallDurationMeasures;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allRegistrationMeasures = allRegistrationMeasures;
        this.allTimeDimensions = allTimeDimensions;
        this.locationDimensionService = locationDimensionService;
    }


    @Transactional
    public void createFor(String callId) {
        CallLog callLog = callLoggerService.getCallLogFor(callId);

        if (callLog == null) {
            log.info(callId + "- callLog not present");
            return;
        }
        if (callLog.hasNoItems()) {
            log.info(callId + "- callLog has no items");
            removeLog(callId, callLog);
            return;
        }

        Long callerId = callLog.callerIdAsLong();
        Long calledNumber = callLog.calledNumberAsLong();
        FrontLineWorkerDimension flwDimension = allFrontLineWorkerDimensions.fetchFor(callerId);
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(flwDimension.getId());
        TimeDimension timeDimension = allTimeDimensions.getFor(callLog.startTime());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();

        for (CallLogItem callLogItem : callLog.getCallLogItems()) {
            if (callLogItem.hasNoTimeLimits()) continue;

            CallDurationMeasure callDurationMeasure = new CallDurationMeasure(
                    flwDimension, locationDimension, timeDimension,
                    callId, calledNumber,
                    callLogItem.duration(), callLogItem.getStartTime(),
                    callLogItem.getEndTime(), callLogItem.getCallFlowType().name());
            allCallDurationMeasures.add(callDurationMeasure);
        }
        log.info(callId + "- callLog callDurationMeasures added");
        removeLog(callId, callLog);
    }

    public void updateLocation(Long callerId, String locationId) {
        List<CallDurationMeasure> callDurationMeasureList = allCallDurationMeasures.findByCallerId(callerId);
        LocationDimension locationDimension = locationDimensionService.getFor(locationId);

        for (CallDurationMeasure callDurationMeasure : callDurationMeasureList) {
            callDurationMeasure.setLocationDimension(locationDimension);
        }
        allCallDurationMeasures.updateAll(callDurationMeasureList);
    }

    public CallDetailsResponse getCallDetails(String msisdn) {
        Long msisdnInLong = Long.valueOf(msisdn);
        List<CallUsageDetails> callUsageDetailsList = allCallDurationMeasures.getCallUsageDetailsByMonthAndYear(msisdnInLong);
        List<CallDurationMeasure> recentJobAidCallDetailsList = allCallDurationMeasures.getRecentJobAidCallDetails(msisdnInLong);
        List<CallDurationMeasure> recentCertificateCourseCallDetailsList = allCallDurationMeasures.getRecentCertificateCourseCallDetails(msisdnInLong);

        return new CallDetailsResponse(callUsageDetailsList, recentJobAidCallDetailsList, recentCertificateCourseCallDetailsList);
    }

    private void removeLog(String callId, CallLog callLog) {
        callLoggerService.delete(callLog);
        log.info(callId + "- callLog removed");
    }
}
