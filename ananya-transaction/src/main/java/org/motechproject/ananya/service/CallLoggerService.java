package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllCallLogList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CallLoggerService {
    private static Logger log = LoggerFactory.getLogger(CallLoggerService.class);

    private AllCallLogList allCallLogs;

    @Autowired
    public CallLoggerService(AllCallLogList allCallLogs) {
        this.allCallLogs = allCallLogs;
    }

    public void saveAll(CallDurationList callDurationList) {
        CallLogList callLogList = new CallLogList(callDurationList.getCallId(), callDurationList.getCallerId());
        for (CallDuration callDuration : callDurationList.all())
            save(callDuration, callLogList);
        allCallLogs.add(callLogList);
        log.info("Saved call duration logs");
    }

    public CallLogList getCallLogList(String callId) {
        return allCallLogs.findByCallId(callId);
    }

    public void delete(CallLogList callLogList) {
        allCallLogs.remove(callLogList);
    }

    private void save(CallDuration callDuration, CallLogList callLogList) {
        DateTime time = new DateTime(callDuration.getTime());
        if (callDuration.isDisconnect()) {
            handleDisconnect(time, callLogList);
            return;
        }
        handleNormalFlow(callDuration, time, callLogList);
    }

    private void handleDisconnect(DateTime endTime, CallLogList callLogList) {
        List<CallLog> allCallLogsByCallId = callLogList.getCallLogs();
        for (CallLog log : allCallLogsByCallId) {
            if (log.getEndTime() == null) {
                log.setEndTime(endTime);
            }
        }
    }

    private void handleNormalFlow(CallDuration callDuration, DateTime time, CallLogList callLogList) {
        DateTime startTime = null, endTime = null;
        String[] split = callDuration.getCallEvent().toString().split("_");
        CallFlowType callFlowType;
        try {
            callFlowType = CallFlowType.valueOf(split[0]);
        } catch (IllegalArgumentException ex) {
            return;
        }
        if (split[1].equalsIgnoreCase("start")) startTime = time;
        else if (split[1].equalsIgnoreCase("end")) endTime = time;
        else return;

        CallLog callLog = new CallLog(callFlowType, startTime, endTime);
        callLogList.addOrUpdate(callLog);
    }
}
