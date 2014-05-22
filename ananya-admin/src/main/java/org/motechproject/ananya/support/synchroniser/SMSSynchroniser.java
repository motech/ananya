package org.motechproject.ananya.support.synchroniser;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.repository.AllSMSLogs;
import org.motechproject.ananya.support.synchroniser.base.Priority;
import org.motechproject.ananya.support.synchroniser.base.Synchroniser;
import org.motechproject.ananya.support.synchroniser.base.SynchroniserLog;
import org.motechproject.ananya.support.synchroniser.service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class SMSSynchroniser extends BaseSynchronizer implements Synchroniser {

    private AllSMSLogs allSMSLogs;
    private SMSService smsService;

    @Autowired
    public SMSSynchroniser(AllSMSLogs allSMSLogs, SMSService smsService,
                           @Qualifier("ananyaProperties") Properties properties) {
        this.allSMSLogs = allSMSLogs;
        this.smsService = smsService;
        this.properties = properties;
    }

    @Override
    public SynchroniserLog replicate() {
        SynchroniserLog synchroniserLog = new SynchroniserLog("SMS");
        List<SMSLog> smsLogs = allSMSLogs.getAll();
        for (SMSLog smslog : smsLogs) {
            try {
                if(!shouldProcessLog(smslog)) continue;
                smsService.buildAndSendSMS(smslog.getCallerId(), smslog.getLanguage(), smslog.getLocationId(), smslog.getCourseAttempts());
                synchroniserLog.add(smslog.getCallerId(), "Success");
                allSMSLogs.remove(smslog);
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
