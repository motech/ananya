package org.motechproject.ananya.service;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.*;
import org.motechproject.ananya.repository.AllCallLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CallLoggerService {
    private static Logger log = LoggerFactory.getLogger(CallLoggerService.class);

    private AllCallLogs allCallLogs;

    @Autowired
    public CallLoggerService(AllCallLogs allCallLogs) {
        this.allCallLogs = allCallLogs;
    }

    public void saveAll(CallDurationList callDurationList) {
        CallLog callLog = new CallLog(callDurationList.getCallId(), callDurationList.getCallerId(), callDurationList.getCalledNumber());
        for (CallDuration callDuration : callDurationList.all())
            save(callDuration, callLog);
        allCallLogs.add(callLog);
        log.info("Saved call duration logs");
    }

    public CallLog getCallLogFor(String callId) {
        return allCallLogs.findByCallId(callId);
    }

    public void delete(CallLog callLog) {
        allCallLogs.remove(callLog);
    }

    private void save(CallDuration callDuration, CallLog callLog) {
        DateTime time = new DateTime(callDuration.getTime());
        if (callDuration.isDisconnect()) {
            handleDisconnect(time, callLog);
            return;
        }
        handleNormalFlow(callDuration, time, callLog);
    }

    private void handleDisconnect(DateTime endTime, CallLog callLog) {
        List<CallLogItem> callLogItems = callLog.getCallLogItems();
        for (CallLogItem log : callLogItems) {
            if (log.getEndTime() == null) {
                log.setEndTime(endTime);
            }
        }
    }

    private void handleNormalFlow(CallDuration callDuration, DateTime time, CallLog callLog) {
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

        CallLogItem callLogItem = new CallLogItem(callFlowType, startTime, endTime);
        callLog.addOrUpdate(callLogItem);
    }

    public List<CallLog> getAll() {
        return allCallLogs.getAll();
    }

    public void removeAll() {
        allCallLogs.removeAll();
    }
}
