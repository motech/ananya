package org.motechproject.ananya.repository;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AllTimeDimensions {

    @Autowired
    private DataAccessTemplate template;

    public TimeDimension getTimeDimension(DateTime dateTime) {
        TimeDimension timeDimension = (TimeDimension) template.getUniqueResult(
                TimeDimension.FIND_BY_DAY_MONTH_YEAR, new String[]{"dateTime"}, new Object[]{dateTime});
        return timeDimension != null ? timeDimension : new TimeDimension(dateTime);
    }

    public TimeDimension fetchTimeDimensionFromDB(DateTime dateTime) {
        return (TimeDimension) template.getUniqueResult(
            TimeDimension.FIND_BY_DAY_MONTH_YEAR, new String[]{"dateTime"}, new Object[]{dateTime});
    }
}
