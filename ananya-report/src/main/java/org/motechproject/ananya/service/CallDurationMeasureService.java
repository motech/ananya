package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.motechproject.ananya.domain.CallFlow;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.ReportDB;
import org.motechproject.ananya.repository.dimension.AllFrontLineWorkerDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CallDurationMeasureService {
    private CallLoggerService callLoggerService;
    private ReportDB reportDB;
    private AllFrontLineWorkerDimensions allFrontLineWorkerDimensions;

    @Autowired
    public CallDurationMeasureService(CallLoggerService callLoggerService, ReportDB reportDB, AllFrontLineWorkerDimensions allFrontLineWorkerDimensions) {
        this.callLoggerService = callLoggerService;
        this.reportDB = reportDB;
        this.allFrontLineWorkerDimensions = allFrontLineWorkerDimensions;
    }

    public void createCallDurationMeasure(String callId){
        Collection<CallLog> allCallLogs = callLoggerService.getAllCallLogs(callId);
        for(CallLog callLog: allCallLogs){
            if( callLog.getStartTime() == null || callLog.getEndTime() == null){
                continue;
            }
            int duration = Seconds.secondsBetween(callLog.getStartTime(), callLog.getEndTime()).getSeconds();
            Long msisdn = Long.valueOf(callLog.getCallerId());
            FrontLineWorkerDimension frontLineWorkerDimension = allFrontLineWorkerDimensions.fetchFor(msisdn);
            if(frontLineWorkerDimension == null){
                frontLineWorkerDimension = allFrontLineWorkerDimensions.getOrMakeFor(msisdn,callLog.getOperator(),"","");
            }
            reportDB.add(new CallDurationMeasure(frontLineWorkerDimension, callId, duration, callLog.getCallFlow().name()));
        }
        callLoggerService.delete(allCallLogs);
    }
}
