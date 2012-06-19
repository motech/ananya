package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.CallLoggerService;
import org.motechproject.ananya.support.synchroniser.base.Priority;
import org.motechproject.ananya.support.synchroniser.base.Synchroniser;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CallDurationSychroniser implements Synchroniser {

    private CallLoggerService callLoggerService;
    private CallDurationMeasureService callDurationMeasureService;

    @Autowired
    public CallDurationSychroniser(CallLoggerService callLoggerService,
                                   CallDurationMeasureService callDurationMeasureService) {
        this.callLoggerService = callLoggerService;
        this.callDurationMeasureService = callDurationMeasureService;
    }

    @Override
    public SynchroniserLog replicate() {
        SynchroniserLog synchroniserLog = new SynchroniserLog("CallDuration");
        List<CallLog> callLogs = callLoggerService.getAll();
        for (CallLog callLog : callLogs) {
            try {
                callDurationMeasureService.createCallDurationMeasure(callLog.getCallId());
                synchroniserLog.add(callLog.getCallId(), "Success");
            } catch (Exception e) {
                synchroniserLog.add(callLog.getCallId(), "Error:" + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return synchroniserLog;
    }

    @Override
    public Priority runPriority() {
        return Priority.low;
    }
}
