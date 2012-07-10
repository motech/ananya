package org.motechproject.ananya.seed;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.BaseLog;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.domain.SMSLog;
import org.motechproject.ananya.repository.AllRegistrationLogs;
import org.motechproject.ananya.repository.AllSMSLogs;
import org.motechproject.ananya.seed.service.SMSSeedService;
import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class VodafoneDataCorrectionSeed {

    private static final Logger log = LoggerFactory.getLogger(VodafoneDataCorrectionSeed.class);

    private AllRegistrationLogs allRegistrationLogs;

    private AllSMSLogs allSMSLogs;

    private SMSSeedService smsSeedService;

    @Autowired
    public VodafoneDataCorrectionSeed(AllRegistrationLogs allRegistrationLogs, AllSMSLogs allSMSLogs, SMSSeedService smsSeedService) {
        this.allRegistrationLogs = allRegistrationLogs;
        this.allSMSLogs = allSMSLogs;
        this.smsSeedService = smsSeedService;
    }

    @Seed(priority = 0, version = "1.4", comment = "update registrationLogs sent for Vodafone w/o callIds after July7th release.Refer bug#122")
    public void correctRegistrationLogsWithMissingCallIds() throws NoSuchFieldException {
        Long time = DateUtil.newDateTime(2012, 7, 7).withHourOfDay(1).getMillis();

        Field callIdField = BaseLog.class.getDeclaredField("callId");
        callIdField.setAccessible(true);

        List<RegistrationLog> registrationLogs = allRegistrationLogs.getAll();
        for (RegistrationLog registrationLog : registrationLogs) {
            if (StringUtils.isNotBlank(registrationLog.getCallId())) continue;
            String callerId = registrationLog.getCallerId();
            ReflectionUtils.setField(callIdField, registrationLog, callerId + "-" + time);
            allRegistrationLogs.update(registrationLog);
            log.info("corrected registration log for " + callerId);

        }
    }

    @Seed(priority = 0, version = "1.5", comment = "send sms and create measure for pending smsLogs after July7th. Refer bug #122")
    public void correctSMSLogs() {
        List<SMSLog> smsLogs = allSMSLogs.getAll();
        for (SMSLog smslog : smsLogs) {
            smsSeedService.buildAndSendSMS(smslog.getCallerId(), smslog.getLocationId(), smslog.getCourseAttempts());
            log.info("sent SMS and created measure for " + smslog.getCallerId());
            allSMSLogs.remove(smslog);
        }
    }

}

