package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.CallDuration;
import org.motechproject.ananya.domain.CallEvent;
import org.motechproject.ananya.domain.CallFlow;
import org.motechproject.ananya.domain.CallLog;
import org.motechproject.ananya.repository.AllCallLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CallLoggerService {
    private AllCallLogs allCallLogs;

    @Autowired
    public CallLoggerService(AllCallLogs allCallLogs) {
        this.allCallLogs = allCallLogs;
    }


    public void save(CallDuration callDuration) {
        DateTime time = new DateTime(callDuration.getTime());
        if( callDuration.getCallEvent() == CallEvent.DISCONNECT){
            HandleDisconnect(callDuration, time);

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


        CallFlow callFlow;

        try
        {
            callFlow = CallFlow.valueOf(split[0]);
        }
        catch(IllegalArgumentException ex)
        {
            return;
        }

        if(split[1].equalsIgnoreCase("start")) startTime = time;
        else if(split[1].equalsIgnoreCase("end")) endTime = time;
        else return;

        CallLog callLog = new CallLog(callDuration.getCallId(), callDuration.getCallerId(), callFlow, startTime, endTime);
        allCallLogs.addOrUpdate(callLog);
    }

}
