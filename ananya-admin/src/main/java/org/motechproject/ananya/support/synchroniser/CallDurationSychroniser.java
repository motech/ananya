package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.service.measure.CallDurationMeasureService;
import org.motechproject.ananya.service.CallLogService;
import org.motechproject.ananya.support.synchroniser.base.Priority;
import org.motechproject.ananya.support.synchroniser.base.Synchroniser;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class CallDurationSychroniser extends BaseSynchronizer implements Synchroniser {

    private CallLogService callLoggerService;
    private CallDurationMeasureService callDurationMeasureService;

    @Autowired
    public CallDurationSychroniser(CallLogService callLoggerService,
                                   CallDurationMeasureService callDurationMeasureService,
                                   @Qualifier("ananyaProperties") Properties properties) {
        this.callLoggerService = callLoggerService;
        this.callDurationMeasureService = callDurationMeasureService;
        this.properties = properties;
    }

    @Override
    public SynchroniserLog replicate() {
        SynchroniserLog synchroniserLog = new SynchroniserLog("CallDuration");
        List<CallLog> callLogs = callLoggerService.getAll();
        for (CallLog callLog : callLogs) {
            try {
                if(!shouldProcessLog(callLog)) continue;
                callDurationMeasureService.createFor(callLog.getCallId());
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
