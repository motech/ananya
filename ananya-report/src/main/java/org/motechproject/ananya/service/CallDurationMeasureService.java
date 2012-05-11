package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.CallLogItem;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.LocationDimension;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.domain.measure.RegistrationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.ananya.repository.measure.AllRegistrationMeasures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CallDurationMeasureService {
    private static Logger log = LoggerFactory.getLogger(CallDurationMeasureService.class);

    private CallLoggerService callLoggerService;
    private ReportDB reportDB;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;
    private AllRegistrationMeasures allRegistrationMeasures;
    private AllTimeDimensions allTimeDimensions;

    public CallDurationMeasureService() {
    }

    @Autowired
    public CallDurationMeasureService(CallLoggerService callLoggerService,
                                      ReportDB reportDB,
                                      AllFrontLineWorkerDimensions allFrontLineWorkerDimensions,
                                      AllRegistrationMeasures allRegistrationMeasures, AllTimeDimensions allTimeDimensions) {
        this.callLoggerService = callLoggerService;
        this.reportDB = reportDB;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
        this.allRegistrationMeasures = allRegistrationMeasures;
        this.allTimeDimensions = allTimeDimensions;
    }



    @Transactional
    public void createCallDurationMeasure(String callId) {
        CallLog callLog = callLoggerService.getCallLogFor(callId);

        if (callLog.getCallLogItems().size() == 0) {
            callLoggerService.delete(callLog);
            return;
        }

        Long callerId = callLog.callerIdAsLong();
        Long calledNumber = callLog.calledNumberAsLong();

        FrontLineWorkerDimension flwDimension = allFrontLineWorkerDimensions.fetchFor(callerId);
        RegistrationMeasure registrationMeasure = allRegistrationMeasures.fetchFor(flwDimension.getId());
        LocationDimension locationDimension = registrationMeasure.getLocationDimension();
        TimeDimension timeDimension = allTimeDimensions.getFor(callLog.getCallLogItems().get(0).getStartTime());

        for (CallLogItem callLogItem : callLog.getCallLogItems()) {
            if (callLogItem.getStartTime() == null || callLogItem.getEndTime() == null)
                continue;

            CallDurationMeasure callDurationMeasure = new CallDurationMeasure(
                    flwDimension,
                    locationDimension,
                    timeDimension,
                    callId,
                    calledNumber,
                    callLogItem.duration(),
                    callLogItem.getStartTime(),
                    callLogItem.getEndTime(),
                    callLogItem.getCallFlowType().name());
            reportDB.add(callDurationMeasure);
        }
        callLoggerService.delete(callLog);
        log.info("Added CallDurationMeasures for callId=" + callId);
    }
}
