package org.motechproject.ananya.service;

import org.motechproject.ananya.domain.SendSMSLog;
import org.motechproject.ananya.repository.AllSendSMSLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendSMSLogService {
    private AllSendSMSLogs allSendSMSLogs;

    @Autowired
    public SendSMSLogService(AllSendSMSLogs allSendSMSLogs) {
        this.allSendSMSLogs = allSendSMSLogs;
    }

    public void add(SendSMSLog sendSMSLog) {
        allSendSMSLogs.add(sendSMSLog);
    }

    public SendSMSLog sendSMSLogFor(String callerId) {
        return allSendSMSLogs.findByCallerId(callerId);
    }

    public void deleteFor(String callerId) {
        allSendSMSLogs.deleteFor(callerId);
    }
}
