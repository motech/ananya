package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.repository.AllSMSLogs;
import org.motechproject.ananya.seed.service.SMSSeedService;
import org.motechproject.ananya.support.synchroniser.base.Priority;
import org.motechproject.ananya.support.synchroniser.base.Synchroniser;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SMSSynchroniser implements Synchroniser {

    private AllSMSLogs allSMSLogs;
    private SMSSeedService smsSeedService;

    @Autowired
    public SMSSynchroniser(AllSMSLogs allSMSLogs, SMSSeedService smsSeedService) {
        this.allSMSLogs = allSMSLogs;
        this.smsSeedService = smsSeedService;
    }

    @Override
    public SynchroniserLog replicate() {
        SynchroniserLog synchroniserLog = new SynchroniserLog("SMS");
        List<SMSLog> smsLogs = allSMSLogs.getAll();
        for (SMSLog smslog : smsLogs) {
            try {
                smsSeedService.buildAndSendSMS(smslog.getCallerId(), smslog.getLocationId(), smslog.getCourseAttempts());
                synchroniserLog.add(smslog.getCallerId(), "Success");
            } catch (Exception e) {
                synchroniserLog.add(smslog.getCallerId(), "Error:" + ExceptionUtils.getFullStackTrace(e));
            }
        }
        return synchroniserLog;
    }

    @Override
    public Priority runPriority() {
        return Priority.medium;
    }
}
