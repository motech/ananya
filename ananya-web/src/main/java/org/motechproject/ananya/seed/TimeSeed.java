package org.motechproject.ananya.seed;

import org.joda.time.LocalDate;
import org.motechproject.ananya.repository.AllTimeDimensions;
import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TimeSeed {

    @Autowired
    private AllTimeDimensions allTimeDimensions;

    @Seed(priority = 0)
    public void load() {
        LocalDate startDate = DateUtil.newDate(2012, 1, 1);
        LocalDate endDate = DateUtil.newDate(2014, 1, 1);

        while (DateUtil.isOnOrBefore(startDate, endDate)) {
            allTimeDimensions.getOrMakeFor(startDate.toDateTimeAtCurrentTime());
            startDate = startDate.plusDays(1);
        }
    }

}
