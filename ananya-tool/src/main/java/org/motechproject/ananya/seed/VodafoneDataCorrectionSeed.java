package org.motechproject.ananya.seed;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.BaseLog;
import org.motechproject.ananya.domain.RegistrationLog;
import org.motechproject.ananya.repository.AllRegistrationLogs;
import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@Service
public class VodafoneDataCorrectionSeed {

    private AllRegistrationLogs allRegistrationLogs;

    @Autowired
    public VodafoneDataCorrectionSeed(AllRegistrationLogs allRegistrationLogs) {
        this.allRegistrationLogs = allRegistrationLogs;
    }

    @Seed(priority = 0, version = "1.4", comment = "update registrationLogs sent for Vodafone w/o callIds after July7th release refer bug#122, " +
            "reflection used as setter not present in given build")
    public void correctRegistrationLogsWithMissingCallIds() throws NoSuchFieldException {

        Long time = DateUtil.newDateTime(2012, 7, 7).withHourOfDay(1).getMillis();

        Field callIdField = BaseLog.class.getDeclaredField("callId");
        callIdField.setAccessible(true);

        List<RegistrationLog> registrationLogs = allRegistrationLogs.getAll();
        for (RegistrationLog registrationLog : registrationLogs) {
            if (StringUtils.isNotBlank(registrationLog.getCallId())) continue;
            ReflectionUtils.setField(callIdField, registrationLog, registrationLog.getCallerId() + "-" + time);
            allRegistrationLogs.update(registrationLog);
        }
    }
}