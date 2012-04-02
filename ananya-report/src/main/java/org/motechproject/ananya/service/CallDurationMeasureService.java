package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.CallLogList;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallDurationMeasureService {
    private static Logger log = LoggerFactory.getLogger(CallDurationMeasureService.class);

    private CallLoggerService callLoggerService;
    private ReportDB reportDB;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    public CallDurationMeasureService(CallLoggerService callLoggerService, ReportDB reportDB, AllFrontLineWorkerDimensions allFrontLineWorkerDimensions) {
        this.callLoggerService = callLoggerService;
        this.reportDB = reportDB;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
    }

    public void createCallDurationMeasure(String callId) {
        CallLogList callLogsList = callLoggerService.getCallLogList(callId);

        for (CallLog callLog : callLogsList.getCallLogs()) {
            if (callLog.getStartTime() == null || callLog.getEndTime() == null)
                continue;

            Long callerId = callLogsList.callerIdAsLong();
            FrontLineWorkerDimension flwDimension = allFrontLineWorkerDimensions.fetchFor(callerId);

            CallDurationMeasure callDurationMeasure = new CallDurationMeasure(
                    flwDimension,
                    callId,
                    callLog.duration(),
                    callLog.getCallFlowType().name());
            reportDB.add(callDurationMeasure);
        }
        callLoggerService.delete(callLogsList);
        log.info("Added CallDurationMeasures for callId=" + callId);
    }
}
