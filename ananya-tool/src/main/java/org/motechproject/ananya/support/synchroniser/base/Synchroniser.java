package org.motechproject.ananya.support.synchroniser.base;

import org.joda.time.DateTime;

public interface Synchroniser {

    SynchroniserLog replicate(DateTime fromDate, DateTime toDate);

    Priority runPriority();

}
