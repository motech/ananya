package org.motechproject.ananya.repository.dimension;

import org.joda.time.DateTime;
import org.motechproject.ananya.domain.dimension.TimeDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllTimeDimensions {

    @Autowired
    private DataAccessTemplate template;

    public AllTimeDimensions() {
    }

    public TimeDimension makeFor(DateTime dateTime) {
        TimeDimension timeDimension = new TimeDimension(dateTime);
        template.save(timeDimension);
        return timeDimension;
    }

    public TimeDimension addOrUpdate(DateTime dateTime) {
        TimeDimension existingTimeDimension = getFor(dateTime);
        if (existingTimeDimension == null) {
            TimeDimension timeDimension = new TimeDimension(dateTime);
            template.save(timeDimension);
            return timeDimension;
        }
        return existingTimeDimension;
    }

    public void update(TimeDimension timeDimension) {
        template.update(timeDimension);
    }

    @Cacheable(value = "timeDimensionCache")
    public TimeDimension getFor(DateTime dateTime) {
        return (TimeDimension) template.getUniqueResult(
                TimeDimension.FIND_BY_DAY_MONTH_YEAR,
                new String[]{"year", "month", "day"},
                new Object[]{dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfYear()});
    }

    @CacheEvict(value = "timeDimensionCache", allEntries = true)
    public void invalidateCache() {
    }
}
