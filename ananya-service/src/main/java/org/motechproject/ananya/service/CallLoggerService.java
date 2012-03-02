package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllCallLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CallLoggerService {
    private AllCallLogs allCallLogs;
    private ReportDataPublisher reportPublisher;

    @Autowired
    public CallLoggerService(AllCallLogs allCallLogs, ReportDataPublisher reportDataPublisher) {
        this.allCallLogs = allCallLogs;
        this.reportPublisher = reportDataPublisher;
    }


    public void save(CallDuration callDuration) {
        DateTime time = new DateTime(callDuration.getTime());
        if( callDuration.getCallEvent() == CallEvent.DISCONNECT){
            HandleDisconnect(callDuration, time);
            reportPublisher.publishCallDuration(new LogData(LogType.CALL_DURATION, callDuration.getCallId()));
            return;
        }
        HandleNormalFlow(callDuration, time);
    }

    private void HandleDisconnect(CallDuration callDuration, DateTime time) {

        Collection<CallLog> allCallLogsByCallId = allCallLogs.findByCallId(callDuration.getCallId());
        for(CallLog log : allCallLogsByCallId){
            if(log.getEndTime()==null) {
                log.setEndTime(time);
                allCallLogs.addOrUpdate(log);
            }
        }
    }

    private void HandleNormalFlow(CallDuration callDuration, DateTime time) {
        DateTime startTime = null, endTime = null;
        String[] split = callDuration.getCallEvent().toString().split("_");
        IvrFlow ivrFlow;

        try
        {
            ivrFlow = IvrFlow.valueOf(split[0]);
        }
        catch(IllegalArgumentException ex)
        {
            return;
        }

        if(split[1].equalsIgnoreCase("start")) startTime = time;
        else if(split[1].equalsIgnoreCase("end")) endTime = time;
        else return;

        CallLog callLog = new CallLog(callDuration.getCallId(), callDuration.getCallerId(), ivrFlow, startTime, endTime);
        allCallLogs.addOrUpdate(callLog);
    }

    public Collection<CallLog> getAllCallLogs(String callId) {
        return allCallLogs.findByCallId(callId);
    }

    public void delete(Collection<CallLog> callLogs) {
        allCallLogs.delete(callLogs);
    }
}
