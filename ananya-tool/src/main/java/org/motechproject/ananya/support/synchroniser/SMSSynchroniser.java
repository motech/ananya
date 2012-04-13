package org.motechproject.ananya.support.synchroniser;

import org.joda.time.DateTime;
import org.motechproject.ananya.support.log.SynchroniserLog;
import org.springframework.stereotype.Component;

@Component
public class SMSSynchroniser implements Synchroniser {

    @Override
    public SynchroniserLog replicate(DateTime fromDate, DateTime toDate) {
        return new SynchroniserLog("SMS");
    }
}
