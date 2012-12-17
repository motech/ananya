package org.motechproject.ananya.support.diagnostics.support.synchroniser;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ananya.domain.BaseLog;

import java.util.Properties;

public class BaseSynchronizer {

    protected Properties properties;

    public boolean shouldProcessLog(BaseLog log){
        LocalDate logTime = new DateTime(Long.parseLong(log.getCallId().split("-")[1])).toLocalDate();
        return logTime.isBefore(
                DateTime.now().minusDays(
                        Integer.parseInt(properties.getProperty("synchroniser.log.delta.days"))).toLocalDate());
    }
}
