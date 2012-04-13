package org.motechproject.ananya.support.synchroniser;

import org.joda.time.DateTime;
import org.motechproject.ananya.support.log.SynchroniserLog;

public interface Synchroniser {

    SynchroniserLog replicate(DateTime fromDate, DateTime toDate);

}
