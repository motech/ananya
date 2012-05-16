package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.repository.AllSMSLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMSLogService {
    private AllSMSLogs allSendSMSLogs;

    @Autowired
    public SMSLogService(AllSMSLogs allSendSMSLogs) {
        this.allSendSMSLogs = allSendSMSLogs;
    }

    public void add(SMSLog SMSLog) {
        allSendSMSLogs.add(SMSLog);
    }

    public SMSLog getSMSLogFor(String callId) {
        return allSendSMSLogs.findByCallId(callId);
    }

    public void deleteFor(SMSLog SMSLog) {
        allSendSMSLogs.remove(SMSLog);
    }
}
