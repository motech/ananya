package org.motechproject.ananya.seed;

import org.joda.time.LocalDate;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.repository.dimension.AllTimeDimensions;
import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TimeSeed {

    @Autowired
    private AllTimeDimensions allTimeDimensions;

    @Seed(priority = 2, version = "1.0")
    public void load() {
        LocalDate startDate = DateUtil.newDate(2012, 1, 1);
        LocalDate endDate = DateUtil.newDate(2014, 1, 1);

        while (DateUtil.isOnOrBefore(startDate.toDateTimeAtStartOfDay(),
                endDate.toDateTimeAtStartOfDay())) {
            allTimeDimensions.addOrUpdate(startDate.toDateTimeAtCurrentTime());
            startDate = startDate.plusDays(1);
        }
    }

    @Seed(priority = 2, version = "1.1")
    public void updateDateField() {
        LocalDate startDate = DateUtil.newDate(2012, 1, 1);
        LocalDate endDate = DateUtil.newDate(2014, 1, 1);

        while (DateUtil.isOnOrBefore(startDate.toDateTimeAtStartOfDay(),
                endDate.toDateTimeAtStartOfDay())) {
            TimeDimension timeDimension = allTimeDimensions.getFor(startDate.toDateTimeAtCurrentTime());
            timeDimension.setDate(startDate.toDate());
            allTimeDimensions.update(timeDimension);
            startDate = startDate.plusDays(1);
        }
    }

}
